package com.cobbliki.rei.data

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtIo
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtSizeTracker
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import java.io.ByteArrayInputStream
import java.math.BigInteger
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipInputStream

object MerchantShopIndex {
    private val storeEntry = Regex("""data/bca/structure/stores/.*\.nbt""")

    fun offers(): List<BuyOffer> = runCatching { build() }.getOrDefault(emptyList())

    private fun build(): List<BuyOffer> {
        val dir = FabricLoader.getInstance().gameDir.resolve("datapacks")
        if (!Files.isDirectory(dir)) return emptyList()
        val seen = HashSet<Triple<String, BigInteger, String>>()
        val out = ArrayList<BuyOffer>()
        Files.newDirectoryStream(dir, "*.zip").use { zips ->
            for (zip in zips) runCatching { scanZip(zip, seen, out) }
        }
        return out
    }

    private fun scanZip(zip: Path, seen: MutableSet<Triple<String, BigInteger, String>>, out: MutableList<BuyOffer>) {
        ZipInputStream(Files.newInputStream(zip)).use { zin ->
            var e = zin.nextEntry
            while (e != null) {
                if (!e.isDirectory && storeEntry.matches(e.name)) {
                    val bytes = zin.readBytes()
                    runCatching { scanStructure(bytes, seen, out) }
                }
                e = zin.nextEntry
            }
        }
    }

    private fun scanStructure(bytes: ByteArray, seen: MutableSet<Triple<String, BigInteger, String>>, out: MutableList<BuyOffer>) {
        val root = NbtIo.readCompressed(ByteArrayInputStream(bytes), NbtSizeTracker.ofUnlimitedBytes())
        val entities = root.getList("entities", 10)
        for (i in 0 until entities.size) {
            val nbt = entities.getCompound(i).getCompound("nbt")
            if (nbt.getString("id") != "cobbledollars:cobble_merchant") continue
            readShop(nbt.getList("CobbleMerchantShop", 10), seen, out)
        }
    }

    private fun readShop(categories: NbtList, seen: MutableSet<Triple<String, BigInteger, String>>, out: MutableList<BuyOffer>) {
        for (c in 0 until categories.size) {
            val cat = categories.getCompound(c)
            val name = cat.getString("Category")
            val offers = cat.getList("Offers", 10)
            for (o in 0 until offers.size) {
                val offer = offers.getCompound(o)
                val item = offer.getCompound("Item")
                val id = item.getString("id")
                val stack = stackOf(id) ?: continue
                val price = offer.getString("Price").toBigIntegerOrNull() ?: continue
                if (seen.add(Triple(id, price, name))) out.add(BuyOffer(stack, price, name))
            }
        }
    }

    private fun stackOf(id: String): ItemStack? {
        val item = Registries.ITEM.get(Identifier.tryParse(id) ?: return null)
        return if (item == Items.AIR) null else ItemStack(item)
    }
}
