package com.cobbliki.rei

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object CobblikiRei : ModInitializer {
    const val MOD_ID = "cobbliki_rei"
    val log = LoggerFactory.getLogger(MOD_ID)

    override fun onInitialize() {
        log.info("Cobbliki REI common init")
    }
}
