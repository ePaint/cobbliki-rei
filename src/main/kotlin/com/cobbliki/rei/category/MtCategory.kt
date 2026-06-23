package com.cobbliki.rei.category

import com.cobbliki.rei.Categories
import com.cobbliki.rei.data.MoveInfo
import com.cobbliki.rei.display.MtDisplay
import com.cobbliki.rei.move.MoveEntryType
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.gui.Renderer
import me.shedaniel.rei.api.client.gui.widgets.Widget
import me.shedaniel.rei.api.client.gui.widgets.Widgets
import me.shedaniel.rei.api.client.registry.display.DisplayCategory
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.util.EntryStacks
import net.minecraft.registry.Registries
import net.minecraft.text.Text
import net.minecraft.util.Identifier

const val MT_PAGE_SIZE = 7

class MtCategory : DisplayCategory<MtDisplay> {
    override fun getCategoryIdentifier(): CategoryIdentifier<MtDisplay> = Categories.MT
    override fun getTitle(): Text = Text.translatable("category.cobbliki_rei.mt")
    override fun getIcon(): Renderer = com.cobbliki.rei.itemIcon("tmcraft:iron_blank_disc", "minecraft:enchanted_book")
    override fun getDisplayHeight(): Int = 16 + MT_PAGE_SIZE * 18
    override fun getDisplayWidth(display: MtDisplay): Int = 184

    override fun setupDisplay(display: MtDisplay, bounds: Rectangle): List<Widget> {
        val w = mutableListOf<Widget>()
        display.moves.forEachIndexed { i, mv ->
            val y = bounds.y + 6 + i * 18
            w.add(Widgets.createSlot(Point(bounds.x + 6, y)).entry(MoveEntryType.stack(mv)))
            w.add(Widgets.createLabel(Point(bounds.x + 28, y + 5), mv.displayName).leftAligned().noShadow().color(0xE0E0E0))
            w.add(Widgets.createLabel(Point(bounds.x + 180, y + 5), Text.literal(stats(mv))).rightAligned().noShadow().color(0xA0A0A0))
        }
        return w
    }

    private fun stats(m: MoveInfo): String {
        val pow = if (m.power > 0) m.power.toInt().toString() else "—"
        val acc = if (m.accuracy in 1.0..100.0) m.accuracy.toInt().toString() else "—"
        return "$pow/$acc/${m.pp}"
    }
}
