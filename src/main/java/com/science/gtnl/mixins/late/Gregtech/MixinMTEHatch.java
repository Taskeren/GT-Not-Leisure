package com.science.gtnl.mixins.late.Gregtech;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.science.gtnl.api.mixinHelper.IMultiblockRecipeMap;
import com.science.gtnl.config.MainConfig;

import appeng.helpers.ICustomNameObject;
import gregtech.api.interfaces.IConfigurationCircuitSupport;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicTank;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;

@Mixin(value = MTEHatch.class, remap = false)
public abstract class MixinMTEHatch extends MTEBasicTank implements IMultiblockRecipeMap {

    @Shadow
    private int texturePage;

    @Shadow
    private int textureIndex;

    @Unique
    private String gtnl$multiBlockRecipeMapName = null;

    public MixinMTEHatch(int aID, String aName, String aNameRegional, int aTier, int aInvSlotCount, String aDescription,
        ITexture... aTextures) {
        super(aID, aName, aNameRegional, aTier, aInvSlotCount, aDescription, aTextures);
    }

    @Override
    public String gtnl$getRecipeMapName() {
        return gtnl$multiBlockRecipeMapName;
    }

    @Override
    public void gtnl$setRecipeMapName(String recipeMap) {
        gtnl$multiBlockRecipeMapName = recipeMap;
    }

    @ModifyVariable(method = "updateCraftingIcon", at = @At("HEAD"), argsOnly = true, index = 1)
    private ItemStack gtnl$modifyCraftingIcon(ItemStack value) {
        if (!MainConfig.enableHatchInterfaceTerminalEnhance) return value;
        if (value.hasDisplayName()) return value;
        MTEHatch hatch = (MTEHatch) (Object) this;
        StringBuilder sb = null;
        if (hatch instanceof IConfigurationCircuitSupport circuitHatch && !(hatch instanceof ICustomNameObject)) {
            ItemStack circuit = getStackInSlot(circuitHatch.getCircuitSlot());
            if (circuit != null) {
                sb = new StringBuilder();
                sb.append("gt_circuit_")
                    .append(circuit.getItemDamage())
                    .append("_");
            }
        }
        if (hatch instanceof MTEHatchInput inputHatch
            && (gtnl$multiBlockRecipeMapName != null || inputHatch.mRecipeMap != null)) {
            if (sb == null) {
                sb = new StringBuilder();
            }
            sb.append("extra_start_")
                .append(
                    gtnl$multiBlockRecipeMapName != null ? gtnl$multiBlockRecipeMapName
                        : inputHatch.mRecipeMap.unlocalizedName)
                .append("_extra_end_");
        }
        if (hatch instanceof MTEHatchInputBus inputBus
            && (gtnl$multiBlockRecipeMapName != null || inputBus.mRecipeMap != null)) {
            if (sb == null) {
                sb = new StringBuilder();
            }
            sb.append("extra_start_")
                .append(
                    gtnl$multiBlockRecipeMapName != null ? gtnl$multiBlockRecipeMapName
                        : inputBus.mRecipeMap.unlocalizedName)
                .append("_extra_end_");
        }
        if (sb != null && sb.length() > 0) {
            ItemStack modified = value.copy();
            modified.setStackDisplayName(sb.toString());
            return modified;
        }
        return value;
    }

    /**
     * <p>
     * This method updates the texture based on the given texture ID. It calculates the texture page and index
     * and issues the appropriate update based on whether the operation is on the server or client side.
     * </p>
     *
     * <p>
     * <b>Warning:</b> This method will be removed in version 2.8.2. Please consider using the new method for texture
     * handling.
     * </p>
     *
     * @param id The texture ID to update the texture page and index.
     *
     * @reason This method will be deprecated in version 2.8.2 due to changes in texture handling. A more efficient
     *         and scalable system will replace this method.
     * @author GTNotLeisure
     */
    @Deprecated
    @Overwrite
    public final void updateTexture(int id) {
        int newTexturePage = id >> 7;
        int newTextureIndex = id & 127;
        if (newTexturePage == texturePage && newTextureIndex == textureIndex) return;
        texturePage = newTexturePage;
        textureIndex = newTextureIndex;

        IGregTechTileEntity base = getBaseMetaTileEntity();

        if (base.isServerSide()) {
            base.issueTileUpdate();
        } else {
            base.issueTextureUpdate();
        }
    }

}
