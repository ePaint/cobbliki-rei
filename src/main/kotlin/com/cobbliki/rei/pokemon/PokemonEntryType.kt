package com.cobbliki.rei.pokemon

import com.cobblemon.mod.common.pokemon.Species
import com.cobbliki.rei.CobblikiRei
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.entry.EntryStack
import me.shedaniel.rei.api.common.entry.type.EntryType
import net.minecraft.util.Identifier

object PokemonEntryType {
    val ID: Identifier = Identifier.of(CobblikiRei.MOD_ID, "pokemon")
    val TYPE: EntryType<PokemonForm> = EntryType.deferred(ID)

    fun stack(species: Species, aspects: Set<String> = emptySet()): EntryStack<PokemonForm> =
        EntryStack.of(TYPE, PokemonForm(species, aspects))

    fun ingredient(species: Species, aspects: Set<String> = emptySet()): EntryIngredient =
        EntryIngredient.of(stack(species, aspects))
}
