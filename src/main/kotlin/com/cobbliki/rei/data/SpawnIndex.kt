package com.cobbliki.rei.data

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipInputStream
import kotlin.io.path.extension

data class SpawnInfo(
    val bucket: String,
    val level: String?,
    val weight: Double,
    val context: String?,
    val presets: List<String>,
    val biomes: List<String>,
    val structures: List<String>,
    val dimensions: List<String>,
    val timeRange: String?,
    val moonPhase: String?,
    val canSeeSky: Boolean?,
    val isRaining: Boolean?,
    val isThundering: Boolean?,
    val minLight: Int?,
    val maxLight: Int?,
    val minSkyLight: Int?,
    val maxSkyLight: Int?,
    val minY: Int?,
    val maxY: Int?,
    val minDepth: Int?,
    val maxDepth: Int?,
    val neededNearbyBlocks: List<String>,
    val neededBaseBlocks: List<String>,
    val bobberBait: Boolean,
    val fluidIsSource: Boolean?,
    val labels: List<String>,
    val aspects: Set<String>,
)

object SpawnIndex {
    private val data by lazy { build() }
    private val spawnEntry = Regex("""data/[^/]+/spawn_pool_world/[^/]+\.json""")

    operator fun get(speciesId: String): List<SpawnInfo> = data[speciesId.lowercase()].orEmpty()

    private fun build(): Map<String, List<SpawnInfo>> {
        val acc = HashMap<String, LinkedHashSet<SpawnInfo>>()
        for (mod in FabricLoader.getInstance().allMods) {
            for (root in mod.rootPaths) scanRoot(root, acc)
        }
        scanDatapacks(acc)
        return acc.mapValues { it.value.toList() }
    }

    private fun scanRoot(root: Path, acc: MutableMap<String, LinkedHashSet<SpawnInfo>>) {
        val dataDir = root.resolve("data")
        if (!Files.isDirectory(dataDir)) return
        runCatching {
            Files.newDirectoryStream(dataDir).use { dirs ->
                for (nsDir in dirs) {
                    val pool = nsDir.resolve("spawn_pool_world")
                    if (!Files.isDirectory(pool)) continue
                    Files.walk(pool).use { walk ->
                        walk.filter { it.extension == "json" }.forEach { f ->
                            runCatching {
                                val obj = Files.newBufferedReader(f).use { JsonParser.parseReader(it).asJsonObject }
                                index(obj, acc)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun scanDatapacks(acc: MutableMap<String, LinkedHashSet<SpawnInfo>>) {
        val dir = FabricLoader.getInstance().gameDir.resolve("datapacks")
        if (!Files.isDirectory(dir)) return
        runCatching {
            Files.newDirectoryStream(dir, "*.zip").use { zips ->
                for (zip in zips) runCatching { scanZip(zip, acc) }
            }
        }
    }

    private fun scanZip(zip: Path, acc: MutableMap<String, LinkedHashSet<SpawnInfo>>) {
        ZipInputStream(Files.newInputStream(zip)).use { zin ->
            var e = zin.nextEntry
            while (e != null) {
                if (!e.isDirectory && spawnEntry.matchEntire(e.name) != null) {
                    val bytes = zin.readBytes()
                    runCatching { index(JsonParser.parseString(bytes.decodeToString()).asJsonObject, acc) }
                }
                e = zin.nextEntry
            }
        }
    }

    private fun index(obj: JsonObject, acc: MutableMap<String, LinkedHashSet<SpawnInfo>>) {
        if (obj.get("enabled")?.asBoolean == false) return
        obj.getAsJsonArray("spawns")?.forEach { se ->
            val s = se as? JsonObject ?: return@forEach
            if (s.get("type")?.asString?.let { it != "pokemon" } == true) return@forEach
            val pokemon = s.get("pokemon")?.asString ?: return@forEach
            val tokens = pokemon.trim().split(Regex("\\s+"))
            val speciesId = tokens.first().lowercase()
            val c = s.getAsJsonObject("condition")
            val info = SpawnInfo(
                bucket = s.get("bucket")?.asString ?: "common",
                level = s.get("level")?.asString,
                weight = s.get("weight")?.asDouble ?: 0.0,
                context = s.get("context")?.asString,
                presets = strings(s, "presets"),
                biomes = strings(c, "biomes"),
                structures = strings(c, "structures"),
                dimensions = strings(c, "dimensions"),
                timeRange = c?.get("timeRange")?.asString,
                moonPhase = c?.get("moonPhase")?.asString,
                canSeeSky = c?.boolOrNull("canSeeSky"),
                isRaining = c?.boolOrNull("isRaining"),
                isThundering = c?.boolOrNull("isThundering"),
                minLight = c?.intOrNull("minLight"),
                maxLight = c?.intOrNull("maxLight"),
                minSkyLight = c?.intOrNull("minSkyLight"),
                maxSkyLight = c?.intOrNull("maxSkyLight"),
                minY = c?.intOrNull("minY"),
                maxY = c?.intOrNull("maxY"),
                minDepth = c?.intOrNull("minDepth"),
                maxDepth = c?.intOrNull("maxDepth"),
                neededNearbyBlocks = strings(c, "neededNearbyBlocks"),
                neededBaseBlocks = strings(c, "neededBaseBlocks"),
                bobberBait = c?.has("bobberBait") == true,
                fluidIsSource = c?.boolOrNull("fluidIsSource"),
                labels = strings(c, "labels") + listOfNotNull(c?.get("label")?.asString),
                aspects = tokens.drop(1).toSet(),
            )
            acc.getOrPut(speciesId) { LinkedHashSet() }.add(info)
        }
    }

    private fun strings(o: JsonObject?, key: String): List<String> =
        o?.getAsJsonArray(key)?.map { it.asString } ?: emptyList()

    private fun JsonObject.boolOrNull(key: String): Boolean? =
        runCatching { get(key)?.asBoolean }.getOrNull()

    private fun JsonObject.intOrNull(key: String): Int? =
        runCatching { get(key)?.asInt }.getOrNull()
}
