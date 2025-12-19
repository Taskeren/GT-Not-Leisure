package com.science.gtnl.mixins.late.Gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.api.mixinHelper.IMultiblockRecipeMap;
import com.science.gtnl.config.MainConfig;

import goodgenerator.blocks.tileEntity.MTEPreciseAssembler;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.recipe.RecipeMap;

@Mixin(value = MTEPreciseAssembler.class, remap = false)
public abstract class MixinMTEPreciseAssembler extends MTEExtendedPowerMultiBlockBase<MixinMTEPreciseAssembler> {

    public MixinMTEPreciseAssembler(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Shadow
    public abstract RecipeMap<?> getRecipeMap();

    @Inject(method = "addToPAssList", at = @At(value = "HEAD"))
    private void gtnl$InputCheck(IGregTechTileEntity aTileEntity, int aBaseCasingIndex,
        CallbackInfoReturnable<Boolean> cir) {
        if (!MainConfig.enableHatchInterfaceTerminalEnhance) return;
        if (aTileEntity == null) return;
        if (getRecipeMap() == null) return;
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) return;
        if (aMetaTileEntity instanceof MTEHatchInputBus hatch) {
            ((IMultiblockRecipeMap) aMetaTileEntity).setRecipeMapName(getRecipeMap().unlocalizedName);
            hatch.updateCraftingIcon(getMachineCraftingIcon());
        }
        if (aMetaTileEntity instanceof MTEHatchInput hatch) {
            ((IMultiblockRecipeMap) aMetaTileEntity).setRecipeMapName(getRecipeMap().unlocalizedName);
            hatch.updateCraftingIcon(getMachineCraftingIcon());
        }
    }
}
