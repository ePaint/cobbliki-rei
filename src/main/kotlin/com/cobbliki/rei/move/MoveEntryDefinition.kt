package com.cobbliki.rei.move

import com.cobbliki.rei.data.MoveInfo
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

object MoveEntryDefinition : EntryDefinition<MoveInfo> {
    override fun getValueType(): Class<MoveInfo> = MoveInfo::class.java
    override fun getType(): EntryType<MoveInfo> = MoveEntryType.TYPE
    override fun getRenderer(): EntryRenderer<MoveInfo> = MoveEntryRenderer
    override fun getSerializer(): EntrySerializer<MoveInfo> = MoveEntrySerializer
    override fun getIdentifier(entry: EntryStack<MoveInfo>, value: MoveInfo): Identifier = Identifier.of("cobblemon", value.id)
    override fun isEmpty(entry: EntryStack<MoveInfo>, value: MoveInfo): Boolean = false
    override fun copy(entry: EntryStack<MoveInfo>, value: MoveInfo): MoveInfo = value
    override fun normalize(entry: EntryStack<MoveInfo>, value: MoveInfo): MoveInfo = value
    override fun wildcard(entry: EntryStack<MoveInfo>, value: MoveInfo): MoveInfo = value
    override fun hash(entry: EntryStack<MoveInfo>, value: MoveInfo, context: ComparisonContext): Long = value.id.hashCode().toLong()
    override fun equals(o1: MoveInfo, o2: MoveInfo, context: ComparisonContext): Boolean = o1.id == o2.id
    override fun asFormattedText(entry: EntryStack<MoveInfo>, value: MoveInfo): Text = value.displayName
    override fun getTagsFor(entry: EntryStack<MoveInfo>, value: MoveInfo): Stream<out TagKey<*>> = Stream.empty()
}
