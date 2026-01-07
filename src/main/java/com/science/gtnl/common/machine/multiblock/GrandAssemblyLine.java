package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlockAnyMeta;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static com.science.gtnl.utils.Utils.*;
import static com.science.gtnl.utils.enums.BlockIcons.OVERLAY_FRONT_TECTECH_MULTIBLOCK;
import static com.science.gtnl.utils.enums.BlockIcons.OVERLAY_FRONT_TECTECH_MULTIBLOCK_ACTIVE;
import static gregtech.api.GregTechAPI.sBlockCasings2;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.Mods.IndustrialCraft2;
import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTUtility.validMTEList;
import static gregtech.common.misc.WirelessNetworkManager.addEUToGlobalEnergyMap;
import static net.minecraft.util.StatCollector.translateToLocal;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.github.bsideup.jabel.Desugar;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.drawable.UITexture;
import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.api.math.Color;
import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.gtnewhorizons.modularui.common.widget.textfield.NumericWidget;
import com.science.gtnl.common.machine.multiMachineBase.GTMMultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.recipes.GTNLProcessingLogic;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Textures;
import gregtech.api.enums.VoidingMode;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchDataAccess;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.AssemblyLineUtils;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.IGTHatchAdder;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.ParallelHelper;
import gregtech.api.util.VoidProtectionHelper;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gregtech.common.tileentities.machines.IDualInputInventory;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.val;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import tectech.thing.casing.BlockGTCasingsTT;

public class GrandAssemblyLine extends GTMMultiMachineBase<GrandAssemblyLine> implements ISurvivalConstructable {

