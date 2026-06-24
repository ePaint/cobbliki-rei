package com.cobbliki.rei.data

import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.api.riding.RidingStyle
import com.cobblemon.mod.common.api.riding.stats.RidingStat
import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.pokemon.FormData
import com.cobblemon.mod.common.pokemon.Species
import com.cobblemon.mod.common.pokemon.abilities.HiddenAbility
import net.minecraft.text.Text

data class AbilityInfo(val name: Text, val description: Text, val hidden: Boolean)

data class RideStyle(val style: RidingStyle, val stats: List<Pair<RidingStat, IntRange>>)
data class RideInfo(val styles: List<RideStyle>, val seats: Int)

data class DexBio(
    val dex: Int,
    val types: List<Pair<Text, Int>>,
    val primaryType: ElementalType,
    val secondaryType: ElementalType?,
    val abilities: List<AbilityInfo>,
    val baseStats: List<Pair<Text, Int>>,
    val eggGroups: List<String>,
    val catchRate: Int,
    val baseExp: Int,
    val friendship: Int,
    val genderKey: String,
    val height: Float,
    val weight: Float,
)

sealed interface DexPage
data class AbilityPage(val abilities: List<AbilityInfo>, val bio: DexBio) : DexPage
data class RidePage(val ride: RideInfo) : DexPage
data class SpawnPage(val spawns: List<SpawnInfo>) : DexPage

const val SPAWN_PER_PAGE = 2
private val RIDE_STAT_ORDER = listOf(
    RidingStat.SPEED, RidingStat.ACCELERATION, RidingStat.JUMP, RidingStat.STAMINA, RidingStat.SKILL,
)

object DexData {
    fun bioOf(s: Species, aspects: Set<String>): DexBio {
        val f = s.getForm(aspects)
        return DexBio(
            dex = s.nationalPokedexNumber,
            types = f.types.map { it.displayName to it.textureXMultiplier },
            primaryType = f.primaryType,
            secondaryType = f.secondaryType,
            abilities = abilitiesOf(f),
            baseStats = STAT_ORDER.map { it.displayName to (f.baseStats[it] ?: 0) },
            eggGroups = f.eggGroups.map { eggLabel(it.showdownID) },
            catchRate = f.catchRate,
            baseExp = f.baseExperienceYield,
            friendship = f.baseFriendship,
            genderKey = genderKey(f.maleRatio),
            height = f.height,
            weight = f.weight,
        )
    }

    fun spawnsOf(s: Species, aspects: Set<String>): List<SpawnInfo> =
        SpawnIndex[s.name].filter { it.aspects == aspects }

    fun rideOf(f: FormData): RideInfo? {
        val r = f.riding
        val behaviours = r.behaviours ?: return null
        if (behaviours.isEmpty()) return null
        val styles = behaviours.entries.map { (style, settings) ->
            RideStyle(style, RIDE_STAT_ORDER.mapNotNull { st -> settings.stats[st]?.let { st to it } })
        }
        return RideInfo(styles, r.seats.size)
    }

    fun spawnPages(s: Species, aspects: Set<String>): List<DexPage> {
        val bio = bioOf(s, aspects)
        val pages = mutableListOf<DexPage>(AbilityPage(bio.abilities, bio))
        rideOf(s.getForm(aspects))?.let { pages.add(RidePage(it)) }
        spawnsOf(s, aspects).chunked(SPAWN_PER_PAGE).forEach { pages.add(SpawnPage(it)) }
        return pages
    }

    private fun abilitiesOf(f: FormData): List<AbilityInfo> =
        f.abilities.groupBy { it.template.displayName }.values.map { group ->
            val a = group.first()
            AbilityInfo(
                name = Text.translatable(a.template.displayName),
                description = Text.translatable(a.template.description),
                hidden = group.all { it is HiddenAbility },
            )
        }

    private fun genderKey(maleRatio: Float): String =
        if (maleRatio < 0f) "genderless" else "ratio"

    private fun eggLabel(showdownId: String): String = prettyResource(showdownId)

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
