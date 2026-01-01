package com.science.gtnl.mixins.late.Gregtech;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.api.mixinHelper.IOutputME;
import com.science.gtnl.common.machine.hatch.OutputBusMEProxy;

import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import gregtech.api.enums.ItemList;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchOutputBus;
import gregtech.common.tileentities.machines.MTEHatchOutputBusME;
import lombok.Getter;
import lombok.Setter;

@Mixin(value = MTEHatchOutputBusME.class, remap = false)
public abstract class MixinMTEHatchOutputBusME extends MTEHatchOutputBus implements IOutputME {

    @Getter
    @Setter
    @Shadow
    @Final
    IItemList<IAEItemStack> itemCache;

    @Getter
    @Setter
    @Shadow
    long lastOutputTick;

    @Getter
    @Setter
    @Shadow
    long lastInputTick;

    @Getter
    @Setter
    @Shadow
    long tickCounter;

    @Getter
    @Setter
    @Shadow
    boolean additionalConnection;

    @Getter
    @Setter
    @Shadow
    EntityPlayer lastClickedPlayer;

    @Getter
    @Setter
    @Shadow
    List<ItemStack> lockedItems;

    public MixinMTEHatchOutputBusME(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier);
    }

    @Inject(method = "getCopiedData", at = @At("RETURN"), cancellable = true)
    private void gtnl$injectCopiedData(EntityPlayer player, CallbackInfoReturnable<NBTTagCompound> cir) {
        if ((Object) this instanceof OutputBusMEProxy) return;

        IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity == null) return;

        NBTTagCompound tag = cir.getReturnValue();
        if (tag == null) {
            tag = new NBTTagCompound();
        }

        tag.setInteger("masterX", tileEntity.getXCoord());
        tag.setInteger("masterY", tileEntity.getYCoord());
        tag.setInteger("masterZ", tileEntity.getZCoord());

        tag.setInteger("masterDim", tileEntity.getWorld().provider.dimensionId);

        cir.setReturnValue(tag);
    }

    @Override
    public void onLeftclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (!(aPlayer instanceof EntityPlayerMP)) return;

        ItemStack dataStick = aPlayer.inventory.getCurrentItem();
        if (!ItemList.Tool_DataStick.isStackEqual(dataStick, false, true)) return;

        dataStick.stackTagCompound = getCopiedData(aPlayer);
        dataStick.setStackDisplayName("Output Bus ME Link Configuration");
        aPlayer.addChatMessage(new ChatComponentTranslation("GT5U.machines.output_bus.saved"));
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer, ForgeDirection side,
        float aX, float aY, float aZ) {
        if (!(aPlayer instanceof EntityPlayerMP)) {
            this.lastClickedPlayer = aPlayer;
            openGui(aPlayer);
            return onRightclick(aBaseMetaTileEntity, aPlayer);
        }

        ItemStack dataStick = aPlayer.inventory.getCurrentItem();

        if (!ItemList.Tool_DataStick.isStackEqual(dataStick, false, true)) {
            this.lastClickedPlayer = aPlayer;
            openGui(aPlayer);
            return onRightclick(aBaseMetaTileEntity, aPlayer);
        }

        if (!pasteCopiedData(aPlayer, dataStick.stackTagCompound)) return false;

        aPlayer.addChatMessage(new ChatComponentTranslation("GT5U.machines.output_bus.loaded"));
        return true;
    }

}
