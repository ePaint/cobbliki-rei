package com.cobbliki.rei.data

import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.pokemon.Species
import net.minecraft.text.Text

data class DexBio(
    val dex: Int,
    val types: List<Pair<Text, Int>>,
    val abilities: List<Text>,
    val baseStats: List<Pair<Text, Int>>,
    val eggGroups: List<String>,
    val catchRate: Int,
    val baseExp: Int,
    val friendship: Int,
    val genderKey: String,
    val height: Float,
    val weight: Float,
)

const val SPAWN_PER_PAGE = 3

object DexData {
    fun bioOf(s: Species): DexBio = DexBio(
        dex = s.nationalPokedexNumber,
        types = s.types.map { it.displayName to it.textureXMultiplier },
        abilities = s.abilities.map { Text.translatable(it.template.displayName) },
        baseStats = STAT_ORDER.map { it.displayName to (s.baseStats[it] ?: 0) },
        eggGroups = s.eggGroups.map { eggLabel(it.showdownID) },
        catchRate = s.catchRate,
        baseExp = s.baseExperienceYield,
        friendship = s.baseFriendship,
        genderKey = genderKey(s.maleRatio),
        height = s.height,
        weight = s.weight,
    )

    fun spawnsOf(s: Species): List<SpawnInfo> = SpawnIndex[s.name]

    fun spawnPages(s: Species): List<List<SpawnInfo>> = spawnsOf(s).chunked(SPAWN_PER_PAGE)

    private fun genderKey(maleRatio: Float): String =
        if (maleRatio < 0f) "genderless" else "ratio"

    private fun eggLabel(showdownId: String): String =
        showdownId.replace('-', ' ').replace('_', ' ').split(' ')
            .filter { it.isNotBlank() }.joinToString(" ") { it.replaceFirstChar(Char::uppercase) }

    private val STAT_ORDER = listOf(
        Stats.HP, Stats.ATTACK, Stats.DEFENCE, Stats.SPECIAL_ATTACK, Stats.SPECIAL_DEFENCE, Stats.SPEED,
    )
}

fun prettyResource(raw: String): String {
    val noTag = raw.removePrefix("#")
    val noNs = noTag.substringAfter(':', noTag)
    val seg = noNs.substringAfterLast('/')
    return seg.replace('-', ' ').replace('_', ' ').split(' ')
        .filter { it.isNotBlank() }.joinToString(" ") { it.replaceFirstChar(Char::uppercase) }
}
