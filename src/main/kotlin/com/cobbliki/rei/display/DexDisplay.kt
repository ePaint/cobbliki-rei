package com.cobbliki.rei.display

import com.cobblemon.mod.common.pokemon.Species
import com.cobbliki.rei.Categories
import com.cobbliki.rei.data.SpawnInfo
import com.cobbliki.rei.pokemon.PokemonEntryType
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.entry.EntryIngredient

class DexDisplay(val species: Species, val page: Int, val spawns: List<SpawnInfo>?) : CobblikiDisplay() {
    val isBio: Boolean get() = spawns == null

    override fun getInputEntries(): List<EntryIngredient> = emptyList()
    override fun getOutputEntries(): List<EntryIngredient> = listOf(PokemonEntryType.ingredient(species))
    override fun getCategoryIdentifier(): CategoryIdentifier<*> = Categories.DEX
}
