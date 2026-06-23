package com.cobbliki.rei.data

import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipInputStream
import kotlin.io.path.extension
import kotlin.io.path.nameWithoutExtension

data class MegaEntry(val namespace: String, val itemKey: String, val megaStone: String, val baseFormes: List<String>)

object MegaIndex {
    private val cache by lazy { build() }
    fun entries(): List<MegaEntry> = cache

    private val stoneRx = Regex(""""?megaStone"?:\s*"([^"]+)"""")
    private val evolvesRx = Regex(""""?megaEvolves"?:\s*(\[[^\]]*]|"[^"]+")""")
    private val itemsJsRx = Regex(""""?(\w+)"?:\s*\{[^{}]*?megaStone:\s*"([^"]+)"[^{}]*?megaEvolves:\s*"([^"]+)"""")
    private val heldEntry = Regex("""data/([^/]+)/showdown/held_items/([^/]+)\.js""")

    private fun build(): List<MegaEntry> {
        val byStone = LinkedHashMap<String, MegaEntry>()
        fromItemsJs().forEach { byStone[it.megaStone] = it }
        fromHeldItems().forEach { byStone[it.megaStone] = it }
        return byStone.values.toList()
    }

    private fun fromItemsJs(): List<MegaEntry> = runCatching {
        val cob = FabricLoader.getInstance().getModContainer("cobblemon").orElse(null) ?: return@runCatching emptyList()
        val zipPath = cob.findPath("data/cobblemon/showdown.zip").orElse(null) ?: return@runCatching emptyList()
        val itemsJs = readInnerZip(Files.readAllBytes(zipPath), "data/items.js") ?: return@runCatching emptyList()
        itemsJsRx.findAll(itemsJs)
            .map { MegaEntry("mega_showdown", it.groupValues[1], it.groupValues[2], listOf(it.groupValues[3])) }
            .toList()
    }.getOrDefault(emptyList())

    private fun fromHeldItems(): List<MegaEntry> {
        val out = ArrayList<MegaEntry>()
        for (mod in FabricLoader.getInstance().allMods) {
            for (root in mod.rootPaths) scanRoot(root, out)
        }
        scanDatapacks(out)
        return out
    }

    private fun scanRoot(root: Path, out: MutableList<MegaEntry>) {
        runCatching {
            Files.newDirectoryStream(root.resolve("data")).use { dirs ->
                for (nsDir in dirs) {
                    val held = nsDir.resolve("showdown").resolve("held_items")
                    if (!Files.isDirectory(held)) continue
                    val ns = nsDir.fileName.toString().trim('/')
                    Files.walk(held).use { walk ->
                        walk.filter { it.extension == "js" }.forEach { f ->
                            parse(ns, f.nameWithoutExtension, Files.readString(f))?.let(out::add)
                        }
                    }
                }
            }
        }
    }

    private fun scanDatapacks(out: MutableList<MegaEntry>) {
        val dir = FabricLoader.getInstance().gameDir.resolve("datapacks")
        if (!Files.isDirectory(dir)) return
        runCatching {
            Files.newDirectoryStream(dir, "*.zip").use { zips ->
                for (zip in zips) runCatching {
                    ZipInputStream(Files.newInputStream(zip)).use { zin ->
                        var e = zin.nextEntry
                        while (e != null) {
                            val m = if (!e.isDirectory) heldEntry.matchEntire(e.name) else null
                            if (m != null) parse(m.groupValues[1], m.groupValues[2], zin.readBytes().decodeToString())?.let(out::add)
                            e = zin.nextEntry
                        }
                    }
                }
            }
        }
    }

    internal fun parse(ns: String, key: String, js: String): MegaEntry? {
        val stone = stoneRx.find(js)?.groupValues?.get(1) ?: return null
        val raw = evolvesRx.find(js)?.groupValues?.get(1) ?: return null
        val formes = Regex(""""([^"]+)"""").findAll(raw).map { it.groupValues[1] }.toList()
        if (formes.isEmpty()) return null
        return MegaEntry(ns, key, stone, formes)
    }

    private fun readInnerZip(bytes: ByteArray, name: String): String? {
        ZipInputStream(bytes.inputStream()).use { zin ->
            var e = zin.nextEntry
            while (e != null) {
                if (e.name == name) return zin.readBytes().decodeToString()
                e = zin.nextEntry
            }
        }
        return null
    }
}
