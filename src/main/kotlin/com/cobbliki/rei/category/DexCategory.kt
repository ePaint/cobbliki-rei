package com.cobbliki.rei.category

import com.cobblemon.mod.common.api.types.ElementalType
import com.cobbliki.rei.Categories
import com.cobbliki.rei.data.AbilityInfo
import com.cobbliki.rei.data.AbilityPage
import com.cobbliki.rei.data.DexBio
import com.cobbliki.rei.data.DexData
import com.cobbliki.rei.data.RideInfo
import com.cobbliki.rei.data.RidePage
import com.cobbliki.rei.data.SpawnInfo
import com.cobbliki.rei.data.SpawnPage
import com.cobbliki.rei.data.TypeChartIndex
import com.cobbliki.rei.data.prettyResource
import com.cobbliki.rei.display.DexDisplay
import com.cobbliki.rei.formText
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
private const val HEIGHT = 206
private const val MODEL = 56
private const val PAD = 6
private const val ROW = 10
private const val TYPE_ICON = 18

class DexCategory : DisplayCategory<DexDisplay> {
    private val typeSheet = Identifier.of("cobblemon", "textures/gui/types_small.png")

    override fun getCategoryIdentifier(): CategoryIdentifier<DexDisplay> = Categories.DEX
    override fun getTitle(): Text = Text.translatable("category.cobbliki_rei.dex")
    override fun getIcon(): Renderer = com.cobbliki.rei.itemIcon("cobblemon:pokedex", "cobblemon:poke_ball")
    override fun getDisplayWidth(display: DexDisplay): Int = WIDTH
    override fun getDisplayHeight(): Int = HEIGHT

    override fun setupDisplay(display: DexDisplay, bounds: Rectangle): List<Widget> {
        val w = mutableListOf<Widget>()
        w.add(Widgets.createRecipeBase(bounds))
        when (val p = display.payload) {
            null -> {
                w.add(pokemonWidget(display.species, bounds.x + PAD, bounds.y + PAD, MODEL, display.aspects))
                w.addAll(nameLabels(display.species, display.aspects, bounds.x + PAD + MODEL / 2, bounds.y + MODEL + 10, MODEL + 8))
                headerSection(display, bounds, w)
            }
            is AbilityPage -> abilitySection(p, bounds, w, compactHeader(display, bounds, "category.cobbliki_rei.dex.abilities", w))
            is RidePage -> rideSection(p.ride, bounds, w, compactHeader(display, bounds, "category.cobbliki_rei.dex.ride", w))
            is SpawnPage -> spawnSection(p.spawns, bounds, w, compactHeader(display, bounds, "category.cobbliki_rei.dex.spawn", w))
        }
        return w
    }

    private fun compactHeader(display: DexDisplay, bounds: Rectangle, sectionKey: String, w: MutableList<Widget>): Int {
        val left = bounds.x + PAD
        w.add(label(left, bounds.y + PAD, formText(display.species, display.aspects), 0xFFFFFF))
        w.add(label(bounds.x + WIDTH - PAD, bounds.y + PAD, Text.translatable(sectionKey), 0x8FA0B0, right = true))
        return bounds.y + PAD + ROW + 4
    }

    private fun headerSection(display: DexDisplay, bounds: Rectangle, w: MutableList<Widget>) {
        val bio = DexData.bioOf(display.species, display.aspects)
        val tx = bounds.x + PAD + MODEL + 8
        val dex = Text.literal("#%04d".format(bio.dex))
        val hy = bounds.y + PAD + 2
        w.add(label(tx, hy, dex, 0xFFFFFF))
        val font = MinecraftClient.getInstance().textRenderer
        addTypes(bio, tx + font.getWidth(dex) + 6, hy - (TYPE_ICON - 8) / 2, w)
        var y = bounds.y + MODEL + 34
        w.add(label(bounds.x + PAD, y, Text.translatable("category.cobbliki_rei.dex.stat_header"), 0x8FA0B0)); y += ROW + 2
        bio.baseStats.forEach { (name, value) ->
            w.add(label(bounds.x + PAD, y, name, 0xB0B0B0))
            w.add(label(bounds.x + WIDTH - PAD, y, Text.literal(value.toString()), 0xE0E0E0, right = true))
            y += ROW
        }
        if (DexData.spawnsOf(display.species, display.aspects).isEmpty()) {
            y += 4
            w.add(label(bounds.x + PAD, y, Text.translatable("category.cobbliki_rei.dex.no_spawn"), 0x8A8A8A))
        }
    }

    private fun addTypes(bio: DexBio, x: Int, y: Int, w: MutableList<Widget>) {
        w.add(typeIcons(x, y, bio.types))
        val cols = bio.types.size.coerceAtLeast(1)
        w.add(Widgets.createTooltip(Rectangle(x, y, cols * (TYPE_ICON + 2), TYPE_ICON), matchupTooltip(bio.primaryType, bio.secondaryType)))
    }

