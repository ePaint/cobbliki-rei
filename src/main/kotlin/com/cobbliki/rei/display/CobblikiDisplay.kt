package com.cobbliki.rei.display

import me.shedaniel.rei.api.common.display.Display
import net.minecraft.util.Identifier

abstract class CobblikiDisplay : Display {
    override fun provideInternalDisplay(): Display = this
    override fun provideInternalDisplayIds(): Collection<Identifier> = emptyList()
}
