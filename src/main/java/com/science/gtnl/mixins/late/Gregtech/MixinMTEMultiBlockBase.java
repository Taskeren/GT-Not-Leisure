package com.science.gtnl.mixins.late.Gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.api.mixinHelper.IMultiblockRecipeMap;
import com.science.gtnl.config.MainConfig;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.api.recipe.RecipeMap;

@Mixin(value = MTEMultiBlockBase.class, remap = false)
public abstract class MixinMTEMultiBlockBase {

    @Shadow
    public abstract RecipeMap<?> getRecipeMap();

    @Inject(method = "addInputBusToMachineList", at = @At(value = "HEAD"))
    private void gtnl$InputBusCheck(IGregTechTileEntity aTileEntity, int aBaseCasingIndex,
        CallbackInfoReturnable<Boolean> cir) {
        if (!MainConfig.enableHatchInterfaceTerminalEnhance) return;
        if (aTileEntity == null) return;
        if (getRecipeMap() == null) return;
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) return;
        if (aMetaTileEntity instanceof MTEHatchInputBus)
            ((IMultiblockRecipeMap) aMetaTileEntity).gtnl$setRecipeMapName(getRecipeMap().unlocalizedName);
    }

    @Inject(method = "addInputHatchToMachineList", at = @At(value = "HEAD"))
    private void gtnl$InputHatchCheck(IGregTechTileEntity aTileEntity, int aBaseCasingIndex,
        CallbackInfoReturnable<Boolean> cir) {
        if (!MainConfig.enableHatchInterfaceTerminalEnhance) return;
        if (aTileEntity == null) return;
        if (getRecipeMap() == null) return;
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) return;
        if (aMetaTileEntity instanceof MTEHatchInput)
            ((IMultiblockRecipeMap) aMetaTileEntity).gtnl$setRecipeMapName(getRecipeMap().unlocalizedName);
    }

}
