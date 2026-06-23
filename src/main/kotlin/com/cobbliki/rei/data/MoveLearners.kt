package com.cobbliki.rei.data

import com.cobblemon.mod.common.pokemon.Species

data class MoveLearners(val move: MoveInfo, val species: List<Species>)

object MoveLearnerIndex {
    fun build(mons: List<PokemonInfo>): List<MoveLearners> {
        val moveById = LinkedHashMap<String, MoveInfo>()
        val speciesByMove = LinkedHashMap<String, MutableList<Species>>()
        for (info in mons) {
            (info.tmMoves + info.tutorMoves + info.eggMoves).distinctBy { it.id }.forEach { mv ->
                moveById.putIfAbsent(mv.id, mv)
                speciesByMove.getOrPut(mv.id) { mutableListOf() }.add(info.species)
            }
        }
        return moveById.map { (id, mv) -> MoveLearners(mv, speciesByMove.getValue(id).toList()) }
    }
}
