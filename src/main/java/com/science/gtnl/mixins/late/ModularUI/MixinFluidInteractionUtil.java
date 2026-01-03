package com.science.gtnl.mixins.late.ModularUI;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.glodblock.github.common.item.ItemFluidDrop;
import com.gtnewhorizons.modularui.api.widget.FluidInteractionUtil;

import gregtech.api.enums.Mods;

@Mixin(value = FluidInteractionUtil.class, remap = false)
public class MixinFluidInteractionUtil {

    @Inject(method = "getFluidForPhantomItem", at = @At("HEAD"), cancellable = true)
    public void injectGetFluidForPhantomItem(ItemStack itemStack, CallbackInfoReturnable<FluidStack> cir) {
        if (!Mods.AE2FluidCraft.isModLoaded()) return;
        if (itemStack == null) return;
        if (!(itemStack.getItem() instanceof ItemFluidDrop)) return;
        cir.setReturnValue(ItemFluidDrop.getFluidStack(itemStack));
    }
}
