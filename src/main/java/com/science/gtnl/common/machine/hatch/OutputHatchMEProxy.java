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
    public int masterX, masterY, masterZ, masterDim;
    public boolean masterSet = false; // indicate if values of masterX, masterY, masterZ are valid

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
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTimer) {
        if (aTimer % 100 == 0 && masterSet && getMaster() == null) {
            trySetMasterFromCoord(masterX, masterY, masterZ, masterDim);
        }
        IOutputME outputME = (IOutputME) this;
        if (getBaseMetaTileEntity().isServerSide()) {
            outputME.setTickCounter(aTimer);
            if (outputME.getTickCounter() > (outputME.getLastOutputTick() + 40)) flushCachedStack();
            if (outputME.getTickCounter() % 20 == 0) getBaseMetaTileEntity().setActive(isActive());
        }

        outputME.gtnl$checkFluidLock();

        super.onPostTick(aBaseMetaTileEntity, aTimer);
    }

    public void flushCachedStack() {
        IOutputME output = (IOutputME) this;
        if (getMaster() == null) {
            output.gtnl$flushCachedStack();
        } else if (getMaster().canAcceptFluid()) {
            IOutputME master = (IOutputME) getMaster();
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
        if (!masterSet) return;
        NBTTagCompound masterNBT = new NBTTagCompound();
        masterNBT.setInteger("masterDim", masterDim);
        masterNBT.setInteger("masterX", masterX);
        masterNBT.setInteger("masterY", masterY);
        masterNBT.setInteger("masterZ", masterZ);
        aNBT.setTag("master", masterNBT);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        if (!aNBT.hasKey("master")) return;
        NBTTagCompound masterNBT = aNBT.getCompoundTag("master");
        masterX = masterNBT.getInteger("masterX");
        masterY = masterNBT.getInteger("masterY");
        masterZ = masterNBT.getInteger("masterZ");
        masterDim = masterNBT.getInteger("masterDim");
        masterSet = true;
    }

    public MTEHatchOutputME getMaster() {
        if (master == null) return null;
        if (master.getBaseMetaTileEntity() == null) { // master disappeared
            master = null;
        }
        return master;
    }

    public MTEHatchOutputME trySetMasterFromCoord(int x, int y, int z, int dim) {
        World world = DimensionManager.getWorld(dim);
        if (world == null) return null;

        TileEntity tileEntity = GTUtil.getTileEntity(world, x, y, z, false);

        if (tileEntity == null) return null;
        if (!(tileEntity instanceof IGregTechTileEntity GTTE)) return null;
        if (!(GTTE.getMetaTileEntity() instanceof MTEHatchOutputME newMaster)) return null;
        if (newMaster instanceof OutputHatchMEProxy) return null;
        if (master != newMaster) master = newMaster;
        masterX = x;
        masterY = y;
        masterZ = z;
        masterSet = true;
        return master;
    }

    @Override
    public boolean pasteCopiedData(EntityPlayer player, NBTTagCompound aNBT) {
        boolean result = super.pasteCopiedData(player, aNBT);
        if (aNBT == null) return result;

        if (!aNBT.hasKey("master")) return result;
        NBTTagCompound masterNBT = aNBT.getCompoundTag("master");
        int x = masterNBT.getInteger("masterX");
        int y = masterNBT.getInteger("masterY");
        int z = masterNBT.getInteger("masterZ");
        int dim = masterNBT.getInteger("masterDim");
        return trySetMasterFromCoord(x, y, z, dim) != null;
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        NBTTagCompound tag = accessor.getNBTData();
        if (tag.hasKey("master")) {
            NBTTagCompound masterNBT = tag.getCompoundTag("master");
            currenttip.add(
                EnumChatFormatting.AQUA + "Linked to Output at "
                    + EnumChatFormatting.WHITE
                    + "[Dim "
                    + masterNBT.getInteger("masterDim")
                    + "] "
                    + masterNBT.getInteger("masterX")
                    + ", "
                    + masterNBT.getInteger("masterY")
                    + ", "
                    + masterNBT.getInteger("masterZ")
                    + EnumChatFormatting.RESET);
        } else {
            currenttip.add(EnumChatFormatting.AQUA + "Unlinked");
        }

        super.getWailaBody(itemStack, currenttip, accessor, config);
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {

        if (masterSet) {
            NBTTagCompound masterNBT = new NBTTagCompound();
            masterNBT.setInteger("masterDim", masterDim);
            masterNBT.setInteger("masterX", masterX);
            masterNBT.setInteger("masterY", masterY);
            masterNBT.setInteger("masterZ", masterZ);
            tag.setTag("master", masterNBT);
        }

        super.getWailaNBTData(player, tile, tag, world, x, y, z);
    }
}
