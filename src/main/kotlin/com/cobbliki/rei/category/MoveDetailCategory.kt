package com.cobbliki.rei.category

import com.cobbliki.rei.Categories
import com.cobbliki.rei.data.MoveInfo
import com.cobbliki.rei.display.MoveDetailDisplay
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

class MoveDetailCategory : DisplayCategory<MoveDetailDisplay> {
    override fun getCategoryIdentifier(): CategoryIdentifier<MoveDetailDisplay> = Categories.MOVE_DETAIL
    override fun getTitle(): Text = Text.translatable("category.cobbliki_rei.move_detail")
    override fun getIcon(): Renderer = com.cobbliki.rei.itemIcon("tmcraft:gold_blank_disc", "minecraft:enchanted_book")
    override fun getDisplayHeight(): Int = 104
    override fun getDisplayWidth(display: MoveDetailDisplay): Int = 200

    override fun setupDisplay(display: MoveDetailDisplay, bounds: Rectangle): List<Widget> {
        val m = display.move
        val w = mutableListOf<Widget>()
        w.add(Widgets.createRecipeBase(bounds))
        w.add(Widgets.createSlot(Point(bounds.x + 6, bounds.y + 6)).entry(MoveEntryType.stack(m)))
        w.add(Widgets.createLabel(Point(bounds.x + 28, bounds.y + 8), m.displayName).leftAligned().noShadow().color(0xFFFFFF))
        val type = m.type.replaceFirstChar { it.uppercase() }
        val cat = m.category.replaceFirstChar { it.uppercase() }
        w.add(Widgets.createLabel(Point(bounds.x + 28, bounds.y + 20), Text.literal("$type · $cat")).leftAligned().noShadow().color(0xA0A0A0))
        val pow = if (m.power > 0) m.power.toInt().toString() else "—"
        val acc = if (m.accuracy in 1.0..100.0) "${m.accuracy.toInt()}%" else "—"
        w.add(Widgets.createLabel(Point(bounds.x + 6, bounds.y + 38), Text.translatable("category.cobbliki_rei.move.stats", pow, acc, m.pp)).leftAligned().noShadow().color(0xC9C9C9))
        wrap(m.description.string, 40).forEachIndexed { i, line ->
            w.add(Widgets.createLabel(Point(bounds.x + 6, bounds.y + 54 + i * 10), Text.literal(line)).leftAligned().noShadow().color(0x9AA0A6))
        }
        return w
    }

    private fun wrap(s: String, max: Int): List<String> {
        if (s.isBlank()) return emptyList()
        val lines = mutableListOf<String>()
        var cur = StringBuilder()
        for (word in s.split(" ")) {
            if (cur.isNotEmpty() && cur.length + 1 + word.length > max) {
                lines.add(cur.toString()); cur = StringBuilder()
            }
            if (cur.isNotEmpty()) cur.append(' ')
            cur.append(word)
        }
        if (cur.isNotEmpty()) lines.add(cur.toString())
        return lines.take(4)
    }
}
