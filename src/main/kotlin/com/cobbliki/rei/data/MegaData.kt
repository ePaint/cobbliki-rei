package com.cobbliki.rei.data

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.pokemon.Species
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

data class MegaEvo(
    val stone: ItemStack,
    val species: Species,
    val baseAspects: Set<String>,
    val resultAspects: Set<String>,
    val resultName: String,
)

object MegaData {
    val present: Boolean get() = FabricLoader.getInstance().isModLoaded("mega_showdown")

    fun entries(): List<MegaEvo> {
        val seen = HashSet<String>()
        val out = ArrayList<MegaEvo>()
        for (e in MegaIndex.entries()) {
            val stone = resolveStone(e.namespace, e.itemKey)
            if (stone == Items.AIR) continue
            val (resultSpecies, megaAspects) = resolveForme(e.megaStone) ?: continue
            for (baseForme in e.baseFormes) {
                val (baseSpecies, baseAspects) = resolveForme(baseForme) ?: continue
                if (baseSpecies != resultSpecies) continue
                val resultAspects = baseAspects + megaAspects
                val name = formName(resultSpecies, resultAspects) ?: continue
                if (!seen.add("${baseSpecies.name}|$baseAspects|$resultAspects")) continue
                out.add(MegaEvo(ItemStack(stone), baseSpecies, baseAspects, resultAspects, name))
            }
        }
        return out
    }

    private fun resolveForme(forme: String): Pair<Species, Set<String>>? {
        PokemonSpecies.getByName(forme.lowercase())?.let { return it to emptySet() }
        val idx = forme.indexOf('-')
        if (idx < 0) return null
        val species = PokemonSpecies.getByName(forme.substring(0, idx).lowercase()) ?: return null
        val rest = forme.substring(idx + 1)
        val form = species.forms.firstOrNull { it.name.replace(" ", "-").equals(rest.replace(" ", "-"), true) }
        return species to (form?.aspects?.toSet() ?: emptySet())
    }

    private fun formName(species: Species, aspects: Set<String>): String? =
        species.forms.firstOrNull { it.aspects.toSet() == aspects }?.name

    private fun resolveStone(ns: String, key: String): Item {
        val direct = Registries.ITEM.get(Identifier.of(ns, key))
        if (direct != Items.AIR) return direct
        if (key.length > 1 && (key.endsWith("x") || key.endsWith("y"))) {
            val alt = Registries.ITEM.get(Identifier.of(ns, key.dropLast(1) + "_" + key.last()))
            if (alt != Items.AIR) return alt
        }
        return direct
    }
}
