package com.cobbliki.rei.data

import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Files
import java.util.zip.ZipInputStream

data class MegaEntry(val itemKey: String, val baseName: String, val megaForme: String)

object MegaIndex {
    private val cache by lazy { build() }
    fun entries(): List<MegaEntry> = cache

    private fun build(): List<MegaEntry> = runCatching {
        val cob = FabricLoader.getInstance().getModContainer("cobblemon").orElse(null) ?: return@runCatching emptyList()
        val zipPath = cob.findPath("data/cobblemon/showdown.zip").orElse(null) ?: return@runCatching emptyList()
        val itemsJs = readInner(Files.readAllBytes(zipPath), "data/items.js") ?: return@runCatching emptyList()
        val rx = Regex(""""?(\w+)"?:\s*\{[^{}]*?megaStone:\s*"([^"]+)"[^{}]*?megaEvolves:\s*"([^"]+)"""")
        rx.findAll(itemsJs).map { MegaEntry(it.groupValues[1], it.groupValues[3], it.groupValues[2]) }.toList()
    }.getOrDefault(emptyList())

    private fun readInner(bytes: ByteArray, name: String): String? {
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
