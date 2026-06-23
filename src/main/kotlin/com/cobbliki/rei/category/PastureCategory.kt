package com.cobbliki.rei.category

import com.cobbliki.rei.Categories
import com.cobbliki.rei.display.PastureDisplay
import com.cobbliki.rei.nameLabel
import com.cobbliki.rei.pokemonWidget
import com.cobbliki.rei.stackOf
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

class PastureCategory : DisplayCategory<PastureDisplay> {
    override fun getCategoryIdentifier(): CategoryIdentifier<PastureDisplay> = Categories.PASTURE
    override fun getTitle(): Text = Text.translatable("category.cobbliki_rei.pasture")
    override fun getIcon(): Renderer = EntryStacks.of(Registries.ITEM.get(Identifier.of("cobblemon", "pasture")))
    override fun getDisplayHeight(): Int = 84
    override fun getDisplayWidth(display: PastureDisplay): Int = 150

    override fun setupDisplay(display: PastureDisplay, bounds: Rectangle): List<Widget> {
        val w = mutableListOf<Widget>()
        w.add(Widgets.createRecipeBase(bounds))
        w.add(pokemonWidget(display.species, bounds.x + 4, bounds.y + 6, 46))
        w.add(nameLabel(display.species, bounds.x + 27, bounds.y + 54))
        w.add(Widgets.createLabel(Point(bounds.x + 27, bounds.y + 66), Text.translatable("category.cobbliki_rei.pasture_rate", "%.0f".format(display.chancePerMinute * 100))).noShadow().color(0x88D17A))
        val x0 = bounds.x + 58; val y0 = bounds.y + 8
        display.drops.take(6).forEachIndexed { i, drop ->
            val sx = x0 + (i % 3) * 26; val sy = y0 + (i / 3) * 28
            w.add(Widgets.createSlot(Point(sx, sy)).entry(EntryStacks.of(stackOf(drop))).markOutput())
            w.add(Widgets.createLabel(Point(sx + 9, sy + 19), Text.literal("%.0f%%".format(drop.percentage))).noShadow().color(0xC9C9C9))
        }
        if (display.drops.size > 6)
            w.add(Widgets.createLabel(Point(bounds.x + 58, bounds.y + 64), Text.literal("+${display.drops.size - 6}")).leftAligned().noShadow().color(0xAAAAAA))
        return w
    }
}
