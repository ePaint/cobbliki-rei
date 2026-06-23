package com.cobbliki.rei.currency

import me.shedaniel.rei.api.client.entry.renderer.EntryRenderer
import me.shedaniel.rei.api.common.entry.EntrySerializer
import me.shedaniel.rei.api.common.entry.EntryStack
import me.shedaniel.rei.api.common.entry.comparison.ComparisonContext
import me.shedaniel.rei.api.common.entry.type.EntryDefinition
import me.shedaniel.rei.api.common.entry.type.EntryType
import net.minecraft.registry.tag.TagKey
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.util.stream.Stream

object CurrencyEntryDefinition : EntryDefinition<Currency> {
    override fun getValueType(): Class<Currency> = Currency::class.java
    override fun getType(): EntryType<Currency> = CurrencyEntryType.TYPE
    override fun getRenderer(): EntryRenderer<Currency> = CurrencyEntryRenderer
    override fun getSerializer(): EntrySerializer<Currency> = CurrencyEntrySerializer
    override fun getIdentifier(entry: EntryStack<Currency>, value: Currency): Identifier = CurrencyEntryType.ID
    override fun isEmpty(entry: EntryStack<Currency>, value: Currency): Boolean = false
    override fun copy(entry: EntryStack<Currency>, value: Currency): Currency = value
    override fun normalize(entry: EntryStack<Currency>, value: Currency): Currency = value
    override fun wildcard(entry: EntryStack<Currency>, value: Currency): Currency = value
    override fun hash(entry: EntryStack<Currency>, value: Currency, context: ComparisonContext): Long = 1L
    override fun equals(o1: Currency, o2: Currency, context: ComparisonContext): Boolean = true
    override fun asFormattedText(entry: EntryStack<Currency>, value: Currency): Text = Text.translatable("entry.cobbliki_rei.cobbledollars")
    override fun getTagsFor(entry: EntryStack<Currency>, value: Currency): Stream<out TagKey<*>> = Stream.empty()
}
