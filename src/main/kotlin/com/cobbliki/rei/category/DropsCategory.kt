package com.cobbliki.rei.category

import com.cobbliki.rei.Categories
import com.cobbliki.rei.display.DropsDisplay
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
import net.minecraft.item.Items
import net.minecraft.text.Text

class DropsCategory : DisplayCategory<DropsDisplay> {
    override fun getCategoryIdentifier(): CategoryIdentifier<DropsDisplay> = Categories.DROPS
    override fun getTitle(): Text = Text.translatable("category.cobbliki_rei.drops")
    override fun getIcon(): Renderer = EntryStacks.of(Items.BONE)
    override fun getDisplayHeight(): Int = 74
    override fun getDisplayWidth(display: DropsDisplay): Int = 150

    override fun setupDisplay(display: DropsDisplay, bounds: Rectangle): List<Widget> {
        val w = mutableListOf<Widget>()
        w.add(Widgets.createRecipeBase(bounds))
        w.add(pokemonWidget(display.species, bounds.x + 4, bounds.y + 8, 46))
        w.add(nameLabel(display.species, bounds.x + 27, bounds.y + 56))
        val x0 = bounds.x + 58; val y0 = bounds.y + 8
        display.drops.take(6).forEachIndexed { i, drop ->
            val sx = x0 + (i % 3) * 26; val sy = y0 + (i / 3) * 28
            w.add(Widgets.createSlot(Point(sx, sy)).entry(EntryStacks.of(stackOf(drop))).markOutput())
            w.add(Widgets.createLabel(Point(sx + 9, sy + 19), Text.literal("%.0f%%".format(drop.percentage))).noShadow().color(0xC9C9C9))
        }
        if (display.drops.size > 6)
            w.add(Widgets.createLabel(Point(bounds.x + 58, bounds.y + 58), Text.literal("+${display.drops.size - 6}")).leftAligned().noShadow().color(0xAAAAAA))
        return w
    }
}
