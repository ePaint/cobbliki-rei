package com.cobbliki.rei.data

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipInputStream
import kotlin.io.path.extension
import kotlin.io.path.nameWithoutExtension

data class LearnsetIds(val tm: List<String>, val tutor: List<String>, val egg: List<String>)

data class EvoEdge(val fromId: String, val fromAspects: Set<String>, val item: String?, val resultRaw: String, val methodKey: String, val level: Int?)

object SpeciesMovesIndex {
    private val data by lazy { build() }
    private val speciesEntry = Regex("""data/([^/]+)/species/(?:.+/)?([^/]+)\.json""")
    private val additionsEntry = Regex("""data/([^/]+)/species_additions/(?:.+/)?([^/]+)\.json""")

    fun formKey(resourceId: String, aspects: Set<String>): String = "$resourceId#" + aspects.toSortedSet().joinToString(",")
    operator fun get(formKey: String): LearnsetIds? = data.first[formKey]
    fun evolutions(): List<EvoEdge> = data.second

    private fun build(): Pair<Map<String, LearnsetIds>, List<EvoEdge>> {
        val moves = HashMap<String, LearnsetIds>()
        val evos = ArrayList<EvoEdge>()
        for (mod in FabricLoader.getInstance().allMods) {
            for (root in mod.rootPaths) scanRoot(root, moves, evos)
        }
        scanDatapacks(moves, evos)
        return moves to evos
    }

    private fun scanRoot(root: Path, moves: MutableMap<String, LearnsetIds>, evos: MutableList<EvoEdge>) {
        val dataDir = root.resolve("data")
        if (!Files.isDirectory(dataDir)) return
        runCatching {
            Files.newDirectoryStream(dataDir).use { dirs ->
                for (nsDir in dirs) {
                    val ns = nsDir.fileName.toString().trim('/')
                    val species = nsDir.resolve("species")
                    if (Files.isDirectory(species)) Files.walk(species).use { walk ->
                        walk.filter { it.extension == "json" }.forEach { f ->
                            runCatching {
                                val obj = Files.newBufferedReader(f).use { JsonParser.parseReader(it).asJsonObject }
                                index("$ns:${f.nameWithoutExtension}", obj, moves, evos)
                            }
                        }
                    }
                    val additions = nsDir.resolve("species_additions")
                    if (Files.isDirectory(additions)) Files.walk(additions).use { walk ->
                        walk.filter { it.extension == "json" }.forEach { f ->
                            runCatching {
                                val obj = Files.newBufferedReader(f).use { JsonParser.parseReader(it).asJsonObject }
                                val id = obj.get("target")?.asString ?: return@runCatching
                                index(id, obj, moves, evos)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun scanDatapacks(moves: MutableMap<String, LearnsetIds>, evos: MutableList<EvoEdge>) {
        val dir = FabricLoader.getInstance().gameDir.resolve("datapacks")
        if (!Files.isDirectory(dir)) return
        runCatching {
            Files.newDirectoryStream(dir, "*.zip").use { zips ->
                for (zip in zips) runCatching { scanZip(zip, moves, evos) }
            }
        }
    }

    private fun scanZip(zip: Path, moves: MutableMap<String, LearnsetIds>, evos: MutableList<EvoEdge>) {
        ZipInputStream(Files.newInputStream(zip)).use { zin ->
            var e = zin.nextEntry
            while (e != null) {
                if (e.isDirectory) { e = zin.nextEntry; continue }
                val sm = speciesEntry.matchEntire(e.name)
                val am = if (sm == null) additionsEntry.matchEntire(e.name) else null
                if (sm != null || am != null) {
                    val bytes = zin.readBytes()
                    runCatching {
                        val obj = JsonParser.parseString(bytes.decodeToString()).asJsonObject
                        val id = if (sm != null) "${sm.groupValues[1]}:${sm.groupValues[2]}"
                                 else obj.get("target")?.asString ?: return@runCatching
                        index(id, obj, moves, evos)
                    }
                }
                e = zin.nextEntry
            }
        }
    }

    private fun index(id: String, obj: JsonObject, moves: MutableMap<String, LearnsetIds>, evos: MutableList<EvoEdge>) {
        collect(id, emptySet(), obj, moves, evos)
        obj.getAsJsonArray("forms")?.forEach { fe ->
            val form = fe as? JsonObject ?: return@forEach
            val aspects = form.getAsJsonArray("aspects")?.map { it.asString }?.toSet() ?: return@forEach
            collect(id, aspects, form, moves, evos)
        }
    }

    private fun collect(id: String, aspects: Set<String>, obj: JsonObject, moves: MutableMap<String, LearnsetIds>, evos: MutableList<EvoEdge>) {
        obj.getAsJsonArray("moves")?.let { arr ->
            val tm = LinkedHashSet<String>(); val tutor = LinkedHashSet<String>(); val egg = LinkedHashSet<String>()
            for (e in arr) {
                val s = e.asString
                val i = s.indexOf(':'); if (i < 0) continue
                val mv = s.substring(i + 1)
                when (s.substring(0, i)) {
                    "tm" -> tm.add(mv); "tutor" -> tutor.add(mv); "egg" -> egg.add(mv)
                }
            }
            moves[formKey(id, aspects)] = LearnsetIds(tm.toList(), tutor.toList(), egg.toList())
        }
        obj.getAsJsonArray("evolutions")?.let { arr ->
            for (e in arr) {
                val o = e as? JsonObject ?: continue
                val result = o.get("result")?.asString ?: continue
                val variant = o.get("variant")?.asString ?: "level_up"
                val ctx = o.get("requiredContext")?.asString?.takeUnless { it.startsWith("#") }
                val reqs = o.getAsJsonArray("requirements")
                val (key, level, held) = method(variant, reqs)
                val item = if (variant == "item_interact") ctx else held
                evos.add(EvoEdge(id, aspects, item, result, key, level))
            }
        }
    }

    private fun method(variant: String, reqs: JsonArray?): Triple<String, Int?, String?> {
        var level: Int? = null
        var friendship = false
        var held: String? = null
        reqs?.forEach { r ->
            val o = r as? JsonObject ?: return@forEach
            when (o.get("variant")?.asString) {
                "level" -> level = (o.get("minLevel") ?: o.get("amount"))?.asInt
                "friendship" -> friendship = true
                "held_item" -> held = o.get("itemCondition")?.asString?.takeUnless { it.startsWith("#") }
            }
        }
        val key = when {
            variant == "item_interact" -> "item"
            variant == "trade" -> "trade"
            level != null -> "level"
            friendship -> "friendship"
            variant == "level_up" -> "levelup"
            else -> variant
        }
        return Triple(key, level, held)
    }
}
