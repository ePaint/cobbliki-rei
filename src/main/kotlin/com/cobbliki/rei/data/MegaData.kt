package com.cobbliki.rei.data

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.pokemon.Species
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

data class MegaEvo(val stone: ItemStack, val base: Species, val megaForme: String, val aspect: String)

object MegaData {
    val present: Boolean get() = FabricLoader.getInstance().isModLoaded("mega_showdown")

    fun entries(): List<MegaEvo> = MegaIndex.entries().mapNotNull { e ->
        val base = PokemonSpecies.getByName(e.baseName.lowercase()) ?: return@mapNotNull null
        MegaEvo(ItemStack(resolveStone(e.itemKey)), base, e.megaForme, aspectOf(e.megaForme))
    }

    private fun resolveStone(key: String): Item {
        val direct = Registries.ITEM.get(Identifier.of("mega_showdown", key))
        if (direct != Items.AIR) return direct
        if (key.length > 1 && (key.endsWith("x") || key.endsWith("y"))) {
            val alt = Registries.ITEM.get(Identifier.of("mega_showdown", key.dropLast(1) + "_" + key.last()))
            if (alt != Items.AIR) return alt
        }
        return direct
    }

    private fun aspectOf(forme: String): String = when {
        forme.endsWith("-X", true) -> "mega_x"
        forme.endsWith("-Y", true) -> "mega_y"
        else -> "mega"
    }
}
