package com.cobbliki.rei

import com.cobbliki.rei.category.BuyCategory
import com.cobbliki.rei.category.DropsCategory
import com.cobbliki.rei.category.EvoCategory
import com.cobbliki.rei.category.EvoItemCategory
import com.cobbliki.rei.category.FossilCategory
import com.cobbliki.rei.category.LEARNER_PAGE
import com.cobbliki.rei.category.LearnersCategory
import com.cobbliki.rei.category.MT_PAGE_SIZE
import com.cobbliki.rei.category.MoveDetailCategory
import com.cobbliki.rei.category.MegaCategory
import com.cobbliki.rei.category.MtCategory
import com.cobbliki.rei.category.PastureCategory
import com.cobbliki.rei.category.SellCategory
import com.cobbliki.rei.data.EconomyData
import com.cobbliki.rei.data.EvolutionData
import com.cobbliki.rei.data.FossilData
import com.cobbliki.rei.data.MegaData
import com.cobbliki.rei.data.MoveLearnerIndex
import com.cobbliki.rei.data.PastureConfig
import com.cobbliki.rei.data.PokemonData
import com.cobbliki.rei.display.BuyDisplay
import com.cobbliki.rei.display.DropsDisplay
import com.cobbliki.rei.display.EvoItemDisplay
import com.cobbliki.rei.display.FossilDisplay
import com.cobbliki.rei.display.LearnersDisplay
import com.cobbliki.rei.display.MegaDisplay
import com.cobbliki.rei.display.MoveDetailDisplay
import com.cobbliki.rei.display.MtDisplay
import com.cobbliki.rei.display.PastureDisplay
import com.cobbliki.rei.display.SellDisplay
import com.cobbliki.rei.currency.CurrencyEntryDefinition
import com.cobbliki.rei.currency.CurrencyEntryType
import com.cobbliki.rei.move.MoveEntryDefinition
import com.cobbliki.rei.move.MoveEntryType
import com.cobbliki.rei.pokemon.PokemonEntryDefinition
import com.cobbliki.rei.pokemon.PokemonEntryType
import me.shedaniel.rei.api.client.plugins.REIClientPlugin
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry
import me.shedaniel.rei.api.client.registry.entry.CollapsibleEntryRegistry
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry
import me.shedaniel.rei.api.common.entry.type.EntryTypeRegistry
import me.shedaniel.rei.api.common.util.EntryStacks
import net.minecraft.registry.Registries
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class CobblikiReiPlugin : REIClientPlugin {
    override fun registerEntryTypes(registry: EntryTypeRegistry) {
        registry.register(PokemonEntryType.TYPE, PokemonEntryDefinition)
        registry.register(MoveEntryType.TYPE, MoveEntryDefinition)
        registry.register(CurrencyEntryType.TYPE, CurrencyEntryDefinition)
    }

    override fun registerEntries(registry: EntryRegistry) {
        val mons = PokemonData.all()
        mons.forEach { info ->
            registry.addEntry(PokemonEntryType.stack(info.species))
            info.species.forms.forEach { form ->
                val aspects = form.aspects.toSet()
                if (aspects.isNotEmpty()) registry.addEntry(PokemonEntryType.stack(info.species, aspects))
            }
        }
        mons.flatMap { it.tmMoves + it.tutorMoves + it.eggMoves }
            .associateBy { it.id }.values
            .forEach { registry.addEntry(MoveEntryType.stack(it)) }
        if (EconomyData.present) registry.addEntry(CurrencyEntryType.stack())
    }

    override fun registerCollapsibleEntries(registry: CollapsibleEntryRegistry) {
        registry.group(Identifier.of(CobblikiRei.MOD_ID, "pokemon"), Text.translatable("group.cobbliki_rei.pokemon"),
            PokemonEntryType.TYPE) { true }
        registry.group(Identifier.of(CobblikiRei.MOD_ID, "moves"), Text.translatable("group.cobbliki_rei.moves"),
            MoveEntryType.TYPE) { true }
    }

    override fun registerCategories(registry: CategoryRegistry) {
        registry.add(DropsCategory())
        registry.add(MtCategory())
        registry.add(LearnersCategory())
        registry.add(MoveDetailCategory())
        registry.add(EvoCategory())
        registry.add(EvoItemCategory())
        registry.add(FossilCategory())
        registry.addWorkstations(Categories.FOSSIL, EntryStacks.of(item("cobblemon:restoration_tank")))
        if (PastureConfig.present) {
            registry.add(PastureCategory())
            registry.addWorkstations(Categories.PASTURE, EntryStacks.of(item("cobblemon:pasture")))
        }
        if (MegaData.present) {
            registry.add(MegaCategory())
            registry.addWorkstations(Categories.MEGA, EntryStacks.of(item("mega_showdown:mega_stone")))
        }
        if (EconomyData.present) {
            registry.add(BuyCategory())
            registry.add(SellCategory())
        }
    }

    override fun registerDisplays(registry: DisplayRegistry) {
        val mons = PokemonData.all()
        mons.forEach { info ->
            if (info.drops.isNotEmpty()) registry.add(DropsDisplay(info.species, info.drops))
            val formSets = (listOf(emptySet<String>()) + info.species.forms.map { it.aspects.toSet() }.filter { it.isNotEmpty() }).distinct()
            formSets.forEach { aspects ->
                val m = PokemonData.movesOf(info.species, aspects)
                (m.tm + m.tutor + m.egg).distinctBy { it.id }.chunked(MT_PAGE_SIZE)
                    .forEach { page -> registry.add(MtDisplay(info.species, aspects, page)) }
            }
        }
        MoveLearnerIndex.build(mons).forEach { ml ->
            registry.add(MoveDetailDisplay(ml.move))
            ml.species.chunked(LEARNER_PAGE).forEach { page -> registry.add(LearnersDisplay(ml.move, page)) }
        }
        EvolutionData.entries().forEach { registry.add(EvoItemDisplay(it.from, it.fromAspects, it.to, it.toAspects, it.item, it.method)) }
        FossilData.entries().forEach { registry.add(FossilDisplay(it.ingredients, it.result, it.aspects)) }
        if (MegaData.present)
            MegaData.entries().forEach { registry.add(MegaDisplay(it.stone, it.base, it.megaForme, it.aspect)) }
        if (PastureConfig.present) {
            val blacklist = PastureConfig.blacklist()
            val chance = PastureConfig.dropChancePerMinute()
            mons.forEach { info ->
                val pd = info.pastureDrops(blacklist)
                if (pd.isNotEmpty()) registry.add(PastureDisplay(info.species, pd, chance))
            }
        }
        if (EconomyData.present) {
            EconomyData.buyOffers().forEach { registry.add(BuyDisplay(it.stack, it.price, it.category)) }
            EconomyData.sellOffers().forEach { registry.add(SellDisplay(it.stack, it.price)) }
        }
    }

    private fun item(id: String) = Registries.ITEM.get(Identifier.of(id))
}
