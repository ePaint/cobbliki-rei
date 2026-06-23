package com.cobbliki.rei

import com.cobbliki.rei.data.DropInfo
import com.cobbliki.rei.display.BuyDisplay
import com.cobbliki.rei.display.DexDisplay
import com.cobbliki.rei.display.DropsDisplay
import com.cobbliki.rei.display.EvoItemDisplay
import com.cobbliki.rei.display.FossilDisplay
import com.cobbliki.rei.display.LearnersDisplay
import com.cobbliki.rei.display.MegaDisplay
import com.cobbliki.rei.display.MoveDetailDisplay
import com.cobbliki.rei.display.MtDisplay
import com.cobbliki.rei.display.PastureDisplay
import com.cobbliki.rei.display.SellDisplay
import com.cobbliki.rei.display.TrainerDropDisplay
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import java.math.BigInteger

object Categories {
    val DROPS: CategoryIdentifier<DropsDisplay> = CategoryIdentifier.of(CobblikiRei.MOD_ID, "drops")
    val PASTURE: CategoryIdentifier<PastureDisplay> = CategoryIdentifier.of(CobblikiRei.MOD_ID, "pasture")
    val MT: CategoryIdentifier<MtDisplay> = CategoryIdentifier.of(CobblikiRei.MOD_ID, "mt")
    val LEARNERS: CategoryIdentifier<LearnersDisplay> = CategoryIdentifier.of(CobblikiRei.MOD_ID, "learners")
    val MOVE_DETAIL: CategoryIdentifier<MoveDetailDisplay> = CategoryIdentifier.of(CobblikiRei.MOD_ID, "move_detail")
    val EVO: CategoryIdentifier<EvoItemDisplay> = CategoryIdentifier.of(CobblikiRei.MOD_ID, "evo")
    val EVO_ITEM: CategoryIdentifier<EvoItemDisplay> = CategoryIdentifier.of(CobblikiRei.MOD_ID, "evo_item")
    val MEGA: CategoryIdentifier<MegaDisplay> = CategoryIdentifier.of(CobblikiRei.MOD_ID, "mega")
    val FOSSIL: CategoryIdentifier<FossilDisplay> = CategoryIdentifier.of(CobblikiRei.MOD_ID, "fossil")
    val BUY: CategoryIdentifier<BuyDisplay> = CategoryIdentifier.of(CobblikiRei.MOD_ID, "buy")
    val SELL: CategoryIdentifier<SellDisplay> = CategoryIdentifier.of(CobblikiRei.MOD_ID, "sell")
    val TRAINER: CategoryIdentifier<TrainerDropDisplay> = CategoryIdentifier.of(CobblikiRei.MOD_ID, "trainer")
    val DEX: CategoryIdentifier<DexDisplay> = CategoryIdentifier.of(CobblikiRei.MOD_ID, "dex")
}

object Money {
    fun format(price: BigInteger): String = "%,d".format(price)
}

fun stackOf(drop: DropInfo): ItemStack = ItemStack(Registries.ITEM.get(drop.item), drop.max.coerceAtLeast(1))
