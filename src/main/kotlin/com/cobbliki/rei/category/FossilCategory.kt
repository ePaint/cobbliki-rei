package com.cobbliki.rei.category

import com.cobbliki.rei.Categories
import com.cobbliki.rei.display.FossilDisplay
import com.cobbliki.rei.nameLabel
import com.cobbliki.rei.pokemonWidget
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

class FossilCategory : DisplayCategory<FossilDisplay> {
    override fun getCategoryIdentifier(): CategoryIdentifier<FossilDisplay> = Categories.FOSSIL
    override fun getTitle(): Text = Text.translatable("category.cobbliki_rei.fossil")
    override fun getIcon(): Renderer = EntryStacks.of(Registries.ITEM.get(Identifier.of("cobblemon", "restoration_tank")))
    override fun getDisplayHeight(): Int = 74
    override fun getDisplayWidth(display: FossilDisplay): Int = 168

    override fun setupDisplay(display: FossilDisplay, bounds: Rectangle): List<Widget> {
        val w = mutableListOf<Widget>()
        w.add(Widgets.createRecipeBase(bounds))
        display.ingredients.take(4).forEachIndexed { i, slot ->
            val sx = bounds.x + 8 + (i % 2) * 20
            val sy = bounds.y + 10 + (i / 2) * 20
            w.add(Widgets.createSlot(Point(sx, sy)).entries(slot.map { EntryStacks.of(it) }).markInput())
        }
        w.add(Widgets.createArrow(Point(bounds.x + 56, bounds.y + 20)))
        w.add(pokemonWidget(display.result, bounds.x + 100, bounds.y + 6, 44, display.aspects))
        w.add(nameLabel(display.result, bounds.x + 122, bounds.y + 52))
        return w
    }
}
