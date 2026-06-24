package com.cobbliki.rei.display

import com.cobblemon.mod.common.pokemon.Species
import com.cobbliki.rei.Categories
import com.cobbliki.rei.data.DexPage
import com.cobbliki.rei.pokemon.PokemonEntryType
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.entry.EntryIngredient

class DexDisplay(val species: Species, val page: Int, val payload: DexPage?) : CobblikiDisplay() {
    override fun getInputEntries(): List<EntryIngredient> = emptyList()

    override fun getOutputEntries(): List<EntryIngredient> =
        (listOf(emptySet<String>()) + species.forms.map { it.aspects.toSet() }.filter { it.isNotEmpty() })
            .distinct().map { PokemonEntryType.ingredient(species, it) }

    override fun getCategoryIdentifier(): CategoryIdentifier<*> = Categories.DEX
}
