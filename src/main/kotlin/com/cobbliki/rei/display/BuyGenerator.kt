package com.cobbliki.rei.display

import com.cobbliki.rei.currency.CurrencyEntryType
import com.cobbliki.rei.data.EconomyData
import me.shedaniel.rei.api.client.registry.display.DynamicDisplayGenerator
import me.shedaniel.rei.api.common.entry.EntryStack
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes
import net.minecraft.item.ItemStack
import java.util.Optional

object BuyGenerator : DynamicDisplayGenerator<BuyDisplay> {
    private fun all(): List<BuyDisplay> = EconomyData.buyOffers().map { BuyDisplay(it.stack, it.price, it.category) }

    override fun getUsageFor(entry: EntryStack<*>): Optional<List<BuyDisplay>> {
        if (entry.type != CurrencyEntryType.TYPE) return Optional.empty()
        return Optional.of(all())
    }

    override fun getRecipeFor(entry: EntryStack<*>): Optional<List<BuyDisplay>> {
        if (entry.type != VanillaEntryTypes.ITEM) return Optional.empty()
        val clicked = entry.value as? ItemStack ?: return Optional.empty()
        val matches = all().filter { ItemStack.areItemsAndComponentsEqual(it.stack, clicked) }
        return if (matches.isEmpty()) Optional.empty() else Optional.of(matches)
    }
}
