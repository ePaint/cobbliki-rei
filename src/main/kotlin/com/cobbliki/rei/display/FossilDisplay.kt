package com.cobbliki.rei.display

import com.cobblemon.mod.common.pokemon.Species
import com.cobbliki.rei.Categories
import com.cobbliki.rei.pokemon.PokemonEntryType
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.util.EntryStacks
import net.minecraft.item.ItemStack

class FossilDisplay(
    val ingredients: List<List<ItemStack>>,
    val result: Species,
    val aspects: Set<String>,
) : CobblikiDisplay() {
    override fun getInputEntries(): List<EntryIngredient> =
        ingredients.map { slot -> EntryIngredient.of(slot.map { EntryStacks.of(it) }) }
    override fun getOutputEntries(): List<EntryIngredient> = listOf(PokemonEntryType.ingredient(result))
    override fun getCategoryIdentifier(): CategoryIdentifier<*> = Categories.FOSSIL
}
