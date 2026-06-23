package com.cobbliki.rei.pokemon

import com.cobblemon.mod.common.pokemon.Species

class PokemonForm(val species: Species, val aspects: Set<String>) {
    val key: String = species.resourceIdentifier.toString() + "#" + aspects.toSortedSet().joinToString(",")
}
