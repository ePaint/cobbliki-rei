package com.cobbliki.rei.display

import com.cobbliki.rei.Categories
import com.cobbliki.rei.currency.CurrencyEntryType
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.util.EntryStacks
import net.minecraft.item.ItemStack
import java.math.BigInteger

class SellDisplay(val stack: ItemStack, val price: BigInteger) : CobblikiDisplay() {
    override fun getInputEntries(): List<EntryIngredient> =
        listOf(EntryIngredient.of(EntryStacks.of(stack)))
    override fun getOutputEntries(): List<EntryIngredient> = listOf(CurrencyEntryType.ingredient())
    override fun getCategoryIdentifier(): CategoryIdentifier<*> = Categories.SELL
}
