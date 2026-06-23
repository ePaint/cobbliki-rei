package com.cobbliki.rei.currency

import me.shedaniel.rei.api.common.entry.EntrySerializer
import me.shedaniel.rei.api.common.entry.EntryStack
import net.minecraft.nbt.NbtCompound

object CurrencyEntrySerializer : EntrySerializer<Currency> {
    override fun supportSaving(): Boolean = true
    override fun supportReading(): Boolean = true
    override fun save(entry: EntryStack<Currency>, value: Currency): NbtCompound = NbtCompound()
    override fun read(tag: NbtCompound): Currency = Currency.INSTANCE
}
