package com.cobbliki.rei.data

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.pokemon.Species
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.text.Text
import net.minecraft.util.Identifier

data class EvoEntry(
    val from: Species,
    val fromAspects: Set<String>,
    val to: Species,
    val toAspects: Set<String>,
    val item: ItemStack?,
    val method: Text,
)

object EvolutionData {
    private val KNOWN = setOf("item", "trade", "friendship", "levelup")

    // SANCTIONED HARDCODE (see rei-mod/CLAUDE.md): LumyMon toggles these forms via a right-click
    // item-interaction in compiled code, exposing NO data file or registry to read. The link only
    // exists in code + tooltip prose, so there is nothing to ingest. Keep in sync with LumyMon.
    private val LUMY_FORM_ITEMS = listOf(
        Triple("mewtwo", "lumymon:rocket_armor", "armored"),
        Triple("lugia", "lumymon:shadow_soul_stone", "shadow"),
    )

    fun entries(): List<EvoEntry> {
        val edges = SpeciesMovesIndex.evolutions()
        val cables = edges.filter { it.methodKey == "trade" && it.item == null }
            .map { it.copy(item = "cobblemon:link_cable", methodKey = "item", level = null) }
        return ((edges + cables).mapNotNull(::resolve) + lumyFormItems()).distinctBy(::keyOf)
    }

    private fun lumyFormItems(): List<EvoEntry> = LUMY_FORM_ITEMS.mapNotNull { (name, itemId, aspect) ->
        val species = PokemonSpecies.getByName(name) ?: return@mapNotNull null
        val item = Identifier.tryParse(itemId)?.let { Registries.ITEM.get(it) } ?: return@mapNotNull null
        if (item == Items.AIR) return@mapNotNull null
        EvoEntry(species, emptySet(), species, setOf(aspect), ItemStack(item), Text.translatable("category.cobbliki_rei.method.item"))
    }

    private fun resolve(e: EvoEdge): EvoEntry? {
        val from = PokemonSpecies.getByIdentifier(Identifier.tryParse(e.fromId) ?: return null) ?: return null
        val tokens = e.resultRaw.trim().split(" ")
        val to = PokemonSpecies.getByName(tokens.first().lowercase()) ?: return null
        val aspects = tokens.drop(1).mapNotNull { t ->
            when {
                "=" !in t -> t
                t.startsWith("aspect=") -> t.substringAfter("=")
                else -> null
            }
        }.toSet()
        val item = e.item?.let { Identifier.tryParse(it)?.let { id -> ItemStack(Registries.ITEM.get(id)) } }
        return EvoEntry(from, e.fromAspects, to, aspects, item, methodText(e.methodKey, e.level))
    }

    private fun methodText(key: String, level: Int?): Text = when {
        key == "level" && level != null -> Text.translatable("category.cobbliki_rei.method.level", level)
        key in KNOWN -> Text.translatable("category.cobbliki_rei.method.$key")
        else -> Text.literal(key.replaceFirstChar { it.uppercase() })
    }

    private fun keyOf(e: EvoEntry): String =
        "${e.from.resourceIdentifier}#${e.fromAspects.sorted()}>${e.to.resourceIdentifier}#${e.toAspects.sorted()}|" +
            (e.item?.let { Registries.ITEM.getId(it.item).toString() } ?: "")
}
