package com.cobbliki.rei.pokemon

import com.cobblemon.mod.common.client.gui.drawProfilePokemon
import com.cobblemon.mod.common.client.render.models.blockbench.FloatingState
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.entry.renderer.EntryRenderer
import me.shedaniel.rei.api.client.gui.widgets.Tooltip
import me.shedaniel.rei.api.client.gui.widgets.TooltipContext
import me.shedaniel.rei.api.common.entry.EntryStack
import net.minecraft.client.gui.DrawContext
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.joml.Quaternionf

object PokemonEntryRenderer : EntryRenderer<PokemonForm> {
    override fun render(entry: EntryStack<PokemonForm>, context: DrawContext, bounds: Rectangle, mouseX: Int, mouseY: Int, delta: Float) {
        val form = entry.value ?: return
        runCatching {
            val matrices = context.matrices
            matrices.push()
            matrices.translate(bounds.centerX.toDouble(), bounds.y.toDouble() + 2.0, 100.0)
            val rotation = Quaternionf().rotationXYZ(Math.toRadians(13.0).toFloat(), Math.toRadians(35.0).toFloat(), 0f)
            drawProfilePokemon(
                renderablePokemon = RenderablePokemon(form.species, form.aspects, ItemStack.EMPTY),
                matrixStack = matrices,
                rotation = rotation,
                state = FloatingState(),
                partialTicks = 0f,
                scale = bounds.height * 0.42f,
            )
            matrices.pop()
        }
    }

    override fun getTooltip(entry: EntryStack<PokemonForm>, context: TooltipContext): Tooltip? {
        val form = entry.value ?: return null
        val formName = if (form.aspects.isEmpty()) null
        else form.species.forms.firstOrNull { it.aspects.toSet() == form.aspects }?.name?.takeIf { it.isNotBlank() }
        val title = if (formName != null) Text.literal("${form.species.translatedName.string} ($formName)") else form.species.translatedName
        return Tooltip.create(title, Text.literal("#%04d".format(form.species.nationalPokedexNumber)).formatted(Formatting.GRAY))
    }
}
