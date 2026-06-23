package com.cobbliki.rei.pokemon

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import me.shedaniel.rei.api.common.entry.EntrySerializer
import me.shedaniel.rei.api.common.entry.EntryStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier

object PokemonEntrySerializer : EntrySerializer<PokemonForm> {
    override fun supportSaving(): Boolean = true
    override fun supportReading(): Boolean = true

    override fun save(entry: EntryStack<PokemonForm>, value: PokemonForm): NbtCompound = NbtCompound().apply {
        putString("id", value.species.resourceIdentifier.toString())
        putString("aspects", value.aspects.joinToString(","))
    }

    override fun read(tag: NbtCompound): PokemonForm {
        val id = Identifier.tryParse(tag.getString("id"))
        val species = id?.let { PokemonSpecies.getByIdentifier(it) } ?: PokemonSpecies.species.first()
        val aspects = tag.getString("aspects").split(",").filter { it.isNotEmpty() }.toSet()
        return PokemonForm(species, aspects)
    }
}
