package com.cobbliki.rei.display

import com.cobbliki.rei.data.DexData
import com.cobbliki.rei.pokemon.PokemonEntryType
import com.cobbliki.rei.pokemon.PokemonForm
import me.shedaniel.rei.api.client.registry.display.DynamicDisplayGenerator
import me.shedaniel.rei.api.common.entry.EntryStack
import java.util.Optional

object DexGenerator : DynamicDisplayGenerator<DexDisplay> {
    override fun getRecipeFor(entry: EntryStack<*>): Optional<List<DexDisplay>> {
        if (entry.type != PokemonEntryType.TYPE) return Optional.empty()
        val form = entry.value as? PokemonForm ?: return Optional.empty()
        val pages = mutableListOf(DexDisplay(form.species, form.aspects, 0, null))
        DexData.spawnPages(form.species, form.aspects).forEachIndexed { i, page ->
            pages.add(DexDisplay(form.species, form.aspects, i + 1, page))
        }
        return Optional.of(pages)
    }
}
