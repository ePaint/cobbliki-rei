package com.cobbliki.rei.category

import com.cobbliki.rei.Categories
import com.cobbliki.rei.data.DexData
import com.cobbliki.rei.data.SpawnInfo
import com.cobbliki.rei.data.prettyResource
import com.cobbliki.rei.display.DexDisplay
import com.cobbliki.rei.nameLabels
import com.cobbliki.rei.pokemonWidget
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.gui.Renderer
import me.shedaniel.rei.api.client.gui.widgets.Widget
import me.shedaniel.rei.api.client.gui.widgets.Widgets
import me.shedaniel.rei.api.client.registry.display.DisplayCategory
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import net.minecraft.util.Identifier

private const val WIDTH = 196
private const val MODEL = 56

class DexCategory : DisplayCategory<DexDisplay> {
    private val typeSheet = Identifier.of("cobblemon", "textures/gui/types_small.png")

    override fun getCategoryIdentifier(): CategoryIdentifier<DexDisplay> = Categories.DEX
    override fun getTitle(): Text = Text.translatable("category.cobbliki_rei.dex")
    override fun getIcon(): Renderer = com.cobbliki.rei.itemIcon("cobblemon:pokedex", "cobblemon:poke_ball")
    override fun getDisplayWidth(display: DexDisplay): Int = WIDTH
    override fun getDisplayHeight(): Int = 150

    override fun setupDisplay(display: DexDisplay, bounds: Rectangle): List<Widget> {
        val w = mutableListOf<Widget>()
        w.add(Widgets.createRecipeBase(bounds))
        w.add(pokemonWidget(display.species, bounds.x + 6, bounds.y + 6, MODEL))
        w.addAll(nameLabels(display.species, emptySet(), bounds.x + 6 + MODEL / 2, bounds.y + MODEL + 8, MODEL + 4))
        val tx = bounds.x + MODEL + 14
        if (display.isBio) bioSection(display, bounds, tx, w) else spawnSection(display.spawns!!, bounds, tx, w)
        return w
    }

    private fun bioSection(display: DexDisplay, bounds: Rectangle, tx: Int, w: MutableList<Widget>) {
        val bio = DexData.bioOf(display.species)
        var y = bounds.y + 8
        w.add(label(tx, y, Text.literal("#%04d".format(bio.dex)), 0xFFFFFF)); y += 11
        val types = bio.types
        w.add(typeIcons(tx, y, types)); y += 20
        if (bio.abilities.isNotEmpty()) {
            w.add(label(tx, y, Text.translatable("category.cobbliki_rei.dex.abilities"), 0x8FA0B0)); y += 10
            bio.abilities.forEach { w.add(label(tx, y, it, 0xC9C9C9)); y += 9 }
        }
        var sy = bounds.y + 8
        bio.baseStats.forEach { (name, value) ->
            w.add(label(bounds.x + WIDTH - 6, sy, Text.translatable("category.cobbliki_rei.dex.stat", name, value), 0xB0B0B0, right = true)); sy += 9
        }
        sy += 4
        w.add(label(bounds.x + WIDTH - 6, sy, line("category.cobbliki_rei.dex.catch", bio.catchRate), 0x9AA0A6, right = true)); sy += 9
        w.add(label(bounds.x + WIDTH - 6, sy, line("category.cobbliki_rei.dex.exp", bio.baseExp), 0x9AA0A6, right = true)); sy += 9
        w.add(label(bounds.x + WIDTH - 6, sy, line("category.cobbliki_rei.dex.friendship", bio.friendship), 0x9AA0A6, right = true)); sy += 9
        val ey = maxOf(y, bounds.y + 86)
        w.add(label(tx, ey, Text.translatable("category.cobbliki_rei.dex.${bio.genderKey}"), 0x9AA0A6))
        w.add(label(tx, ey + 9, Text.translatable("category.cobbliki_rei.dex.egg", bio.eggGroups.joinToString(", ").ifBlank { "—" }), 0x9AA0A6))
        w.add(label(tx, ey + 18, Text.translatable("category.cobbliki_rei.dex.size", "%.1f".format(bio.height / 10f), "%.1f".format(bio.weight / 10f)), 0x9AA0A6))
        if (DexData.spawnsOf(display.species).isEmpty())
            w.add(label(tx, ey + 27, Text.translatable("category.cobbliki_rei.dex.no_spawn"), 0x8A8A8A))
    }

    private fun spawnSection(spawns: List<SpawnInfo>, bounds: Rectangle, tx: Int, w: MutableList<Widget>) {
        var y = bounds.y + 8
        w.add(label(tx, y, Text.translatable("category.cobbliki_rei.dex.spawn"), 0xFFFFFF)); y += 11
        val maxPx = bounds.x + WIDTH - 6 - tx
        spawns.forEach { sp ->
            sp.aspects.takeIf { it.isNotEmpty() }?.let {
                w.add(label(tx, y, Text.literal(it.joinToString(" ") { a -> a.replaceFirstChar(Char::uppercase) }), 0xC9A0E0)); y += 9
            }
            val meta = listOfNotNull(
                Text.translatable("category.cobbliki_rei.dex.bucket.${sp.bucket.replace('-', '_')}").string,
                sp.level?.let { Text.translatable("category.cobbliki_rei.dex.level", it).string },
                sp.timeRange?.let { Text.translatable("category.cobbliki_rei.dex.time.$it").string },
            ).joinToString(" · ")
            w.add(label(tx, y, Text.literal(meta), 0xB0B0B0)); y += 9
            val where = (sp.biomes.map { Text.translatable("category.cobbliki_rei.dex.biome", prettyResource(it)).string } +
                sp.structures.map { Text.translatable("category.cobbliki_rei.dex.structure", prettyResource(it)).string })
            where.forEach { line ->
                wrap(line, maxPx).forEach { w.add(label(tx + 4, y, Text.literal(it), 0x9AA0A6)); y += 9 }
            }
            y += 3
        }
    }

    private fun typeIcons(x: Int, y: Int, types: List<Pair<Text, Int>>): Widget =
        Widgets.createDrawableWidget { ctx, _, _, _ ->
            types.forEachIndexed { i, (_, col) ->
                ctx.drawTexture(typeSheet, x + i * 40, y, 36, 18, (col * 18).toFloat(), 0f, 18, 18, 324, 18)
            }
        }

    private fun label(x: Int, y: Int, text: Text, color: Int, right: Boolean = false): Widget {
        val l = Widgets.createLabel(Point(x, y), text).noShadow().color(color)
        return if (right) l.rightAligned() else l.leftAligned()
    }

    private fun line(key: String, value: Int): Text = Text.translatable(key, value)

    private fun wrap(s: String, maxPx: Int): List<String> {
        val font = MinecraftClient.getInstance().textRenderer
        if (font.getWidth(s) <= maxPx) return listOf(s)
        val lines = mutableListOf<String>()
        var cur = StringBuilder()
        for (word in s.split(" ")) {
            val tentative = if (cur.isEmpty()) word else "$cur $word"
            if (cur.isNotEmpty() && font.getWidth(tentative) > maxPx) { lines.add(cur.toString()); cur = StringBuilder(word) }
            else cur = StringBuilder(tentative)
        }
        if (cur.isNotEmpty()) lines.add(cur.toString())
        return lines.take(3)
    }
}
