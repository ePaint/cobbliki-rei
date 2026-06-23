package com.cobbliki.rei.currency

import com.cobbliki.rei.CobblikiRei
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.entry.EntryStack
import me.shedaniel.rei.api.common.entry.type.EntryType
import net.minecraft.util.Identifier

object CurrencyEntryType {
    val ID: Identifier = Identifier.of(CobblikiRei.MOD_ID, "cobbledollars")
    val TYPE: EntryType<Currency> = EntryType.deferred(ID)

    fun stack(): EntryStack<Currency> = EntryStack.of(TYPE, Currency.INSTANCE)
    fun ingredient(): EntryIngredient = EntryIngredient.of(stack())
}
