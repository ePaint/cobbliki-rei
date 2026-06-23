package com.cobbliki.rei.data

import net.minecraft.item.ItemStack
import java.math.BigInteger

// The shop the player actually sees is the server-synced one held in CobbleDollars'
// ClientShopConfig, not the local default_shop.json (which is only the singleplayer default).
// Read it reflectively so the mod stays decoupled from CobbleDollars at compile/runtime.
object LiveShop {
    private const val CONFIG = "fr.harmex.cobbledollars.common.client.config.ClientShopConfig"

    private fun config(): Any? = runCatching { Class.forName(CONFIG).getField("INSTANCE").get(null) }.getOrNull()

    private fun call(target: Any, method: String): Any? =
        runCatching { target.javaClass.getMethod(method).invoke(target) }.getOrNull()

    private fun offers(container: Any?): List<Pair<ItemStack, BigInteger>> {
        val list = container as? List<*> ?: return emptyList()
        return list.mapNotNull { o ->
            o ?: return@mapNotNull null
            val item = call(o, "getItem") as? ItemStack ?: return@mapNotNull null
            val price = call(o, "getPrice") as? BigInteger ?: return@mapNotNull null
            if (item.isEmpty) null else item to price
        }
    }

    fun buyOffers(): List<BuyOffer> {
        val cfg = config() ?: return emptyList()
        val shop = call(cfg, "getDefaultShop") as? List<*> ?: return emptyList()
        val out = ArrayList<BuyOffer>()
        for (cat in shop) {
            cat ?: continue
            val name = call(cat, "getName") as? String ?: ""
            offers(call(cat, "getOffers")).forEach { (item, price) -> out.add(BuyOffer(item, price, name)) }
        }
        return out
    }

    fun sellOffers(): List<SellOffer> {
        val cfg = config() ?: return emptyList()
        return offers(call(cfg, "getBank")).map { (item, price) -> SellOffer(item, price) }
    }
}
