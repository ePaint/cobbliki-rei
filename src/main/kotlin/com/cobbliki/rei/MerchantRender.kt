package com.cobbliki.rei

import me.shedaniel.rei.api.client.gui.widgets.Widget
import me.shedaniel.rei.api.client.gui.widgets.Widgets
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import net.minecraft.village.VillagerData
import net.minecraft.village.VillagerType

object MerchantRender {
    private var cached: LivingEntity? = null

    private fun entity(): LivingEntity? {
        cached?.let { return it }
        val world = MinecraftClient.getInstance().world ?: return null
        return runCatching {
            val v = VillagerEntity(EntityType.VILLAGER, world)
            Registries.VILLAGER_PROFESSION.get(Identifier.of("cobbledollars", "cobble_merchant"))?.let {
                v.villagerData = VillagerData(VillagerType.PLAINS, it, 1)
            }
            cached = v
            v
        }.getOrNull()
    }

    fun widget(x: Int, y: Int, box: Int): Widget = Widgets.createDrawableWidget { ctx, _, _, _ ->
        val e = entity() ?: return@createDrawableWidget
        runCatching {
            InventoryScreen.drawEntity(
                ctx, x, y, x + box, y + box, box, 0f,
                (x + box * 0.5f), (y + box * 0.5f), e
            )
        }
    }
}
