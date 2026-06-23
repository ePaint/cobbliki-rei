package com.cobbliki.rei.category

import com.cobbliki.rei.Categories
import com.cobbliki.rei.CurrencyIcon
import com.cobbliki.rei.Money
import com.cobbliki.rei.coin
import com.cobbliki.rei.display.BuyDisplay
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.gui.Renderer
import me.shedaniel.rei.api.client.gui.widgets.Widget
import me.shedaniel.rei.api.client.gui.widgets.Widgets
import me.shedaniel.rei.api.client.registry.display.DisplayCategory
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.util.EntryStacks
import net.minecraft.text.Text

class BuyCategory : DisplayCategory<BuyDisplay> {
    override fun getCategoryIdentifier(): CategoryIdentifier<BuyDisplay> = Categories.BUY
    override fun getTitle(): Text = Text.translatable("category.cobbliki_rei.buy")
    override fun getIcon(): Renderer = CurrencyIcon
    override fun getDisplayHeight(): Int = 42

    override fun setupDisplay(display: BuyDisplay, bounds: Rectangle): List<Widget> {
        val w = mutableListOf<Widget>()
        w.add(Widgets.createRecipeBase(bounds))
        w.add(coin(bounds.x + 10, bounds.y + 9, 16))
        w.add(Widgets.createLabel(Point(bounds.x + 30, bounds.y + 12), Text.literal(Money.format(display.price))).leftAligned().color(0x55FF55))
        w.add(Widgets.createArrow(Point(bounds.x + 84, bounds.y + 8)))
        w.add(Widgets.createSlot(Point(bounds.x + 116, bounds.y + 7)).entry(EntryStacks.of(display.stack)).markOutput())
        w.add(Widgets.createLabel(Point(bounds.x + 10, bounds.y + 30), Text.translatable("category.cobbliki_rei.vendor", display.category)).leftAligned().noShadow().color(0xAAAAAA))
        return w
    }
}
