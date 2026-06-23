package com.cobbliki.rei

import com.cobblemon.mod.common.pokemon.Species
import com.cobbliki.rei.pokemon.PokemonEntryType
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.gui.widgets.Widget
import me.shedaniel.rei.api.client.gui.widgets.Widgets
import net.minecraft.text.Text
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

fun pokemonWidget(species: Species, x: Int, y: Int, size: Int, aspects: Set<String> = emptySet()): Widget =
    Widgets.createSlot(Rectangle(x, y, size, size)).entry(PokemonEntryType.stack(species, aspects)).disableBackground()

fun nameLabel(species: Species, cx: Int, y: Int): Widget =
    Widgets.createLabel(Point(cx, y), species.translatedName).noShadow().color(0xE0E0E0)

fun arrow(cx: Int, cy: Int): Widget =
    Widgets.createLabel(Point(cx, cy), Text.literal("→")).noShadow().color(0xD0D0D0)

fun coin(x: Int, y: Int, size: Int): Widget =
    Widgets.createDrawableWidget { ctx, _, _, _ -> CurrencyIcon.blit(ctx, x, y, size) }

fun merchantWidget(x: Int, y: Int, box: Int): Widget = MerchantRender.widget(x, y, box)

fun itemIcon(primary: String, fallback: String): me.shedaniel.rei.api.client.gui.Renderer {
    val p = Registries.ITEM.get(Identifier.of(primary))
    val item = if (p != Items.AIR) p else Registries.ITEM.get(Identifier.of(fallback))
    return me.shedaniel.rei.api.common.util.EntryStacks.of(item)
}

fun tmcraftDiscs(moveId: String): List<ItemStack> =
    listOf("tm", "tutor", "egg", "star").mapNotNull { prefix ->
        val item = Registries.ITEM.get(Identifier.of("tmcraft", "${prefix}_$moveId"))
        if (item != Items.AIR) ItemStack(item) else null
    }
