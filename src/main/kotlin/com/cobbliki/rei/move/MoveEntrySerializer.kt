package com.cobbliki.rei.move

import com.cobbliki.rei.data.MoveInfo
import com.cobbliki.rei.data.moveInfoOf
import me.shedaniel.rei.api.common.entry.EntrySerializer
import me.shedaniel.rei.api.common.entry.EntryStack
import net.minecraft.nbt.NbtCompound

object MoveEntrySerializer : EntrySerializer<MoveInfo> {
    override fun supportSaving(): Boolean = true
    override fun supportReading(): Boolean = true

    override fun save(entry: EntryStack<MoveInfo>, value: MoveInfo): NbtCompound =
        NbtCompound().apply { putString("id", value.id) }

    override fun read(tag: NbtCompound): MoveInfo = moveInfoOf(tag.getString("id"))
}
