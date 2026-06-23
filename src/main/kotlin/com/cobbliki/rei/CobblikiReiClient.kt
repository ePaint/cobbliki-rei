package com.cobbliki.rei

import com.cobbliki.rei.data.EconomyData
import com.cobbliki.rei.data.LiveShop
import com.cobbliki.rei.data.PastureConfig
import com.cobbliki.rei.data.PokemonData
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.minecraft.text.Text

object CobblikiReiClient : ClientModInitializer {
    override fun onInitializeClient() {
        CobblikiRei.log.info("Cobbliki REI client init")
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(
                ClientCommandManager.literal("cobblikirei").executes { ctx ->
                    val all = PokemonData.all()
                    val withDrops = all.count { it.drops.isNotEmpty() }
                    val withTm = all.count { it.tmMoves.isNotEmpty() }
                    val bl = PastureConfig.blacklist().size
                    ctx.source.sendFeedback(
                        Text.literal(
                            "Cobbliki REI: ${all.size} species, $withDrops con drops, " +
                                "$withTm con MTs; pastureLoot=${PastureConfig.present} blacklist=$bl"
                        )
                    )
                    val live = LiveShop.buyOffers()
                    val eff = EconomyData.buyOffers()
                    val cats = live.map { it.category }.distinct()
                    ctx.source.sendFeedback(
                        Text.literal(
                            "Economy: present=${EconomyData.present} liveBuy=${live.size} effectiveBuy=${eff.size} " +
                                "liveSell=${LiveShop.sellOffers().size} cats=$cats"
                        )
                    )
                    1
                }
            )
        }
    }
}
