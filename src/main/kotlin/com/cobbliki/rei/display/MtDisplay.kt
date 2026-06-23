package com.cobbliki.rei.display

import com.cobblemon.mod.common.pokemon.Species
import com.cobbliki.rei.Categories
import com.cobbliki.rei.data.MoveInfo
import com.cobbliki.rei.pokemon.PokemonEntryType
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.entry.EntryIngredient

class MtDisplay(val species: Species, val aspects: Set<String>, val moves: List<MoveInfo>) : CobblikiDisplay() {
    override fun getInputEntries(): List<EntryIngredient> = listOf(PokemonEntryType.ingredient(species, aspects))
    override fun getOutputEntries(): List<EntryIngredient> = emptyList()
    override fun getCategoryIdentifier(): CategoryIdentifier<*> = Categories.MT
}
