package com.cobbliki.rei.display

import com.cobbliki.rei.Categories
import com.cobbliki.rei.data.TrainerDrop
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.util.EntryStacks

class TrainerDropDisplay(val trainer: TrainerDrop) : CobblikiDisplay() {
    override fun getInputEntries(): List<EntryIngredient> = emptyList()
    override fun getOutputEntries(): List<EntryIngredient> = trainer.drops.map { EntryIngredient.of(EntryStacks.of(it)) }
    override fun getCategoryIdentifier(): CategoryIdentifier<*> = Categories.TRAINER
}
