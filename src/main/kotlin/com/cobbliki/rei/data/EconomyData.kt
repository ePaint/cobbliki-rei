package com.cobbliki.rei.data

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import java.math.BigInteger
import kotlin.io.path.bufferedReader
import kotlin.io.path.exists

data class BuyOffer(val stack: ItemStack, val price: BigInteger, val category: String)

data class SellOffer(val stack: ItemStack, val price: BigInteger)

object EconomyData {
    val present: Boolean get() = FabricLoader.getInstance().isModLoaded("cobbledollars")

    private val merchantOffers by lazy { MerchantShopIndex.offers() }

    private fun config(name: String): JsonObject? {
        val f = FabricLoader.getInstance().configDir.resolve("cobbledollars").resolve(name)
        if (!f.exists()) return null
        return runCatching { f.bufferedReader().use { JsonParser.parseReader(it).asJsonObject } }.getOrNull()
    }

    private fun stackOf(id: String?): ItemStack? {
        val item = Registries.ITEM.get(Identifier.tryParse(id ?: return null) ?: return null)
        return if (item == Items.AIR) null else ItemStack(item)
    }

    fun buyOffers(): List<BuyOffer> {
        val global = LiveShop.buyOffers().ifEmpty(::fileBuyOffers)
        val seen = HashSet<Triple<String, BigInteger, String>>()
        val out = ArrayList<BuyOffer>()
        for (o in global + merchantOffers) {
            val id = Registries.ITEM.getId(o.stack.item).toString()
            if (seen.add(Triple(id, o.price, o.category))) out.add(o)
        }
        return out
    }

    fun sellOffers(): List<SellOffer> = LiveShop.sellOffers().ifEmpty(::fileSellOffers)

    private fun fileBuyOffers(): List<BuyOffer> {
        val shop = config("default_shop.json")?.getAsJsonArray("defaultShop") ?: return emptyList()
        val out = ArrayList<BuyOffer>()
        for (catEl in shop) {
            val cat = catEl as? JsonObject ?: continue
            for ((name, arr) in cat.entrySet()) {
                arr.asJsonArray.forEach { oEl ->
                    val o = oEl.asJsonObject
                    val stack = stackOf(o.get("item")?.asString) ?: return@forEach
                    out.add(BuyOffer(stack, o.get("price").asBigInteger, name))
                }
            }
        }
        return out
    }

    private fun fileSellOffers(): List<SellOffer> {
        val bank = config("bank.json")?.getAsJsonArray("bank") ?: return emptyList()
        return bank.mapNotNull { oEl ->
            val o = oEl.asJsonObject
            val stack = stackOf(o.get("item")?.asString) ?: return@mapNotNull null
            SellOffer(stack, o.get("price").asBigInteger)
        }
    }
}
