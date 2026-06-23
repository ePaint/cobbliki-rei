package com.cobbliki.rei.data

import com.cobblemon.mod.common.api.fossil.Fossils
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.pokemon.Species
import net.minecraft.item.ItemStack

data class FossilEntry(val ingredients: List<List<ItemStack>>, val result: Species, val aspects: Set<String>)

object FossilData {
    fun entries(): List<FossilEntry> = Fossils.all().mapNotNull { f ->
        val sp = f.result.species ?: return@mapNotNull null
        val species = PokemonSpecies.getByName(sp.lowercase()) ?: return@mapNotNull null
        val ingredients = f.fossils.map { pred ->
            pred.items().map { rel -> rel.stream().map { ItemStack(it.value()) }.toList() }.orElse(emptyList<ItemStack>())
        }.filter { it.isNotEmpty() }
        if (ingredients.isEmpty()) return@mapNotNull null
        FossilEntry(ingredients, species, f.result.aspects.toSet())
    }
}
