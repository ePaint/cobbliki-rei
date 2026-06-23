package com.cobbliki.rei.data

import com.google.gson.JsonParser
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.util.Identifier
import kotlin.io.path.bufferedReader
import kotlin.io.path.exists

object PastureConfig {
    val present: Boolean get() = FabricLoader.getInstance().isModLoaded("pasture-loot")

    private fun root() = run {
        val f = FabricLoader.getInstance().configDir.resolve("PastureLoot.json")
        if (f.exists()) f.bufferedReader().use { JsonParser.parseReader(it).asJsonObject } else null
    }

    fun blacklist(): Set<Identifier> {
        val arr = root()?.getAsJsonArray("item_blacklist") ?: return emptySet()
        return arr.mapNotNull { Identifier.tryParse(it.asString) }.toSet()
    }

    fun dropChancePerMinute(): Double =
        root()?.get("drop_chance_per_minute")?.asDouble ?: 0.0
}
