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
                    val species = nsDir.resolve("species")
                    if (!Files.isDirectory(species)) continue
                    val ns = nsDir.fileName.toString().trim('/')
                    Files.walk(species).use { walk ->
                        walk.filter { it.extension == "json" }.forEach { f ->
                            runCatching {
                                val obj = Files.newBufferedReader(f).use { JsonParser.parseReader(it).asJsonObject }
                                index("$ns:${f.nameWithoutExtension}", obj, moves, evos)
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
                val m = if (!e.isDirectory) speciesEntry.matchEntire(e.name) else null
                if (m != null) {
                    val bytes = zin.readBytes()
                    runCatching {
                        val obj = JsonParser.parseString(bytes.decodeToString()).asJsonObject
                        index("${m.groupValues[1]}:${m.groupValues[2]}", obj, moves, evos)
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
                val item = if (variant == "item_interact") ctx else null
                val (key, level) = method(variant, item, o.getAsJsonArray("requirements"))
                evos.add(EvoEdge(id, aspects, item, result, key, level))
            }
        }
    }

    private fun method(variant: String, item: String?, reqs: JsonArray?): Pair<String, Int?> {
        var level: Int? = null
        var friendship = false
        reqs?.forEach { r ->
            val o = r as? JsonObject ?: return@forEach
            when (o.get("variant")?.asString) {
                "level" -> level = (o.get("minLevel") ?: o.get("amount"))?.asInt
                "friendship" -> friendship = true
            }
        }
        val key = when {
            variant == "item_interact" && item != null -> "item"
            variant == "trade" -> "trade"
            level != null -> "level"
            friendship -> "friendship"
            variant == "level_up" -> "levelup"
            else -> variant
        }
        return key to level
    }
}
