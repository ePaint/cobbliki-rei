package com.cobbliki.rei.move

import com.cobbliki.rei.CobblikiRei
import com.cobbliki.rei.data.MoveInfo
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.entry.EntryStack
import me.shedaniel.rei.api.common.entry.type.EntryType
import net.minecraft.util.Identifier

object MoveEntryType {
    val ID: Identifier = Identifier.of(CobblikiRei.MOD_ID, "move")
    val TYPE: EntryType<MoveInfo> = EntryType.deferred(ID)

    fun stack(move: MoveInfo): EntryStack<MoveInfo> = EntryStack.of(TYPE, move)
    fun ingredient(move: MoveInfo): EntryIngredient = EntryIngredient.of(stack(move))
}
