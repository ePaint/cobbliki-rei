package com.cobbliki.rei.display

import com.cobblemon.mod.common.pokemon.Species
import com.cobbliki.rei.Categories
import com.cobbliki.rei.pokemon.PokemonEntryType
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.util.EntryStacks
import net.minecraft.item.ItemStack

class MegaDisplay(
    val stone: ItemStack,
    val species: Species,
    val baseAspects: Set<String>,
    val resultAspects: Set<String>,
    val resultName: String,
) : CobblikiDisplay() {
    override fun getInputEntries(): List<EntryIngredient> =
        listOf(EntryIngredient.of(EntryStacks.of(stone)), PokemonEntryType.ingredient(species, baseAspects))
    override fun getOutputEntries(): List<EntryIngredient> = listOf(PokemonEntryType.ingredient(species, resultAspects))
    override fun getCategoryIdentifier(): CategoryIdentifier<*> = Categories.MEGA
}
