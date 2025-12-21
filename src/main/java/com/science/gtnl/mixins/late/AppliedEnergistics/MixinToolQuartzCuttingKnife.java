package com.science.gtnl.mixins.late.AppliedEnergistics;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import appeng.helpers.ICustomNameObject;
import appeng.items.tools.quartz.ToolQuartzCuttingKnife;

@Deprecated
@Mixin(value = ToolQuartzCuttingKnife.class, remap = false)
public abstract class MixinToolQuartzCuttingKnife {

    @WrapOperation(method = "onItemUse", at = @At(value = "CONSTANT", args = "classValue=appeng/tile/AEBaseTile"))
    private boolean wrapInstanceOfAEBaseTile(Object obj, Operation<Boolean> original) {
        return original.call(obj) || obj instanceof ICustomNameObject;
    }
}
