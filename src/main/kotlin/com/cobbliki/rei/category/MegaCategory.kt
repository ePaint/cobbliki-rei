package com.cobbliki.rei.category

import com.cobbliki.rei.Categories
import com.cobbliki.rei.display.MegaDisplay
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

class MegaCategory : DisplayCategory<MegaDisplay> {
    override fun getCategoryIdentifier(): CategoryIdentifier<MegaDisplay> = Categories.MEGA
    override fun getTitle(): Text = Text.translatable("category.cobbliki_rei.mega")
    override fun getIcon(): Renderer = EntryStacks.of(Registries.ITEM.get(Identifier.of("mega_showdown", "mega_stone")))
    override fun getDisplayHeight(): Int = 80
    override fun getDisplayWidth(display: MegaDisplay): Int = 168

    override fun setupDisplay(display: MegaDisplay, bounds: Rectangle): List<Widget> {
        val w = mutableListOf<Widget>()
        w.add(Widgets.createRecipeBase(bounds))
        w.add(Widgets.createSlot(Point(bounds.x + 6, bounds.y + 24)).entry(EntryStacks.of(display.stone)).markInput())
        w.add(pokemonWidget(display.base, bounds.x + 28, bounds.y + 6, 44))
        w.add(nameLabel(display.base, bounds.x + 50, bounds.y + 52))
        w.add(Widgets.createArrow(Point(bounds.x + 76, bounds.y + 22)))
        w.add(pokemonWidget(display.base, bounds.x + 104, bounds.y + 6, 44, setOf(display.aspect)))
        w.add(Widgets.createLabel(Point(bounds.x + 126, bounds.y + 52), Text.literal(display.megaForme)).noShadow().color(0xF06292))
        return w
    }
}
