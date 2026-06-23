package com.cobbliki.rei.currency

import com.cobbliki.rei.CurrencyIcon
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.entry.renderer.EntryRenderer
import me.shedaniel.rei.api.client.gui.widgets.Tooltip
import me.shedaniel.rei.api.client.gui.widgets.TooltipContext
import me.shedaniel.rei.api.common.entry.EntryStack
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text

object CurrencyEntryRenderer : EntryRenderer<Currency> {
    override fun render(entry: EntryStack<Currency>, ctx: DrawContext, bounds: Rectangle, mouseX: Int, mouseY: Int, delta: Float) {
        CurrencyIcon.blit(ctx, bounds.x, bounds.y, minOf(bounds.width, bounds.height))
    }

    override fun getTooltip(entry: EntryStack<Currency>, context: TooltipContext): Tooltip =
        Tooltip.create(Text.translatable("entry.cobbliki_rei.cobbledollars"))
}
