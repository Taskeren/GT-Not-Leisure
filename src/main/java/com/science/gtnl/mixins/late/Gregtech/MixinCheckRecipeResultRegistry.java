package com.science.gtnl.mixins.late.Gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;

@Mixin(value = CheckRecipeResultRegistry.class, remap = false)
public abstract class MixinCheckRecipeResultRegistry {

    @Inject(method = "getSampleFromRegistry", at = @At("HEAD"), cancellable = true)
    private static void injectGetSampleFromRegistry(String id, CallbackInfoReturnable<CheckRecipeResult> cir) {
        if (id == null) return;
        if (id.isEmpty()) cir.setReturnValue(CheckRecipeResultRegistry.NONE);
    }
}
