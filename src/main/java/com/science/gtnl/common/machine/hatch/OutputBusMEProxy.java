package com.science.gtnl.common.machine.hatch;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import com.science.gtnl.api.mixinHelper.IOutputME;
import com.science.gtnl.utils.enums.GTNLItemList;

import appeng.api.networking.GridFlags;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.util.GTUtil;
import gregtech.common.tileentities.machines.MTEHatchOutputBusME;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class OutputBusMEProxy extends MTEHatchOutputBusME {

    public MTEHatchOutputBusME master;

    public OutputBusMEProxy(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OutputBusMEProxy(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OutputBusMEProxy(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public String[] getDescription() {
        return new String[] { StatCollector.translateToLocal("Tooltip_OutputBusMEProxy_00"),
            StatCollector.translateToLocal("Tooltip_OutputBusMEProxy_01"),
            StatCollector.translateToLocal("Tooltip_OutputBusMEProxy_02"),
            StatCollector.translateToLocal("Tooltip_OutputBusMEProxy_03"),
            StatCollector.translateToLocal("Tooltip_OutputBusMEProxy_04"),
            StatCollector.translateToLocal("Tooltip_OutputBusMEProxy_05"),
            StatCollector.translateToLocal("Tooltip_OutputBusMEProxy_06") };
    }

    @Override
    public void flushCachedStack() {
        if (this.master == null) {
            super.flushCachedStack();
        } else if (this.master.canAcceptItem()) {
            IOutputME master = (IOutputME) this.master;
            IOutputME output = (IOutputME) this;
            IItemList<IAEItemStack> masterCache = master.getItemCache();
            IItemList<IAEItemStack> itemCache = output.getItemCache();

            for (IAEItemStack stack : itemCache) {
                if (stack != null && stack.getStackSize() > 0) {
                    masterCache.addStorage(stack);
                }
            }

            itemCache.resetStatus();

            output.setLastOutputTick(output.getTickCounter());
        }
    }

    @Override
    public AENetworkProxy getProxy() {
        if (gridProxy == null) {
            if (getBaseMetaTileEntity() instanceof IGridProxyable) {
                gridProxy = new AENetworkProxy(
                    (IGridProxyable) getBaseMetaTileEntity(),
                    "proxy",
                    GTNLItemList.OutputBusMEProxy.get(1),
                    true);
                gridProxy.setFlags(GridFlags.REQUIRE_CHANNEL);
                updateValidGridProxySides();
                if (getBaseMetaTileEntity().getWorld() != null) gridProxy.setOwner(
                    getBaseMetaTileEntity().getWorld()
                        .getPlayerEntityByName(getBaseMetaTileEntity().getOwnerName()));
            }
        }
        return this.gridProxy;
    }

    @Override
    public void onLeftclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {}

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        ((IOutputME) this).setLastClickedPlayer(aPlayer);

        openGui(aPlayer);

        return true;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);

        if (master == null) return;

        IGregTechTileEntity tileEntity = master.getBaseMetaTileEntity();
        if (tileEntity == null) return;

        aNBT.setInteger("masterDim", tileEntity.getWorld().provider.dimensionId);
        aNBT.setInteger("masterX", tileEntity.getXCoord());
        aNBT.setInteger("masterY", tileEntity.getYCoord());
        aNBT.setInteger("masterZ", tileEntity.getZCoord());
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);

        if (!aNBT.hasKey("masterDim") || !aNBT.hasKey("masterX")
            || !aNBT.hasKey("masterY")
            || !aNBT.hasKey("masterZ")) {
            return;
        }

        int dim = aNBT.getInteger("masterDim");
        int x = aNBT.getInteger("masterX");
        int y = aNBT.getInteger("masterY");
        int z = aNBT.getInteger("masterZ");

        World world = DimensionManager.getWorld(dim);
        if (world == null) return;

        TileEntity te = GTUtil.getTileEntity(world, x, y, z, true);

        if (te instanceof IGregTechTileEntity gtTE
            && gtTE.getMetaTileEntity() instanceof MTEHatchOutputBusME outputBusME) {
            this.master = outputBusME;
        }
    }

    @Override
    public boolean pasteCopiedData(EntityPlayer player, NBTTagCompound aNBT) {
        boolean result = super.pasteCopiedData(player, aNBT);
        if (aNBT == null) return result;

        if (!aNBT.hasKey("masterDim") || !aNBT.hasKey("masterX")
            || !aNBT.hasKey("masterY")
            || !aNBT.hasKey("masterZ")) {
            return result;
        }

        int dim = aNBT.getInteger("masterDim");
        int x = aNBT.getInteger("masterX");
        int y = aNBT.getInteger("masterY");
        int z = aNBT.getInteger("masterZ");

        World world = DimensionManager.getWorld(dim);
        if (world == null) return result;

        TileEntity te = GTUtil.getTileEntity(world, x, y, z, true);

        if (te instanceof IGregTechTileEntity gtTE
            && gtTE.getMetaTileEntity() instanceof MTEHatchOutputBusME outputBusME) {
            this.master = outputBusME;
            return true;
        }

        return result;
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        NBTTagCompound tag = accessor.getNBTData();

        if (tag.getBoolean("linked")) {
            int dimID = tag.getInteger("masterDim");

            currenttip.add(
                EnumChatFormatting.AQUA + "Linked to Output at "
                    + EnumChatFormatting.WHITE
                    + "[Dim "
                    + dimID
                    + "] "
                    + tag.getInteger("masterX")
                    + ", "
                    + tag.getInteger("masterY")
                    + ", "
                    + tag.getInteger("masterZ")
                    + EnumChatFormatting.RESET);
        } else {
            currenttip.add(EnumChatFormatting.AQUA + "Unlinked");
        }

        super.getWailaBody(itemStack, currenttip, accessor, config);
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        tag.setBoolean("linked", master != null);

        if (master != null) {
            IGregTechTileEntity te = master.getBaseMetaTileEntity();
            if (te != null) {
                tag.setInteger("masterDim", te.getWorld().provider.dimensionId);
                tag.setInteger("masterX", te.getXCoord());
                tag.setInteger("masterY", te.getYCoord());
                tag.setInteger("masterZ", te.getZCoord());
            }
        }

        super.getWailaNBTData(player, tile, tag, world, x, y, z);
    }
}
