package com.cobbliki.rei.pokemon

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

object PokemonEntryDefinition : EntryDefinition<PokemonForm> {
    override fun getValueType(): Class<PokemonForm> = PokemonForm::class.java
    override fun getType(): EntryType<PokemonForm> = PokemonEntryType.TYPE
    override fun getRenderer(): EntryRenderer<PokemonForm> = PokemonEntryRenderer
    override fun getSerializer(): EntrySerializer<PokemonForm> = PokemonEntrySerializer
    override fun getIdentifier(entry: EntryStack<PokemonForm>, value: PokemonForm): Identifier = value.species.resourceIdentifier
    override fun isEmpty(entry: EntryStack<PokemonForm>, value: PokemonForm): Boolean = false
    override fun copy(entry: EntryStack<PokemonForm>, value: PokemonForm): PokemonForm = value
    override fun normalize(entry: EntryStack<PokemonForm>, value: PokemonForm): PokemonForm = value
    override fun wildcard(entry: EntryStack<PokemonForm>, value: PokemonForm): PokemonForm = value
    override fun hash(entry: EntryStack<PokemonForm>, value: PokemonForm, context: ComparisonContext): Long = value.key.hashCode().toLong()
    override fun equals(o1: PokemonForm, o2: PokemonForm, context: ComparisonContext): Boolean = o1.key == o2.key
    override fun asFormattedText(entry: EntryStack<PokemonForm>, value: PokemonForm): Text = value.species.translatedName
    override fun getTagsFor(entry: EntryStack<PokemonForm>, value: PokemonForm): Stream<out TagKey<*>> = Stream.empty()
}
