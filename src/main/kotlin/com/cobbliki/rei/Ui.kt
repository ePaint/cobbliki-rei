package com.cobbliki.rei

import com.cobblemon.mod.common.pokemon.Species
import com.cobbliki.rei.pokemon.PokemonEntryType
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.gui.widgets.Widget
import me.shedaniel.rei.api.client.gui.widgets.Widgets
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

fun pokemonWidget(species: Species, x: Int, y: Int, size: Int, aspects: Set<String> = emptySet()): Widget =
    Widgets.createSlot(Rectangle(x, y, size, size)).entry(PokemonEntryType.stack(species, aspects)).disableBackground()

fun nameLabel(species: Species, cx: Int, y: Int): Widget =
    Widgets.createLabel(Point(cx, y), species.translatedName).noShadow().color(0xE0E0E0)

fun formName(species: Species, aspects: Set<String>): String? =
    if (aspects.isEmpty()) null
    else species.forms.firstOrNull { it.aspects.toSet() == aspects }?.name?.takeIf { it.isNotBlank() }

fun formText(species: Species, aspects: Set<String>): Text {
    val fn = formName(species, aspects) ?: return species.translatedName
    return Text.empty().append(species.translatedName).append(" ($fn)")
}

fun nameLabels(species: Species, aspects: Set<String>, cx: Int, y: Int, maxPx: Int, color: Int = 0xE0E0E0): List<Widget> {
    val text = formText(species, aspects)
    val font = MinecraftClient.getInstance().textRenderer
    if (font.getWidth(text) <= maxPx) return listOf(Widgets.createLabel(Point(cx, y), text).noShadow().color(color))
    val lines = mutableListOf<String>()
    var cur = StringBuilder()
    for (word in text.string.split(" ")) {
        val t = if (cur.isEmpty()) word else "$cur $word"
        if (cur.isNotEmpty() && font.getWidth(t) > maxPx) { lines.add(cur.toString()); cur = StringBuilder(word) }
        else cur = StringBuilder(t)
    }
    if (cur.isNotEmpty()) lines.add(cur.toString())
    return lines.take(2).mapIndexed { i, ln -> Widgets.createLabel(Point(cx, y + i * 9), Text.literal(ln)).noShadow().color(color) }
}

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
