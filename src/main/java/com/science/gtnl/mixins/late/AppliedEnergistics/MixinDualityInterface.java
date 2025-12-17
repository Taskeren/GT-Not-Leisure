package com.science.gtnl.mixins.late.AppliedEnergistics;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import appeng.helpers.DualityInterface;

@Mixin(value = DualityInterface.class, remap = false)
public class MixinDualityInterface {

    @Inject(
        method = "getTermName",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getUnlocalizedName()Ljava/lang/String;"),
        cancellable = true)
    private void gtnl$injectBeforeItemReturn(CallbackInfoReturnable<String> cir,
        @Local(name = "item") ItemStack itemStack) {
        if (itemStack.hasDisplayName()) cir.setReturnValue(itemStack.getDisplayName());
    }
}
