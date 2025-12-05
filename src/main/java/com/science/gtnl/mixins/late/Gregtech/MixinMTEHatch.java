package com.science.gtnl.mixins.late.Gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicTank;
import gregtech.api.metatileentity.implementations.MTEHatch;

@Deprecated
@Mixin(value = MTEHatch.class, remap = false)
public abstract class MixinMTEHatch extends MTEBasicTank {

    @Shadow
    private int texturePage;

    @Shadow
    private int textureIndex;

    public MixinMTEHatch(int aID, String aName, String aNameRegional, int aTier, int aInvSlotCount, String aDescription,
        ITexture... aTextures) {
        super(aID, aName, aNameRegional, aTier, aInvSlotCount, aDescription, aTextures);
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