    private fun matchupTooltip(primary: ElementalType, secondary: ElementalType?): List<Text> {
        val ms = TypeChartIndex.matchups(primary, secondary)
        val weak = ms.filter { it.multiplier > 1.0 }.sortedByDescending { it.multiplier }
        val resist = ms.filter { it.multiplier in 0.01..0.99 }.sortedBy { it.multiplier }
        val immune = ms.filter { it.multiplier == 0.0 }
        val out = mutableListOf<Text>()
        section("category.cobbliki_rei.dex.weak", weak, true)?.let { out.add(it) }
        section("category.cobbliki_rei.dex.resist", resist, true)?.let { out.add(it) }
        section("category.cobbliki_rei.dex.immune", immune, false)?.let { out.add(it) }
        if (out.isEmpty()) out.add(Text.translatable("category.cobbliki_rei.dex.neutral"))
        return out
    }

    private fun section(key: String, ms: List<com.cobbliki.rei.data.TypeMatch>, withMult: Boolean): Text? {
        if (ms.isEmpty()) return null
        val body = Text.empty()
        ms.forEachIndexed { i, m ->
            if (i > 0) body.append(Text.literal(", "))
            body.append(m.attacking.displayName)
            if (withMult) body.append(Text.literal(" ")).append(Text.translatable("category.cobbliki_rei.dex.mult", fmtMult(m.multiplier)))
        }
        return Text.empty().append(Text.translatable(key)).append(Text.literal(": ")).append(body)
    }

    private fun fmtMult(m: Double): String = if (m == m.toLong().toDouble()) m.toLong().toString() else m.toString()

    private fun abilitySection(page: AbilityPage, bounds: Rectangle, w: MutableList<Widget>, top: Int) {
        val left = bounds.x + PAD
        val maxPx = WIDTH - 2 * PAD
        var y = top
        page.abilities.forEach { ab ->
            w.add(label(left, y, abilityName(ab), 0xFFFFFF)); y += ROW
            wrap(ab.description.string, maxPx).forEach { ln ->
                w.add(label(left + 4, y, Text.literal(ln), 0x9AA0A6)); y += ROW - 1
            }
            y += 3
        }
        y += 2
        bioLines(page.bio).forEach { t ->
            wrap(t.string, maxPx).forEach { ln -> w.add(label(left, y, Text.literal(ln), 0x9AA0A6)); y += ROW - 1 }
        }
    }

    private fun abilityName(ab: AbilityInfo): Text {
        if (!ab.hidden) return ab.name
        return Text.empty().append(ab.name).append(Text.literal(" ")).append(Text.translatable("category.cobbliki_rei.dex.hidden"))
    }

    private fun bioLines(bio: DexBio): List<Text> = listOf(
        line("category.cobbliki_rei.dex.catch", bio.catchRate),
        line("category.cobbliki_rei.dex.exp", bio.baseExp),
        line("category.cobbliki_rei.dex.friendship", bio.friendship),
        Text.translatable("category.cobbliki_rei.dex.${bio.genderKey}"),
        Text.translatable("category.cobbliki_rei.dex.egg", bio.eggGroups.joinToString(", ").ifBlank { "—" }),
        Text.translatable("category.cobbliki_rei.dex.size", "%.1f".format(bio.height / 10f), "%.1f".format(bio.weight / 10f)),
    )

    private fun rideSection(ride: RideInfo, bounds: Rectangle, w: MutableList<Widget>, top: Int) {
        val left = bounds.x + PAD
        val right = bounds.x + WIDTH - PAD
        var y = top
        if (ride.seats > 0) {
            w.add(label(left, y, Text.translatable("category.cobbliki_rei.dex.ride.seats", ride.seats), 0xB0B0B0)); y += ROW + 2
        }
        ride.styles.forEach { st ->
            w.add(label(left, y, Text.translatable("category.cobbliki_rei.dex.ride.style.${st.style.name.lowercase()}"), 0x8FA0B0)); y += ROW + 1
            st.stats.forEach { (stat, range) ->
                w.add(label(left + 4, y, Text.translatable("category.cobbliki_rei.dex.ride.stat.${stat.name.lowercase()}"), 0xB0B0B0))
                w.add(label(right, y, Text.literal("${range.first}–${range.last}"), 0xE0E0E0, right = true))
                y += ROW
            }
            y += 4
        }
    }

