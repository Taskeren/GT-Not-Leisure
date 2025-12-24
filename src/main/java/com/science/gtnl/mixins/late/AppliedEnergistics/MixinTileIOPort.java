package com.science.gtnl.mixins.late.AppliedEnergistics;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.llamalad7.mixinextras.sugar.Local;

import appeng.tile.storage.TileIOPort;

@Mixin(value = TileIOPort.class, remap = false)
public abstract class MixinTileIOPort {

    @ModifyConstant(method = "tickingRequest", constant = @Constant(longValue = 536_870_912))
    public long modifyTickingRequest(long constant, @Local(name = "ItemsToMove") long ItemsToMove) {
        return (Long.MAX_VALUE - 1) / ItemsToMove;
    }
}
