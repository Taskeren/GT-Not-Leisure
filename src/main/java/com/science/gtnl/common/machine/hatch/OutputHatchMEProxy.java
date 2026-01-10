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
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IItemList;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.util.GTUtil;
import gregtech.common.tileentities.machines.MTEHatchOutputME;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class OutputHatchMEProxy extends MTEHatchOutputME {

    public static final String COPIED_DATA_IDENTIFIER = "outputHatchME";

    public MTEHatchOutputME master;

    public OutputHatchMEProxy(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OutputHatchMEProxy(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OutputHatchMEProxy(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public String[] getDescription() {
        return new String[] { StatCollector.translateToLocal("Tooltip_OutputHatchMEProxy_00"),
            StatCollector.translateToLocal("Tooltip_OutputHatchMEProxy_01"),
            StatCollector.translateToLocal("Tooltip_OutputHatchMEProxy_02"),
            StatCollector.translateToLocal("Tooltip_OutputHatchMEProxy_03"),
            StatCollector.translateToLocal("Tooltip_OutputHatchMEProxy_04"),
            StatCollector.translateToLocal("Tooltip_OutputHatchMEProxy_05"),
            StatCollector.translateToLocal("Tooltip_OutputHatchMEProxy_06") };
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        IOutputME outputME = (IOutputME) this;
        if (getBaseMetaTileEntity().isServerSide()) {
            outputME.setTickCounter(aTick);
            if (outputME.getTickCounter() > (outputME.getLastOutputTick() + 40)) flushCachedStack();
            if (outputME.getTickCounter() % 20 == 0) getBaseMetaTileEntity().setActive(isActive());
        }

        outputME.gtnl$checkFluidLock();

        super.onPostTick(aBaseMetaTileEntity, aTick);
    }

    public void flushCachedStack() {
        IOutputME output = (IOutputME) this;
        if (this.master == null) {
            output.gtnl$flushCachedStack();
        } else if (this.master.canAcceptFluid()) {
            IOutputME master = (IOutputME) this.master;
            IItemList<IAEFluidStack> masterCache = master.getFluidCache();
            IItemList<IAEFluidStack> fluidCache = output.getFluidCache();

            for (IAEFluidStack stack : fluidCache) {
                if (stack != null && stack.getStackSize() > 0) {
                    masterCache.addStorage(stack);
                }
            }

            fluidCache.resetStatus();

            output.setLastOutputTick(output.getTickCounter());
        }
    }

    @Override
    public AENetworkProxy getProxy() {
        IOutputME outputME = (IOutputME) this;
        AENetworkProxy gridProxy = outputME.getGridProxy();
        if (gridProxy == null) {
            if (getBaseMetaTileEntity() instanceof IGridProxyable) {
                gridProxy = new AENetworkProxy(
                    (IGridProxyable) getBaseMetaTileEntity(),
                    "proxy",
                    GTNLItemList.OutputHatchMEProxy.get(1),
                    true);
                outputME.setGridProxy(gridProxy);
                gridProxy.setFlags(GridFlags.REQUIRE_CHANNEL);
                outputME.gtnl$updateValidGridProxySides();
                if (getBaseMetaTileEntity().getWorld() != null) gridProxy.setOwner(
                    getBaseMetaTileEntity().getWorld()
                        .getPlayerEntityByName(getBaseMetaTileEntity().getOwnerName()));
            }
        }
        return gridProxy;
    }

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

        if (te instanceof IGregTechTileEntity gtTE && gtTE.getMetaTileEntity() instanceof MTEHatchOutputME outputME) {
            this.master = outputME;
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

        if (te instanceof IGregTechTileEntity gtTE && gtTE.getMetaTileEntity() instanceof MTEHatchOutputME outputME) {
            this.master = outputME;
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
