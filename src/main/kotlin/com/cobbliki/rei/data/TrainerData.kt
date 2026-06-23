package com.cobbliki.rei.data

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipInputStream

data class TrainerDrop(val id: String, val name: Text, val drops: List<ItemStack>)

object TrainerData {
    val present: Boolean get() = FabricLoader.getInstance().isModLoaded("rctmod")

    private val lootEntry = Regex("""data/rctmod/loot_table/trainers/(.+)\.json""")
    private val nameEntry = Regex("""data/rctmod/trainers/(.+)\.json""")

    private val cache by lazy { build() }
    fun entries(): List<TrainerDrop> = cache

    private class Raw {
        val items = LinkedHashSet<Identifier>()
        var name: String? = null
    }

    private fun build(): List<TrainerDrop> {
        if (!present) return emptyList()
        val raw = HashMap<String, Raw>()
        for (mod in FabricLoader.getInstance().allMods) for (root in mod.rootPaths) scanRoot(root, raw)
        scanDatapacks(raw)
        return raw.entries.mapNotNull { (id, r) ->
            if (r.items.isEmpty()) return@mapNotNull null
            val stacks = r.items.mapNotNull { stackOf(it) }
            if (stacks.isEmpty()) return@mapNotNull null
            TrainerDrop(id, Text.literal(r.name ?: id), stacks)
        }.sortedBy { it.id }
    }

    private fun stackOf(id: Identifier): ItemStack? {
        val item = Registries.ITEM.get(id)
        return if (item == Items.AIR) null else ItemStack(item)
    }

    private fun scanRoot(root: Path, raw: MutableMap<String, Raw>) {
        val base = root.resolve("data").resolve("rctmod")
        if (!Files.isDirectory(base)) return
        runCatching {
            val loot = base.resolve("loot_table").resolve("trainers")
            if (Files.isDirectory(loot)) Files.walk(loot).use { w ->
                w.filter { Files.isRegularFile(it) && it.toString().endsWith(".json") }.forEach { f ->
                    val rel = loot.relativize(f).toString().replace('\\', '/').removeSuffix(".json")
                    indexLoot(rel, Files.readString(f), raw)
                }
            }
            val names = base.resolve("trainers")
            if (Files.isDirectory(names)) Files.walk(names).use { w ->
                w.filter { Files.isRegularFile(it) && it.toString().endsWith(".json") }.forEach { f ->
                    val id = names.relativize(f).toString().replace('\\', '/').removeSuffix(".json").substringAfterLast('/')
                    indexName(id, Files.readString(f), raw)
                }
            }
        }
    }

    private fun scanDatapacks(raw: MutableMap<String, Raw>) {
        val dir = FabricLoader.getInstance().gameDir.resolve("datapacks")
        if (!Files.isDirectory(dir)) return
        runCatching {
            Files.newDirectoryStream(dir, "*.zip").use { zips ->
                for (zip in zips) runCatching {
                    ZipInputStream(Files.newInputStream(zip)).use { zin ->
                        var e = zin.nextEntry
                        while (e != null) {
                            if (!e.isDirectory) {
                                val lm = lootEntry.matchEntire(e.name)
                                val nm = if (lm == null) nameEntry.matchEntire(e.name) else null
                                if (lm != null) indexLoot(lm.groupValues[1], zin.readBytes().decodeToString(), raw)
                                else if (nm != null) indexName(nm.groupValues[1].substringAfterLast('/'), zin.readBytes().decodeToString(), raw)
                            }
                            e = zin.nextEntry
                        }
                    }
                }
            }
        }
    }

    private fun indexLoot(rel: String, json: String, raw: MutableMap<String, Raw>) {
        val id = rel.substringAfterLast('/')
        val r = raw.getOrPut(id) { Raw() }
        runCatching {
            val obj = JsonParser.parseString(json).asJsonObject
            obj.getAsJsonArray("pools")?.forEach { pe ->
                (pe as? JsonObject)?.getAsJsonArray("entries")?.forEach { ee ->
                    val o = ee as? JsonObject ?: return@forEach
                    if (o.get("type")?.asString != "minecraft:item") return@forEach
                    Identifier.tryParse(o.get("name")?.asString ?: return@forEach)?.let(r.items::add)
                }
            }
        }
    }

    private fun indexName(id: String, json: String, raw: MutableMap<String, Raw>) {
        runCatching {
            val n = JsonParser.parseString(json).asJsonObject.getAsJsonObject("name") ?: return
            val text = n.get("literal")?.asString ?: n.get("translate")?.asString ?: return
            raw.getOrPut(id) { Raw() }.name = text
        }
    }
}
