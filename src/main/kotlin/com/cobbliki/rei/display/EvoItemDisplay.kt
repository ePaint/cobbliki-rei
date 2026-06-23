package com.cobbliki.rei.display

import com.cobblemon.mod.common.pokemon.Species
import com.cobbliki.rei.Categories
import com.cobbliki.rei.pokemon.PokemonEntryType
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.util.EntryStacks
import net.minecraft.item.ItemStack

class EvoItemDisplay(
    val from: Species,
    val fromAspects: Set<String>,
    val to: Species,
    val toAspects: Set<String>,
    val item: ItemStack?,
    val method: net.minecraft.text.Text,
) : CobblikiDisplay() {
    override fun getInputEntries(): List<EntryIngredient> = buildList {
        add(PokemonEntryType.ingredient(from, fromAspects))
        item?.let { add(EntryIngredient.of(EntryStacks.of(it))) }
    }
    override fun getOutputEntries(): List<EntryIngredient> = listOf(PokemonEntryType.ingredient(to, toAspects))
    override fun getCategoryIdentifier(): CategoryIdentifier<*> = if (item != null) Categories.EVO_ITEM else Categories.EVO
}
