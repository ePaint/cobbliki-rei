package com.cobbliki.rei.data

import com.cobblemon.mod.common.api.drop.ItemDropEntry
import com.cobblemon.mod.common.api.moves.Moves
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.pokemon.Species
import net.minecraft.text.Text
import net.minecraft.util.Identifier

data class DropInfo(val item: Identifier, val min: Int, val max: Int, val percentage: Float)

data class MoveInfo(
    val id: String,
    val displayName: Text,
    val typeName: Text,
    val typeIndex: Int,
    val power: Double,
    val accuracy: Double,
    val pp: Int,
    val categoryName: Text,
    val description: Text,
)

fun moveInfoOf(id: String): MoveInfo {
    val m = Moves.getByName(id) ?: return MoveInfo(id, Text.literal(id), Text.empty(), 0, 0.0, 0.0, 0, Text.empty(), Text.empty())
    return MoveInfo(m.name, m.displayName, m.elementalType.displayName, m.elementalType.textureXMultiplier, m.power, m.accuracy, m.pp, m.damageCategory.displayName, m.description)
}

data class PokemonInfo(
    val species: Species,
    val dex: Int,
    val name: String,
    val displayName: Text,
    val drops: List<DropInfo>,
    val tmMoves: List<MoveInfo>,
    val tutorMoves: List<MoveInfo>,
    val eggMoves: List<MoveInfo>,
) {
    fun pastureDrops(blacklist: Set<Identifier>): List<DropInfo> =
        drops.filter { it.item !in blacklist }
}

data class FormMoves(val tm: List<MoveInfo>, val tutor: List<MoveInfo>, val egg: List<MoveInfo>) {
    fun isEmpty() = tm.isEmpty() && tutor.isEmpty() && egg.isEmpty()
}

object PokemonData {
    fun all(): List<PokemonInfo> = PokemonSpecies.species.map(::build).sortedBy { it.dex }

    fun movesOf(species: Species, aspects: Set<String>): FormMoves {
        val ids = SpeciesMovesIndex[SpeciesMovesIndex.formKey(species.resourceIdentifier.toString(), aspects)]
        return FormMoves(ids?.tm.orEmpty().map(::moveInfoOf), ids?.tutor.orEmpty().map(::moveInfoOf), ids?.egg.orEmpty().map(::moveInfoOf))
    }

    private fun build(s: Species): PokemonInfo {
        val m = movesOf(s, emptySet())
        return PokemonInfo(
            species = s,
            dex = s.nationalPokedexNumber,
            name = s.name,
            displayName = s.translatedName,
            drops = s.drops.entries.filterIsInstance<ItemDropEntry>().mapNotNull(::dropOf),
            tmMoves = m.tm,
            tutorMoves = m.tutor,
            eggMoves = m.egg,
        )
    }

    private fun dropOf(e: ItemDropEntry): DropInfo? {
        val id = e.item ?: return null
        val range = e.quantityRange
        return DropInfo(id, range?.first ?: e.quantity, range?.last ?: e.quantity, e.percentage)
    }
}
