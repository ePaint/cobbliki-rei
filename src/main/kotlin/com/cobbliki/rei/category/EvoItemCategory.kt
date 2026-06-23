package com.cobbliki.rei.category

import com.cobbliki.rei.Categories
import com.cobbliki.rei.arrow
import com.cobbliki.rei.display.EvoItemDisplay
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

class EvoItemCategory : DisplayCategory<EvoItemDisplay> {
    override fun getCategoryIdentifier(): CategoryIdentifier<EvoItemDisplay> = Categories.EVO_ITEM
    override fun getTitle(): Text = Text.translatable("category.cobbliki_rei.evo_item")
    override fun getIcon(): Renderer = EntryStacks.of(Registries.ITEM.get(Identifier.of("cobblemon", "dusk_stone")))
    override fun getDisplayHeight(): Int = 74
    override fun getDisplayWidth(display: EvoItemDisplay): Int = 168

    override fun setupDisplay(display: EvoItemDisplay, bounds: Rectangle): List<Widget> {
        val w = mutableListOf<Widget>()
        w.add(Widgets.createRecipeBase(bounds))
        w.add(pokemonWidget(display.from, bounds.x + 6, bounds.y + 6, 44, display.fromAspects))
        w.add(nameLabel(display.from, bounds.x + 28, bounds.y + 52))
        display.item?.let { w.add(Widgets.createSlot(Point(bounds.x + 66, bounds.y + 10)).entry(EntryStacks.of(it)).markInput()) }
        w.add(arrow(bounds.x + 75, bounds.y + 34))
        w.add(pokemonWidget(display.to, bounds.x + 110, bounds.y + 6, 44, display.toAspects))
        w.add(nameLabel(display.to, bounds.x + 132, bounds.y + 52))
        return w
    }
}
