package com.cobbliki.rei.category

import com.cobbliki.rei.Categories
import com.cobbliki.rei.arrow
import com.cobbliki.rei.display.EvoItemDisplay
import com.cobbliki.rei.nameLabels
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

class EvoCategory : DisplayCategory<EvoItemDisplay> {
    override fun getCategoryIdentifier(): CategoryIdentifier<EvoItemDisplay> = Categories.EVO
    override fun getTitle(): Text = Text.translatable("category.cobbliki_rei.evo")
    override fun getIcon(): Renderer = EntryStacks.of(Registries.ITEM.get(Identifier.of("cobblemon", "rare_candy")))
    override fun getDisplayHeight(): Int = 74
    override fun getDisplayWidth(display: EvoItemDisplay): Int = 150

    override fun setupDisplay(display: EvoItemDisplay, bounds: Rectangle): List<Widget> {
        val w = mutableListOf<Widget>()
        w.add(Widgets.createRecipeBase(bounds))
        w.add(pokemonWidget(display.from, bounds.x + 6, bounds.y + 6, 44, display.fromAspects))
        w.addAll(nameLabels(display.from, display.fromAspects, bounds.x + 28, bounds.y + 52, 56))
        w.add(Widgets.createLabel(Point(bounds.x + 75, bounds.y + 12), display.method).noShadow().color(0xC9C9C9))
        w.add(arrow(bounds.x + 75, bounds.y + 28))
        w.add(pokemonWidget(display.to, bounds.x + 100, bounds.y + 6, 44, display.toAspects))
        w.addAll(nameLabels(display.to, display.toAspects, bounds.x + 122, bounds.y + 52, 56))
        return w
    }
}
