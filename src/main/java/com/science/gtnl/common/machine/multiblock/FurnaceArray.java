package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.*;
import static gregtech.api.util.GTUtility.*;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.SubscribeEventUtils;
import com.science.gtnl.utils.Utils;

import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.tileentities.machines.MTEHatchOutputBusME;

public class FurnaceArray extends MultiMachineBase<FurnaceArray> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final int HORIZONTAL_OFF_SET = 1;
    private static final int VERTICAL_OFF_SET = 1;
    private static final int DEPTH_OFF_SET = 0;
    public static int CASING_INDEX = GTUtility.getTextureId((byte) 116, (byte) 52);

    public long furnaceCount = 1;
    public long coalCount = 0;

    public int tick = 0;

    public long time;

    public static ItemStack furnace = new ItemStack(Blocks.furnace), coal = new ItemStack(Items.coal);

    public FurnaceArray(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public FurnaceArray(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new FurnaceArray(this.mName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_STEAM_FURNACE_ACTIVE)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_STEAM_FURNACE)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return CASING_INDEX;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.furnaceRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("FurnaceArrayRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_FurnaceArray_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_FurnaceArray_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_FurnaceArray_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_FurnaceArray_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_FurnaceArray_04"))
            .addMultiAmpHatchInfo()
            .beginStructureBlock(3, 3, 3, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_FurnaceArray_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_FurnaceArray_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_FurnaceArray_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<FurnaceArray> getStructureDefinition() {
        return StructureDefinition.<FurnaceArray>builder()
            .addShape(
                STRUCTURE_PIECE_MAIN,
                transpose(
                    new String[][] { { "AAA", "AAA", "AAA" }, { "A~A", "A A", "AAA" }, { "AAA", "AAA", "AAA" }, }))
            .addElement(
                'A',
                buildHatchAdder(FurnaceArray.class).casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(InputBus, OutputBus, Maintenance)
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(BlockLoader.metaCasing02, 20))))
            .build();
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return true;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setLong("furnaceCount", furnaceCount);
        aNBT.setLong("coalCount", coalCount);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        furnaceCount = aNBT.getLong("furnaceCount");
        coalCount = aNBT.getLong("coalCount");
    }

    @Override
    public void onBlockDestroyed() {
        super.onBlockDestroyed();
        IGregTechTileEntity te = getBaseMetaTileEntity();
        if (te.isClientSide()) return;

        World world = te.getWorld();
        int x = te.getXCoord();
        int y = te.getYCoord();
        int z = te.getZCoord();

        long remainingFurnace = furnaceCount;
        while (remainingFurnace > 0) {
            int dropAmount = (int) Math.min(64, remainingFurnace);
            ItemStack drop = furnace.copy();
            drop.stackSize = dropAmount;
            EntityItem ent = new EntityItem(world, x, y, z, drop);
            world.spawnEntityInWorld(ent);
            remainingFurnace -= dropAmount;
        }

        long remainingCoal = coalCount / 2;
        while (remainingCoal > 0) {
            int dropAmount = (int) Math.min(64, remainingCoal);
            ItemStack drop = coal.copy();
            drop.stackSize = dropAmount;
            EntityItem ent = new EntityItem(world, x, y, z, drop);
            world.spawnEntityInWorld(ent);
            remainingCoal -= dropAmount;
        }
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        time = 0;
        List<ItemStack> tInput = getAllStoredInputs();
        if (tInput.isEmpty()) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }
        long maxParallel = Math.min(furnaceCount, coalCount / 32);
        if (maxParallel <= 0) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        MinecraftServer server = MinecraftServer.getServer();
        double msptDiff = 25.0 - (int) (MathHelper.average(server.tickTimeArray) * 1.0E-6D);

        double adjustmentFactor;
        if (msptDiff > 0) {
            adjustmentFactor = Math.min(1.0, msptDiff);
        } else {
            adjustmentFactor = Math.max(-100.0, msptDiff * 5.0);
        }

        time = (long) (maxParallel / 5d + adjustmentFactor);
        if (time < 0) time = 0;
        if (time >= 200) {
            IGregTechTileEntity gtTE = getBaseMetaTileEntity();
            int x = gtTE.getXCoord();
            int y = gtTE.getYCoord();
            int z = gtTE.getZCoord();
            String name = gtTE.getOwnerName();
            for (EntityPlayerMP player : server.getConfigurationManager().playerEntityList) {
                player.addChatMessage(new ChatComponentTranslation("Info_FurnaceArray_00", x, y, z, name, time));
            }
        }

        List<ItemStack> outputSlots = new ArrayList<>();
        for (ItemStack stack : getItemOutputSlots(null)) {
            outputSlots.add(stack != null ? stack.copy() : null);
        }

        boolean hasMEOutputBus = false;
        for (final MTEHatch bus : validMTEList(mOutputBusses)) {
            if (bus instanceof MTEHatchOutputBusME meBus) {
                if (!meBus.isLocked() && meBus.canAcceptItem()) {
                    hasMEOutputBus = true;
                    break;
                }
            }
        }

        ArrayList<ItemStack> smeltedOutputs = new ArrayList<>();
        long toSmelt = maxParallel;

        for (ItemStack item : tInput) {
            if (GTUtility.areStacksEqual(item, furnace)) {
                furnaceCount += item.stackSize;
                depleteInput(item);
                continue;
            }
            if (GTUtility.areStacksEqual(item, coal)) {
                coalCount += item.stackSize;
                depleteInput(item);
                continue;
            }

            ItemStack smeltedOutput = GTModHandler.getSmeltingOutput(item, false, null);
            if (smeltedOutput == null) continue;

            long remainingToSmelt = Math.min(toSmelt, item.stackSize);
            long maxOutput = 0;

            if (hasMEOutputBus) {
                maxOutput = remainingToSmelt;
            } else {
                long needed = remainingToSmelt;
                ItemStack outputType = smeltedOutput.copy();
                outputType.stackSize = 1;

                for (int i = 0; i < outputSlots.size(); i++) {
                    ItemStack slot = outputSlots.get(i);
                    if (slot == null) {
                        long canFit = Math.min(needed, outputType.getMaxStackSize());
                        ItemStack newStack = outputType.copy();
                        newStack.stackSize = (int) canFit;
                        outputSlots.set(i, newStack);
                        maxOutput += canFit;
                        needed -= canFit;
                    } else if (slot.isItemEqual(outputType)) {
                        int space = (slot.stackSize == 65 ? (int) needed : slot.getMaxStackSize() - slot.stackSize);
                        long canFit = Math.min(needed, space);
                        slot.stackSize += (int) canFit;
                        maxOutput += canFit;
                        needed -= canFit;
                    }
                    if (needed <= 0) break;
                }
            }

            long toProcess = protectsExcessItem() ? maxOutput : remainingToSmelt;
            if (toProcess <= 0) continue;

            Utils.addStacksToList(smeltedOutputs, smeltedOutput, toProcess);

            item.stackSize -= (int) toProcess;
            toSmelt -= toProcess;
            if (toSmelt <= 0) break;
        }

        if (smeltedOutputs.isEmpty()) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        this.mOutputItems = smeltedOutputs.toArray(new ItemStack[0]);
        this.mEfficiency = 10000 - (getIdealStatus() - getRepairStatus()) * 1000;
        this.mEfficiencyIncrease = 10000;
        this.mMaxProgresstime = 100;
        this.lEUt = 0;
        this.updateSlots();

        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        if (++tick % 20 == 0) {
            SubscribeEventUtils.sleepTime += time;
        }
        return super.onRunningTick(aStack);
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET,
            elementBudget,
            env,
            false,
            true);
    }
}
