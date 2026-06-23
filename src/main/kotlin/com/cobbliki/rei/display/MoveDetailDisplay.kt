package com.cobbliki.rei.display

import com.cobbliki.rei.Categories
import com.cobbliki.rei.data.MoveInfo
import com.cobbliki.rei.move.MoveEntryType
import com.cobbliki.rei.tmcraftDiscs
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.util.EntryStacks

class MoveDetailDisplay(val move: MoveInfo) : CobblikiDisplay() {
    override fun getInputEntries(): List<EntryIngredient> =
        listOf(MoveEntryType.ingredient(move)) + tmcraftDiscs(move.id).map { EntryIngredient.of(EntryStacks.of(it)) }
    override fun getOutputEntries(): List<EntryIngredient> = emptyList()
    override fun getCategoryIdentifier(): CategoryIdentifier<*> = Categories.MOVE_DETAIL
}
