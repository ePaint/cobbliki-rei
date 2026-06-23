package com.cobbliki.rei

import me.shedaniel.rei.api.client.gui.widgets.Widget
import me.shedaniel.rei.api.client.gui.widgets.Widgets
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.data.TrackedData

// rctmod renders trainers as player-model entities and resolves the skin from the trainer id,
// so a TrainerMob built reflectively (no compile dependency) renders correctly via drawEntity.
object TrainerRender {
    private const val TRAINER_MOB = "com.gitlab.srcmc.rctmod.world.entities.TrainerMob"
    private val cache = HashMap<String, LivingEntity>()

    private fun entity(trainerId: String): LivingEntity? {
        cache[trainerId]?.let { return it }
        val world = MinecraftClient.getInstance().world ?: return null
        val mob = runCatching {
            val cls = Class.forName(TRAINER_MOB)
            val type = cls.getMethod("getEntityType").invoke(null) as EntityType<*>
            val e = type.create(world) as? LivingEntity ?: return@runCatching null
            // setTrainerId skips the synced DataTracker write on the client (it's normally set
            // server-side), so the skin lookup sees an empty id. Set the tracked value directly.
            @Suppress("UNCHECKED_CAST")
            val data = cls.getDeclaredField("DATA_TRAINER_ID").apply { isAccessible = true }.get(null) as TrackedData<String>
            e.dataTracker.set(data, trainerId)
            runCatching { cls.getMethod("setTrainerId", String::class.java).invoke(e, trainerId) }
            e
        }.getOrNull() ?: return null
        cache[trainerId] = mob
        return mob
    }

    fun widget(trainerId: String, x: Int, y: Int, w: Int, h: Int, size: Int): Widget = Widgets.createDrawableWidget { ctx, _, _, _ ->
        val e = entity(trainerId) ?: return@createDrawableWidget
        runCatching {
            InventoryScreen.drawEntity(ctx, x, y, x + w, y + h, size, 0f, x + w * 0.5f, y + h * 0.5f, e)
        }
    }
}