    private fun spawnSection(spawns: List<SpawnInfo>, bounds: Rectangle, w: MutableList<Widget>, top: Int) {
        val left = bounds.x + PAD
        val maxPx = WIDTH - 2 * PAD
        var y = top
        spawns.forEach { sp ->
            sp.aspects.takeIf { it.isNotEmpty() }?.let {
                w.add(label(left, y, Text.literal(it.joinToString(" ") { a -> a.replaceFirstChar(Char::uppercase) }), 0xC9A0E0)); y += ROW
            }
            val meta = listOfNotNull(
                Text.translatable("category.cobbliki_rei.dex.bucket.${sp.bucket.replace('-', '_')}").string,
                sp.level?.let { Text.translatable("category.cobbliki_rei.dex.level", it).string },
                sp.context?.let { Text.translatable("category.cobbliki_rei.dex.context.$it").string },
            ).joinToString(" · ")
            w.add(label(left, y, Text.literal(meta), 0xB0B0B0)); y += ROW
            spawnLines(sp).forEach { t ->
                wrap(t.string, maxPx - 4).forEach { ln -> w.add(label(left + 4, y, Text.literal(ln), 0x9AA0A6)); y += ROW - 1 }
            }
            y += 4
        }
    }

    private fun spawnLines(sp: SpawnInfo): List<Text> {
        val out = mutableListOf<Text>()
        if (sp.structures.isNotEmpty())
            out.add(joinList("category.cobbliki_rei.dex.structure", sp.structures))
        if (sp.biomes.isNotEmpty())
            out.add(joinList("category.cobbliki_rei.dex.biome", sp.biomes))
        if (sp.dimensions.isNotEmpty())
            out.add(joinList("category.cobbliki_rei.dex.dimension", sp.dimensions))
        whenLine(sp)?.let { out.add(it) }
        lightLine(sp)?.let { out.add(it) }
        heightLine(sp)?.let { out.add(it) }
        if (sp.neededNearbyBlocks.isNotEmpty())
            out.add(joinList("category.cobbliki_rei.dex.nearby", sp.neededNearbyBlocks))
        if (sp.neededBaseBlocks.isNotEmpty())
            out.add(joinList("category.cobbliki_rei.dex.base", sp.neededBaseBlocks))
        if (sp.bobberBait) out.add(Text.translatable("category.cobbliki_rei.dex.fishing"))
        return out
    }

    private fun whenLine(sp: SpawnInfo): Text? {
        val parts = mutableListOf<String>()
        sp.timeRange?.let { parts.add(Text.translatable("category.cobbliki_rei.dex.time.$it").string) }
        if (sp.isThundering == true) parts.add(Text.translatable("category.cobbliki_rei.dex.weather.thunder").string)
        else if (sp.isRaining == true) parts.add(Text.translatable("category.cobbliki_rei.dex.weather.rain").string)
        sp.moonPhase?.let { parts.add(Text.translatable("category.cobbliki_rei.dex.moon", it).string) }
        if (sp.canSeeSky == false) parts.add(Text.translatable("category.cobbliki_rei.dex.underground").string)
        if (parts.isEmpty()) return null
        return Text.translatable("category.cobbliki_rei.dex.when", parts.joinToString(" · "))
    }

    private fun lightLine(sp: SpawnInfo): Text? {
        val lo = sp.minLight ?: sp.minSkyLight
        val hi = sp.maxLight ?: sp.maxSkyLight
        if (lo == null && hi == null) return null
        return Text.translatable("category.cobbliki_rei.dex.light", lo ?: 0, hi ?: 15)
    }

    private fun heightLine(sp: SpawnInfo): Text? {
        if (sp.minY == null && sp.maxY == null) return null
        return Text.translatable("category.cobbliki_rei.dex.height", sp.minY?.toString() ?: "?", sp.maxY?.toString() ?: "?")
    }

    private fun joinList(key: String, ids: List<String>): Text =
        Text.translatable(key, ids.joinToString(", ") { prettyResource(it) })

    private fun typeIcons(x: Int, y: Int, types: List<Pair<Text, Int>>): Widget =
        Widgets.createDrawableWidget { ctx, _, _, _ ->
            types.forEachIndexed { i, (_, col) ->
                ctx.drawTexture(typeSheet, x + i * (TYPE_ICON + 2), y, TYPE_ICON, TYPE_ICON, (col * 18).toFloat(), 0f, 18, 18, 324, 18)
            }
        }

    private fun label(x: Int, y: Int, text: Text, color: Int, right: Boolean = false): Widget {
        val l = Widgets.createLabel(Point(x, y), text).noShadow().color(color)
        return if (right) l.rightAligned() else l.leftAligned()
    }

    private fun line(key: String, value: Int): Text = Text.translatable(key, value)

    private fun wrap(s: String, maxPx: Int): List<String> {
        if (s.isBlank()) return emptyList()
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
        return lines
    }
}
