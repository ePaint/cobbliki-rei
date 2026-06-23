package com.cobbliki.rei.category

import com.cobbliki.rei.Categories
import com.cobbliki.rei.TrainerRender
import com.cobbliki.rei.display.TrainerDropDisplay
import com.cobbliki.rei.itemIcon
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.gui.Renderer
import me.shedaniel.rei.api.client.gui.widgets.Widget
import me.shedaniel.rei.api.client.gui.widgets.Widgets
import me.shedaniel.rei.api.client.registry.display.DisplayCategory
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.util.EntryStacks
import net.minecraft.text.Text

class TrainerCategory : DisplayCategory<TrainerDropDisplay> {
    override fun getCategoryIdentifier(): CategoryIdentifier<TrainerDropDisplay> = Categories.TRAINER
    override fun getTitle(): Text = Text.translatable("category.cobbliki_rei.trainer")
    override fun getIcon(): Renderer = itemIcon("rctmod:trainer_card", "minecraft:paper")
    override fun getDisplayHeight(): Int = 100
    override fun getDisplayWidth(display: TrainerDropDisplay): Int = 168

    override fun setupDisplay(display: TrainerDropDisplay, bounds: Rectangle): List<Widget> {
        val t = display.trainer
        val w = mutableListOf<Widget>()
        w.add(Widgets.createRecipeBase(bounds))
        w.add(TrainerRender.widget(t.id, bounds.x + 8, bounds.y + 6, 44, 76, 28))
        w.add(Widgets.createLabel(Point(bounds.x + 30, bounds.y + 86), t.name).noShadow().color(0xE0E0E0))
        val cols = 6
        val startX = bounds.x + 58
        val startY = bounds.y + 8
        t.drops.forEachIndexed { i, stack ->
            val cx = startX + (i % cols) * 18
            val cy = startY + (i / cols) * 18
            w.add(Widgets.createSlot(Point(cx, cy)).entry(EntryStacks.of(stack)).markOutput())
        }
        return w
    }
}
