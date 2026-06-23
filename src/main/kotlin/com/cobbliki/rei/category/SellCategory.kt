package com.cobbliki.rei.category

import com.cobbliki.rei.Categories
import com.cobbliki.rei.CurrencyIcon
import com.cobbliki.rei.Money
import com.cobbliki.rei.coin
import com.cobbliki.rei.display.SellDisplay
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.gui.Renderer
import me.shedaniel.rei.api.client.gui.widgets.Widget
import me.shedaniel.rei.api.client.gui.widgets.Widgets
import me.shedaniel.rei.api.client.registry.display.DisplayCategory
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.util.EntryStacks
import net.minecraft.text.Text

class SellCategory : DisplayCategory<SellDisplay> {
    override fun getCategoryIdentifier(): CategoryIdentifier<SellDisplay> = Categories.SELL
    override fun getTitle(): Text = Text.translatable("category.cobbliki_rei.sell")
    override fun getIcon(): Renderer = CurrencyIcon
    override fun getDisplayHeight(): Int = 34

    override fun setupDisplay(display: SellDisplay, bounds: Rectangle): List<Widget> {
        val w = mutableListOf<Widget>()
        w.add(Widgets.createRecipeBase(bounds))
        w.add(Widgets.createSlot(Point(bounds.x + 10, bounds.y + 8)).entry(EntryStacks.of(display.stack)).markInput())
        w.add(Widgets.createArrow(Point(bounds.x + 40, bounds.y + 9)))
        w.add(coin(bounds.x + 74, bounds.y + 10, 16))
        w.add(Widgets.createLabel(Point(bounds.x + 94, bounds.y + 13), Text.literal(Money.format(display.price))).leftAligned().color(0xFFD24A))
        return w
    }
}
