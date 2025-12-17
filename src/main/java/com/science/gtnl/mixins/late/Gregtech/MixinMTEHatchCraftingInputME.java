package com.science.gtnl.mixins.late.Gregtech;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import appeng.api.networking.crafting.ICraftingMedium;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME;

@Mixin(value = MTEHatchCraftingInputME.class, remap = false)
public abstract class MixinMTEHatchCraftingInputME extends MTEHatchInputBus implements ICraftingMedium {

    @Shadow
    @Final
    private static int SLOT_CIRCUIT;

    @Shadow
    @Final
    private static int SLOT_MANUAL_START;

    public MixinMTEHatchCraftingInputME(int id, String name, String nameRegional, int tier) {
        super(id, name, nameRegional, tier);
    }

    @Inject(method = "getName", at = @At("HEAD"), cancellable = true)
    private void gtnl$overrideGetName(CallbackInfoReturnable<String> cir) {
        MTEHatchCraftingInputME self = (MTEHatchCraftingInputME) (Object) this;

        if (self.hasCustomName()) {
            cir.setReturnValue(self.getCustomName());
            return;
        }

        StringBuilder sb = new StringBuilder();

        if (mInventory[SLOT_CIRCUIT] != null) {
            sb.append("gt_circuit_")
                .append(mInventory[SLOT_CIRCUIT].getItemDamage())
                .append('_');
        }

        if (mInventory[SLOT_MANUAL_START] != null) {
            sb.append("extra_start_")
                .append(mInventory[SLOT_MANUAL_START].getUnlocalizedName())
                .append("_extra_end_");
        }

        if (getCrafterIcon() != null) {
            sb.append(getCrafterIcon().getUnlocalizedName());
        } else {
            sb.append("gt.blockmachines.")
                .append(mName)
                .append(".name");
        }

        cir.setReturnValue(sb.toString());
    }
}
