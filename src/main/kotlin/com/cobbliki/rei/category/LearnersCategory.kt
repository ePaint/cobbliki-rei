package com.cobbliki.rei.category

import com.cobbliki.rei.Categories
import com.cobbliki.rei.display.LearnersDisplay
import com.cobbliki.rei.move.MoveEntryType
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

const val LEARNER_COLS = 6
const val LEARNER_ROWS = 4
const val LEARNER_PAGE = LEARNER_COLS * LEARNER_ROWS

class LearnersCategory : DisplayCategory<LearnersDisplay> {
    override fun getCategoryIdentifier(): CategoryIdentifier<LearnersDisplay> = Categories.LEARNERS
    override fun getTitle(): Text = Text.translatable("category.cobbliki_rei.learners")
    override fun getIcon(): Renderer = EntryStacks.of(Registries.ITEM.get(Identifier.of("tmcraft", "iron_blank_disc")))
    override fun getDisplayHeight(): Int = 24 + LEARNER_ROWS * 30
    override fun getDisplayWidth(display: LearnersDisplay): Int = 12 + LEARNER_COLS * 30

    override fun setupDisplay(display: LearnersDisplay, bounds: Rectangle): List<Widget> {
        val w = mutableListOf<Widget>()
        w.add(Widgets.createRecipeBase(bounds))
        w.add(Widgets.createSlot(Point(bounds.x + 6, bounds.y + 4)).entry(MoveEntryType.stack(display.move)))
        w.add(Widgets.createLabel(Point(bounds.x + 28, bounds.y + 9), display.move.displayName).leftAligned().noShadow().color(0xE0E0E0))
        display.species.forEachIndexed { i, sp ->
            val x = bounds.x + 8 + (i % LEARNER_COLS) * 30
            val y = bounds.y + 24 + (i / LEARNER_COLS) * 30
            w.add(pokemonWidget(sp, x, y, 28))
        }
        return w
    }
}
