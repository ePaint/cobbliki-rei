package com.cobbliki.rei.display

import com.cobbliki.rei.Categories
import com.cobbliki.rei.currency.CurrencyEntryType
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.util.EntryStacks
import net.minecraft.item.ItemStack
import java.math.BigInteger

class BuyDisplay(val stack: ItemStack, val price: BigInteger, val category: String) : CobblikiDisplay() {
    override fun getInputEntries(): List<EntryIngredient> = listOf(CurrencyEntryType.ingredient())
    override fun getOutputEntries(): List<EntryIngredient> =
        listOf(EntryIngredient.of(EntryStacks.of(stack)))
    override fun getCategoryIdentifier(): CategoryIdentifier<*> = Categories.BUY
}
