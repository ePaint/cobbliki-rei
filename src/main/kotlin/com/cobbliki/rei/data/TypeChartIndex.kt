package com.cobbliki.rei.data

import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.api.types.ElementalTypes
import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Files
import java.util.zip.ZipInputStream

data class TypeMatch(val attacking: ElementalType, val multiplier: Double)

object TypeChartIndex {
    private val chart by lazy { build() }

    private val blockRx = Regex("""(\w+):\s*\{[^{}]*?damageTaken:\s*\{([^}]*)\}""", RegexOption.DOT_MATCHES_ALL)
    private val pairRx = Regex("""(\w+)\s*:\s*(\d+)""")

    fun matchups(primary: ElementalType, secondary: ElementalType?): List<TypeMatch> =
        ElementalTypes.all().map { a ->
            val m = factor(primary, a) * (secondary?.let { factor(it, a) } ?: 1.0)
            TypeMatch(a, m)
        }

    private fun factor(defending: ElementalType, attacking: ElementalType): Double =
        codeToFactor(chart[defending.showdownId]?.get(attacking.name) ?: 0)

    private fun codeToFactor(code: Int): Double = when (code) {
        1 -> 0.5
        2 -> 2.0
        3 -> 0.0
        else -> 1.0
    }

    private fun build(): Map<String, Map<String, Int>> = runCatching {
        val cob = FabricLoader.getInstance().getModContainer("cobblemon").orElse(null) ?: return@runCatching emptyMap()
        val zipPath = cob.findPath("data/cobblemon/showdown.zip").orElse(null) ?: return@runCatching emptyMap()
        val js = readInnerZip(Files.readAllBytes(zipPath), "data/typechart.js") ?: return@runCatching emptyMap()
        blockRx.findAll(js).associate { m ->
            val defending = m.groupValues[1].lowercase()
            val taken = pairRx.findAll(m.groupValues[2]).associate { it.groupValues[1] to it.groupValues[2].toInt() }
            defending to taken
        }
    }.getOrDefault(emptyMap())

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