    public static int PARALLEL_WINDOW_ID = 10;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String GAL_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/grand_assembly_line";
    private static final String[][] shape = StructureUtils.readStructureFromFile(GAL_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 46;
    private static final int VERTICAL_OFF_SET = 2;
    private static final int DEPTH_OFF_SET = 0;
    public List<MTEHatchDataAccess> mDataAccessHatches = new ObjectArrayList<>();
    public String costingEUText = ZERO_STRING;
    public UUID ownerUUID;
    public boolean wirelessMode = false;
    public boolean isDualInputHatch = false;
    public boolean useSingleAmp = true;
    public int minRecipeTime = 20;

    public GrandAssemblyLine(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GrandAssemblyLine(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GrandAssemblyLine(this.mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("GrandAssemblyLineRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_07"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_08"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_09"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_10"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_11"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_12"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addTecTechHatchInfo()
            .beginStructureBlock(48, 5, 5, true)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_Casing"), 1)
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_Casing"), 1)
            .addInputBus(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_GrandAssemblyLine_Casing"), 1)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity baseMetaTileEntity, ForgeDirection sideDirection,
        ForgeDirection facingDirection, int colorIndex, boolean active, boolean redstoneLevel) {
        if (sideDirection == facingDirection) {
            if (active) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_TECTECH_MULTIBLOCK_ACTIVE)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_TECTECH_MULTIBLOCK)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.assemblylineVisualRecipes;
    }

    @Nonnull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(RecipeMaps.assemblylineVisualRecipes, GTNLRecipeMaps.GrandAssemblyLineRecipes);
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        ItemStack controllerItem = getControllerSlot();
        this.mParallelTier = getParallelTier(controllerItem);
        long energyEU = wirelessMode ? Integer.MAX_VALUE
            : GTValues.VP[mEnergyHatchTier] * (useSingleAmp ? 1 : getMaxInputAmps() / 2);
        int maxParallel = getTrueParallel();

        if (energyEU <= 0) return CheckRecipeResultRegistry.POWER_OVERFLOW;

        List<IDualInputInventory> inputInventories = new ObjectArrayList<>();

        if (isDualInputHatch) {
            for (IDualInputHatch dualInputHatch : mDualInputHatches) {
                Iterator<? extends IDualInputInventory> inventoryIterator = dualInputHatch.inventories();
                while (inventoryIterator.hasNext()) {
                    IDualInputInventory inventory = inventoryIterator.next();
                    if (inventory.isEmpty()) continue;
                    inputInventories.add(inventory);
                }
            }
        } else {
            // 将常规输入仓/总线包装成总成
            IDualInputInventory wrappedInventory = new WrappedInventory(getAllStoredInputs(), getStoredFluids());
            if (!wrappedInventory.isEmpty()) inputInventories.add(wrappedInventory);
        }

        return processRecipeLogic(inputInventories, energyEU, maxParallel, minRecipeTime);
    }

    public CheckRecipeResult processRecipeLogic(List<IDualInputInventory> inputInventories, long energyEU,
        int maxParallel, int minDuration) {
        List<GTRecipe.RecipeAssemblyLine> validRecipes = new ObjectArrayList<>();

        if (AssemblyLineUtils.isItemDataStick(mInventory[1])) {
            validRecipes.addAll(AssemblyLineUtils.findALRecipeFromDataStick(mInventory[1]));
        }

        for (MTEHatchDataAccess dataAccess : validMTEList(mDataAccessHatches)) {
            validRecipes.addAll(dataAccess.getAssemblyLineRecipes());
        }

        if (validRecipes.isEmpty()) {
            return CheckRecipeResultRegistry.NO_DATA_STICKS;
        }

        validRecipes.removeIf(
            recipe -> recipe.mInputs == null || Arrays.stream(recipe.mInputs)
                .anyMatch(Objects::isNull)
                || recipe.mFluidInputs == null
                || Arrays.stream(recipe.mFluidInputs)
                    .anyMatch(Objects::isNull)
                || recipe.mOutput == null
                || recipe.mEUt > energyEU);

        // 按耗电量排序，优先匹配低耗电配方
        validRecipes.sort(Comparator.comparingInt(recipe -> recipe.mEUt));

        ArrayList<ItemStack> totalOutputs = new ArrayList<>();
        BigInteger totalCostingEU = BigInteger.ZERO;
        long totalWiredEU = 0;
        int maxDurationFound = 0;
        int totalExecutedParallel = 0;
        int remainingGlobalParallel = maxParallel;

        int circuitOC = -1;
        for (ItemStack item : getAllStoredInputs()) {
            if (item.getItem() == ItemList.Circuit_Integrated.getItem()) {
                circuitOC = item.getItemDamage();
                break;
            }
        }
        int overclockTime = (mParallelTier >= 11) ? 4 : 2;

        for (IDualInputInventory inventory : inputInventories) {
            if (remainingGlobalParallel <= 0) break;

            ItemStack[] invItems = inventory.getItemInputs();
            if (invItems == null || invItems.length == 0) continue;
            FluidStack[] invFluids = inventory.getFluidInputs();

            for (GTRecipe.RecipeAssemblyLine recipe : validRecipes) {
                int localMaxParallel = remainingGlobalParallel;

                if (protectsExcessItem()) {
                    ArrayList<ItemStack> predictedOutputs = new ArrayList<>(totalOutputs);

                    ItemStack predicted = recipe.mOutput.copy();
                    ParallelHelper.addItemsLong(
                        predictedOutputs,
                        predicted,
                        (long) predicted.stackSize * remainingGlobalParallel);

                    VoidProtectionHelper voidProtectionHelper = new VoidProtectionHelper();
                    voidProtectionHelper.setMachine(this)
                        .setItemOutputs(predictedOutputs.toArray(new ItemStack[0]))
                        .setMaxParallel(remainingGlobalParallel)
                        .build();

                    int allowedParallel = Math.min(voidProtectionHelper.getMaxParallel(), remainingGlobalParallel);
                    if (allowedParallel <= 0 || voidProtectionHelper.isItemFull()) continue;
                    localMaxParallel = allowedParallel;
                }

                double parallelFactor = calculateParallelByItemsUnordered(invItems, localMaxParallel, recipe);
                if (parallelFactor < 1.0) continue;

                parallelFactor = calculateParallelByFluidsUnordered(invFluids, parallelFactor, recipe.mFluidInputs);
                if (parallelFactor < 1.0) continue;

                localMaxParallel = (int) parallelFactor;

                // --- 4.2 配方匹配成功，开始进行超频计算 (集成 minDuration) ---
                int adjustedTime = recipe.mDuration;
                int adjustedPower = recipe.mEUt;

                if (wirelessMode) {
                    val IntMax = BigInteger.valueOf(Integer.MAX_VALUE);
                    val big4 = BigInteger.valueOf(4);
                    // 无线模式下，时间直接设为传入的 minDuration (这里通常对应你之前用的 minRecipeTime)
                    adjustedTime = minDuration;
                    BigInteger adjustedPowerBigInt = BigInteger
                        .valueOf((long) recipe.mEUt * recipe.mDuration / adjustedTime);
                    while (adjustedPowerBigInt.compareTo(IntMax) > 0) {
                        adjustedPowerBigInt = adjustedPowerBigInt.divide(big4);
                        adjustedTime *= 4;
                    }
                    adjustedPower = adjustedPowerBigInt.min(IntMax)
                        .intValue();
                } else {
                    int overclockCount = 0;
                    long energyRatio = energyEU / Math.max(1, recipe.mEUt);
                    long threshold = 1;

                    while (energyRatio >= threshold * 4) {
                        overclockCount++;
                        threshold *= 4;
                    }
                    if (circuitOC >= 0) {
                        overclockCount = Math.min(overclockCount, circuitOC);
                    }

                    // 在超频计算时，增加对 minDuration 的检查
                    // 只有当超频后的时间仍然 >= minDuration 时，才允许继续增加功率缩短时间
                    while (overclockCount > 0 && adjustedPower * 4L <= Integer.MAX_VALUE
                        && adjustedTime / overclockTime >= minDuration) { // 限制在此
                        overclockCount--;
                        adjustedPower *= 4;
                        adjustedTime /= overclockTime;
                    }
                }

                // 确保不低于硬性下限
                adjustedTime = Math.max(minDuration, adjustedTime);
                adjustedPower = Math.max(1, adjustedPower);

                // 批处理模式
                if (adjustedTime < 128 && batchMode) {
                    double timeFactor = 128.0 / adjustedTime;
                    double energyFactor = (double) energyEU / adjustedPower;
                    double newPower = adjustedPower * timeFactor;

                    if (newPower > energyEU) {
                        adjustedPower = (int) Math.min(Integer.MAX_VALUE, adjustedPower * energyFactor);
                        adjustedTime = (int) Math.max(minDuration, adjustedTime * energyFactor);
                    } else {
                        adjustedPower = (int) Math.min(Integer.MAX_VALUE, newPower);
                        adjustedTime = 128;
                    }
                }

                // --- 4.3 能量上限检查 (有线模式) ---
                if (!wirelessMode) {
                    long currentInvLoad = (long) adjustedPower * localMaxParallel;
                    if (totalWiredEU + currentInvLoad > energyEU) {
                        long availablePower = energyEU - totalWiredEU;
                        localMaxParallel = (int) (availablePower / adjustedPower);
                    }
                }

                if (localMaxParallel <= 0) continue;

                consumeItemsUnordered(recipe, localMaxParallel, invItems);
                consumeFluidsUnordered(recipe, localMaxParallel, invFluids);

                // --- 4.5 记录结果 ---
                if (recipe.mOutput != null) {
                    ItemStack out = recipe.mOutput.copy();
                    ParallelHelper.addItemsLong(totalOutputs, out, (long) out.stackSize * localMaxParallel);
                }

                if (wirelessMode) {
                    BigInteger cost = BigInteger.valueOf(adjustedPower)
                        .multiply(BigInteger.valueOf(adjustedTime))
                        .multiply(BigInteger.valueOf(localMaxParallel));
                    totalCostingEU = totalCostingEU.add(cost);
                } else {
                    totalWiredEU += (long) adjustedPower * localMaxParallel;
                }

                maxDurationFound = Math.max(maxDurationFound, adjustedTime);
                totalExecutedParallel += localMaxParallel;
                remainingGlobalParallel -= localMaxParallel;

                break;
            }
        }

        // 5. 最终结算
        if (totalExecutedParallel == 0) return CheckRecipeResultRegistry.NO_RECIPE;
        if (totalOutputs.isEmpty()) return CheckRecipeResultRegistry.NO_RECIPE;

        mOutputItems = totalOutputs.toArray(new ItemStack[0]);
        updateSlots();

        if (wirelessMode) {
            if (!addEUToGlobalEnergyMap(ownerUUID, totalCostingEU.multiply(NEGATIVE_ONE))) {
                return CheckRecipeResultRegistry.insufficientPower(totalCostingEU.longValue());
            }
            costingEUText = GTUtility.formatNumbers(totalCostingEU);
            this.lEUt = 0;
        } else {
            this.lEUt = -totalWiredEU;
        }
        this.mMaxProgresstime = maxDurationFound;
        this.mEfficiency = 10000;
        this.mEfficiencyIncrease = 10000;

        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    public static Object2LongOpenHashMap<GTUtility.ItemId> getInventoryItemMap(ItemStack[] inputs) {
        Object2LongOpenHashMap<GTUtility.ItemId> itemMap = new Object2LongOpenHashMap<>();
        if (inputs == null) return itemMap;
        for (ItemStack is : inputs) {
            if (is == null || is.stackSize <= 0) continue;
            itemMap.merge(GTUtility.ItemId.createNoCopy(is), is.stackSize, Long::sum);
        }
        return itemMap;
    }

    public static Object2LongOpenHashMap<Fluid> getInventoryFluidMap(FluidStack[] inputs) {
        Object2LongOpenHashMap<Fluid> fluidMap = new Object2LongOpenHashMap<>();
        if (inputs == null) return fluidMap;
        for (FluidStack fs : inputs) {
            if (fs == null || fs.amount <= 0) continue;
            fluidMap.merge(fs.getFluid(), fs.amount, Long::sum);
        }
        return fluidMap;
    }

    public static double calculateParallelByItemsUnordered(ItemStack[] invItems, int maxParallel,
        GTRecipe.RecipeAssemblyLine recipe) {
        if (recipe.mInputs == null || recipe.mInputs.length == 0) return 0;

        Object2LongOpenHashMap<GTUtility.ItemId> availableMap = getInventoryItemMap(invItems);
        double currentParallel = maxParallel;

        for (int i = 0; i < recipe.mInputs.length; i++) {
            ItemStack mainRequirement = recipe.mInputs[i];
            ItemStack[] alts = recipe.mOreDictAlt[i];

            long maxParallelForThisSlot = 0;

            long mainAvailable = availableMap.getOrDefault(GTUtility.ItemId.createNoCopy(mainRequirement), 0L);
            maxParallelForThisSlot = Math.max(maxParallelForThisSlot, mainAvailable / mainRequirement.stackSize);

            if (maxParallelForThisSlot == 0 && alts != null) {
                for (ItemStack alt : alts) {
                    if (alt == null) continue;
                    if (maxParallelForThisSlot != 0) break;
                    long altAvailable = availableMap.getOrDefault(GTUtility.ItemId.createNoCopy(alt), 0L);
                    maxParallelForThisSlot = Math.max(maxParallelForThisSlot, altAvailable / alt.stackSize);
                }
            }

            if (maxParallelForThisSlot <= 0) return 0;
            currentParallel = Math.min(currentParallel, (double) maxParallelForThisSlot);
        }
        return currentParallel;
    }

    public static double calculateParallelByFluidsUnordered(FluidStack[] invFluids, double currentParallel,
        FluidStack[] recipeFluids) {
        if (recipeFluids == null || recipeFluids.length == 0) return currentParallel;
        Object2LongOpenHashMap<Fluid> availableMap = getInventoryFluidMap(invFluids);

        for (FluidStack req : recipeFluids) {
            if (req == null) continue;
            long available = availableMap.getOrDefault(req.getFluid(), 0L);
            if (available < req.amount) return 0;
            currentParallel = Math.min(currentParallel, (double) available / req.amount);
        }
        return currentParallel;
    }

    public void consumeItemsUnordered(GTRecipe.RecipeAssemblyLine recipe, int parallel, ItemStack[] invItems) {
        if (recipe.mInputs == null) return;

        Object2LongOpenHashMap<GTUtility.ItemId> availableMap = getInventoryItemMap(invItems);

        for (int i = 0; i < recipe.mInputs.length; i++) {
            ItemStack mainReq = recipe.mInputs[i];
            ItemStack[] alts = recipe.mOreDictAlt[i];

            ItemStack chosenStack = mainReq;
            long maxPossible = (availableMap.getOrDefault(GTUtility.ItemId.createNoCopy(mainReq), 0L))
                / mainReq.stackSize;

            if (maxPossible == 0 && alts != null) {
                for (ItemStack alt : alts) {
                    if (maxPossible != 0) break;
                    if (alt == null) continue;
                    maxPossible = (availableMap.getOrDefault(GTUtility.ItemId.createNoCopy(alt), 0L)) / alt.stackSize;
                    chosenStack = alt;
                }
            }

            long totalToConsume = (long) chosenStack.stackSize * parallel;
            depleteFromRequirement(chosenStack, totalToConsume, invItems);
        }
    }

    public void depleteFromRequirement(ItemStack requirement, long amountToConsume, ItemStack[] invItems) {
        for (ItemStack slot : invItems) {
            if (slot == null || amountToConsume <= 0) continue;

            if (GTUtility.areStacksEqual(requirement, slot)) {
                int canTake = (int) Math.min(slot.stackSize, amountToConsume);
                slot.stackSize -= canTake;
                amountToConsume -= canTake;
            }
            if (amountToConsume <= 0) break;
        }
    }

    public void consumeFluidsUnordered(GTRecipe.RecipeAssemblyLine recipe, int parallel, FluidStack[] invFluids) {
        if (recipe.mFluidInputs == null || invFluids == null) return;

        for (FluidStack recipeFluid : recipe.mFluidInputs) {
            if (recipeFluid == null) continue;
            long totalToConsume = (long) recipeFluid.amount * parallel;

            for (FluidStack slotFluid : invFluids) {
                if (slotFluid == null || totalToConsume <= 0) continue;

                if (GTUtility.areFluidsEqual(recipeFluid, slotFluid)) {
                    int canTake = (int) Math.min(slotFluid.amount, totalToConsume);
                    slotFluid.amount -= canTake;
                    totalToConsume -= canTake;
                }
                if (totalToConsume <= 0) break;
            }
        }
    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        for (MTEHatchDataAccess hatch_dataAccess : mDataAccessHatches) {
            hatch_dataAccess.setActive(true);
        }
        return super.onRunningTick(aStack);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("wirelessMode", wirelessMode);
        aNBT.setInteger("parallelTier", mParallelTier);
        aNBT.setInteger("minRecipeTime", minRecipeTime);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        wirelessMode = aNBT.getBoolean("wirelessMode");
        mParallelTier = aNBT.getInteger("parallelTier");
        minRecipeTime = aNBT.getInteger("minRecipeTime");
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        this.ownerUUID = aBaseMetaTileEntity.getOwnerUuid();
    }

    @Override
    public IStructureDefinition<GrandAssemblyLine> getStructureDefinition() {
        return StructureDefinition.<GrandAssemblyLine>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', ofBlock(sBlockCasings2, 5))
            .addElement(
                'B',
                buildHatchAdder(GrandAssemblyLine.class).casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(InputBus)
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasingsTT, 3))))
            .addElement('C', ofBlock(sBlockCasingsTT, 2))
            .addElement(
                'D',
                buildHatchAdder(GrandAssemblyLine.class).casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(OutputBus)
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasingsTT, 3))))
            .addElement(
                'E',
                buildHatchAdder(GrandAssemblyLine.class).casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(
                        InputHatch,
                        InputBus,
                        OutputBus,
                        Energy.or(ExoticEnergy),
                        ParallelCon,
                        DataHatchElement.DataAccess)
                    .buildAndChain(
                        onElementPass(
                            x -> ++x.mCountCasing,
                            ofBlockAnyMeta(GameRegistry.findBlock(IndustrialCraft2.ID, "blockAlloyGlass")))))
            .addElement(
                'F',
                buildHatchAdder(GrandAssemblyLine.class).casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(
                        InputHatch,
                        InputBus,
                        OutputBus,
                        Maintenance,
                        Energy.or(ExoticEnergy),
                        ParallelCon,
                        DataHatchElement.DataAccess)
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasingsTT, 3))))
            .addElement('G', ofBlock(sBlockCasings2, 9))
            .build();
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!this.checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET)
            || !checkHatch()) return false;
        return mCountCasing >= 590;
    }

    @Override
    public boolean checkHatch() {
        setupParameters();
        if (mParallelTier < 9 && !checkEnergyHatch()) return false;

        if (!mDualInputHatches.isEmpty()) {
            isDualInputHatch = true;
            if (!mInputBusses.isEmpty() || !mInputHatches.isEmpty()) return false;
        }

        if (mParallelTier >= 12 && mEnergyHatches.isEmpty() && mExoticEnergyHatches.isEmpty()) {
            wirelessMode = true;
            useSingleAmp = false;
            mEnergyHatchTier = 14;
            return mDataAccessHatches.size() <= 1 && mMaintenanceHatches.size() <= 1;
        }

        return !(mEnergyHatches.isEmpty() && mExoticEnergyHatches.isEmpty()) && mDataAccessHatches.size() <= 1
            && mMaintenanceHatches.size() <= 1;
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        mDataAccessHatches.clear();
        isDualInputHatch = false;
        useSingleAmp = true;
        wirelessMode = false;
    }

    @Override
    public void setupParameters() {
        super.setupParameters();
        useSingleAmp = mEnergyHatches.size() == 1 && mExoticEnergyHatches.isEmpty() && getMaxInputAmps() <= 4;
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

    @Override
    public int getCasingTextureID() {
        return BlockGTCasingsTT.textureOffset + 3;
    }

    @Override
    public Set<VoidingMode> getAllowedVoidingModes() {
        return VoidingMode.ITEM_ONLY_MODES;
    }

    @Override
    public boolean supportsSingleRecipeLocking() {
        return false;
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNLProcessingLogic() {

            @NotNull
            @Override
            public GTNLOverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return GTNLOverclockCalculator.ofNoOverclock(recipe)
                    .setExtraDurationModifier(mConfigSpeedBoost)
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier())
                    .setAmperage(wirelessMode ? Long.MAX_VALUE : useSingleAmp ? 1 : getMaxInputAmps() / 2)
                    .setEUt(wirelessMode ? Long.MAX_VALUE : getMachineVoltageLimit());
            }
        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    public double getEUtDiscount() {
        return 0.8 - (mParallelTier / 50.0) * ((mParallelTier >= 12) ? 0.2 : 1);
    }

    @Override
    public double getDurationModifier() {
        return (1 / 1.67 - (Math.max(0, mParallelTier - 1) / 50.0)) * ((mParallelTier >= 12) ? 1.0 / 20.0 : 1);
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        buildContext.addSyncedWindow(PARALLEL_WINDOW_ID, this::createParallelWindow);
        builder.widget(new ButtonWidget().setOnClick((clickData, widget) -> {
            if (!widget.isClient()) {
                widget.getContext()
                    .openSyncedWindow(PARALLEL_WINDOW_ID);
            }
        })
            .setPlayClickSound(true)
            .setBackground(() -> {
                List<UITexture> ret = new ObjectArrayList<>();
                ret.add(GTUITextures.BUTTON_STANDARD);
                ret.add(GTUITextures.OVERLAY_BUTTON_BATCH_MODE_ON);
                return ret.toArray(new IDrawable[0]);
            })
            .addTooltip(translateToLocal("Info_GrandAssemblyLine_00"))
            .setTooltipShowUpDelay(TOOLTIP_DELAY)
            .setPos(174, 112)
            .setSize(16, 16));
        super.addUIWidgets(builder, buildContext);
    }

    public ModularWindow createParallelWindow(final EntityPlayer player) {
        final int WIDTH = 158;
        final int HEIGHT = 52;
        final int PARENT_WIDTH = getGUIWidth();
        final int PARENT_HEIGHT = getGUIHeight();
        ModularWindow.Builder builder = ModularWindow.builder(WIDTH, HEIGHT);
        builder.setBackground(GTUITextures.BACKGROUND_SINGLEBLOCK_DEFAULT);
        builder.setGuiTint(getGUIColorization());
        builder.setDraggable(true);
        builder.setPos(
            (size, window) -> Alignment.Center.getAlignedPos(size, new Size(PARENT_WIDTH, PARENT_HEIGHT))
                .add(
                    Alignment.BottomRight.getAlignedPos(new Size(PARENT_WIDTH, PARENT_HEIGHT), new Size(WIDTH, HEIGHT))
                        .add(WIDTH - 3, 0)
                        .subtract(0, 10)));
        builder.widget(
            TextWidget.localised("Info_GrandAssemblyLine_00")
                .setPos(3, 4)
                .setSize(150, 20))
            .widget(
                new NumericWidget().setSetter(val -> minRecipeTime = (int) val)
                    .setGetter(() -> minRecipeTime)
                    .setBounds(1, Integer.MAX_VALUE)
                    .setDefaultValue(1)
                    .setScrollValues(1, 4, 64)
                    .setTextAlignment(Alignment.Center)
                    .setTextColor(Color.WHITE.normal)
                    .setSize(150, 18)
                    .setPos(4, 25)
                    .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD)
                    .attachSyncer(
                        new FakeSyncWidget.IntegerSyncer(() -> minRecipeTime, (val) -> minRecipeTime = val),
                        builder));
        return builder.build();
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        final IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null) {
            tag.setBoolean("wirelessMode", wirelessMode);
            if (wirelessMode) tag.setString("costingEUText", costingEUText);
        }
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        if (tag.getBoolean("wirelessMode")) {
            currentTip.add(EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("Waila_WirelessMode"));
            currentTip.add(
                EnumChatFormatting.AQUA + StatCollector.translateToLocal("Waila_CurrentEuCost")
                    + EnumChatFormatting.RESET
                    + ": "
                    + EnumChatFormatting.GOLD
                    + tag.getString("costingEUText")
                    + EnumChatFormatting.RESET
                    + " EU");
        }
    }

    @Override
    public String[] getInfoData() {
        List<String> ret = new ObjectArrayList<>(Arrays.asList(super.getInfoData()));
        if (wirelessMode) {
            ret.add(EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("Waila_WirelessMode"));
            ret.add(
                EnumChatFormatting.AQUA + StatCollector.translateToLocal("Waila_CurrentEuCost")
                    + EnumChatFormatting.RESET
                    + ": "
                    + EnumChatFormatting.GOLD
                    + costingEUText
                    + EnumChatFormatting.RESET
                    + " EU");
        }
        return ret.toArray(new String[0]);
    }

    public boolean addDataAccessToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) return false;
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) return false;
        if (aMetaTileEntity instanceof MTEHatchDataAccess) {
            ((MTEHatch) aMetaTileEntity).updateTexture(aBaseCasingIndex);
            return mDataAccessHatches.add((MTEHatchDataAccess) aMetaTileEntity);
        }
        return false;
    }

    public enum DataHatchElement implements IHatchElement<GrandAssemblyLine> {

        DataAccess;

        @Override
        public List<? extends Class<? extends IMetaTileEntity>> mteClasses() {
            return Collections.singletonList(MTEHatchDataAccess.class);
        }

        @Override
        public IGTHatchAdder<GrandAssemblyLine> adder() {
            return GrandAssemblyLine::addDataAccessToMachineList;
        }

        @Override
        public long count(GrandAssemblyLine t) {
            return t.mDataAccessHatches.size();
        }
    }

    @Desugar
    public record WrappedInventory(List<ItemStack> itemInputs, List<FluidStack> fluidInputs)
        implements IDualInputInventory {

        @Override
        public boolean isEmpty() {
            return itemInputs == null || fluidInputs == null;
        }

        @Override
        public ItemStack[] getItemInputs() {
            return itemInputs.toArray(new ItemStack[0]);
        }

        @Override
        public FluidStack[] getFluidInputs() {
            return fluidInputs.toArray(new FluidStack[0]);
        }
    }

}
