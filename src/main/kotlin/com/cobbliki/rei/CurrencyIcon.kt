package com.cobbliki.rei

import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.gui.Renderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.Identifier

object CurrencyIcon : Renderer {
    private val TEX: Identifier = Identifier.of("cobbliki_rei", "textures/gui/cobbledollar.png")

    fun blit(ctx: DrawContext, x: Int, y: Int, size: Int) =
        ctx.drawTexture(TEX, x, y, size, size, 0f, 0f, 11, 12, 11, 12)

    override fun render(ctx: DrawContext, bounds: Rectangle, mouseX: Int, mouseY: Int, delta: Float) {
        val s = minOf(bounds.width, bounds.height)
        blit(ctx, bounds.x + (bounds.width - s) / 2, bounds.y + (bounds.height - s) / 2, s)
    }
}
