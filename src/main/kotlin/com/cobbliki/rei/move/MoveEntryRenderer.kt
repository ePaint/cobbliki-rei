package com.cobbliki.rei.move

import com.cobbliki.rei.data.MoveInfo
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.entry.renderer.EntryRenderer
import me.shedaniel.rei.api.client.gui.widgets.Tooltip
import me.shedaniel.rei.api.client.gui.widgets.TooltipContext
import me.shedaniel.rei.api.common.entry.EntryStack
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier

object MoveEntryRenderer : EntryRenderer<MoveInfo> {
    private val TYPES: Identifier = Identifier.of("cobblemon", "textures/gui/types_small.png")

    override fun render(entry: EntryStack<MoveInfo>, ctx: DrawContext, bounds: Rectangle, mouseX: Int, mouseY: Int, delta: Float) {
        val move = entry.value ?: return
        val s = minOf(bounds.width, bounds.height)
        ctx.drawTexture(TYPES, bounds.x, bounds.y + (bounds.height - s) / 2, s, s, (move.typeIndex * 18).toFloat(), 0f, 18, 18, 324, 18)
    }

    override fun getTooltip(entry: EntryStack<MoveInfo>, context: TooltipContext): Tooltip? {
        val move = entry.value ?: return null
        val lines = mutableListOf<Text>(move.displayName)
        lines.add(Text.empty().append(move.typeName).append(" · ").append(move.categoryName).formatted(Formatting.GRAY))
        val pow = if (move.power > 0) move.power.toInt().toString() else "—"
        val acc = if (move.accuracy in 1.0..100.0) "${move.accuracy.toInt()}%" else "—"
        lines.add(Text.translatable("category.cobbliki_rei.move.stats", pow, acc, move.pp).formatted(Formatting.DARK_GRAY))
        if (move.description.string.isNotBlank()) lines.add(move.description.copy().formatted(Formatting.GRAY, Formatting.ITALIC))
        return Tooltip.create(lines)
    }
}
