package com.cobbliki.rei.display

import com.cobblemon.mod.common.pokemon.Species
import com.cobbliki.rei.Categories
import com.cobbliki.rei.data.DropInfo
import com.cobbliki.rei.pokemon.PokemonEntryType
import com.cobbliki.rei.stackOf
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.util.EntryStacks

class DropsDisplay(val species: Species, val drops: List<DropInfo>) : CobblikiDisplay() {
    override fun getInputEntries(): List<EntryIngredient> = listOf(PokemonEntryType.ingredient(species))
    override fun getOutputEntries(): List<EntryIngredient> =
        drops.map { EntryIngredient.of(EntryStacks.of(stackOf(it))) }
    override fun getCategoryIdentifier(): CategoryIdentifier<*> = Categories.DROPS
}
