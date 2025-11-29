package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.sBlockCasings8;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.NAQUADAH_REACTOR_SOLID_FRONT;
import static gregtech.api.enums.Textures.BlockIcons.NAQUADAH_REACTOR_SOLID_FRONT_ACTIVE;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;
import static gtPlusPlus.core.block.ModBlocks.blockCasings4Misc;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;
import static tectech.thing.metaTileEntity.multi.base.TTMultiblockBase.HatchElement.*;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.science.gtnl.api.IConfigurationMaintenance;
import com.science.gtnl.common.material.RecipePool;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.item.ItemUtils;
import com.science.gtnl.utils.recipes.GTNL_OverclockCalculator;
import com.science.gtnl.utils.recipes.GTNL_ProcessingLogic;
import com.science.gtnl.utils.recipes.metadata.NaquadahReactorMetadata;

import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatchMaintenance;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;

public abstract class NaquadahReactor extends TTMultiblockBase implements IConstructable, ISurvivalConstructable {

    public int tCountCasing;
    public double mConfigSpeedBoost = 1;
    public boolean useExtraGas = false;

    public NaquadahReactor(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        useLongPower = true;
    }

    public NaquadahReactor(String aName) {
        super(aName);
        useLongPower = true;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(NAQUADAH_REACTOR_SOLID_FRONT_ACTIVE)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(NAQUADAH_REACTOR_SOLID_FRONT)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    public abstract int getCasingTextureID();

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(172, 67));
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipePool.NaquadahReactorRecipes;
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNL_ProcessingLogic() {

            @Override
            public @NotNull CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                int recipeReq = recipe.getMetadataOrDefault(NaquadahReactorMetadata.INSTANCE, 0);
                if (recipeReq > getReactorTier()) {
                    return CheckRecipeResultRegistry.insufficientMachineTier(recipeReq);
                }
                return super.validateRecipe(recipe);
            }

            @Override
            @Nonnull
            public GTNL_OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setNoOverclock(true);
            }
        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    public abstract int getReactorTier();

    public abstract int getDurationMultiple();

    public abstract int getEUtMultiple();

    public abstract FluidStack getExtraGas();

    @Nonnull
    @Override
    public CheckRecipeResult checkProcessing_EM() {
        useExtraGas = false;
        CheckRecipeResult result = super.checkProcessing_EM();
        // inputs are consumed at this point
        updateSlots();
        if (!result.wasSuccessful()) return result;

        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        mMaxProgresstime = (int) (processingLogic.getDuration() * mConfigSpeedBoost);
        lEUt = (long) ((GTNL_ProcessingLogic) processingLogic).lastRecipe.mSpecialValue
            * processingLogic.getCurrentParallels();

        mOutputItems = processingLogic.getOutputItems();
        mOutputFluids = processingLogic.getOutputFluids();

        List<FluidStack> tFluids = getStoredFluids();
        for (FluidStack fs : tFluids) {
            if (GTUtility.areFluidsEqual(fs, getExtraGas())) {
                mMaxProgresstime /= getDurationMultiple();
                lEUt *= getEUtMultiple();
                useExtraGas = true;
                break;
            }
        }

        return result;
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (aBaseMetaTileEntity.isServerSide()) {
            if (aTick % 20 == 0) {
                boolean found = false;
                for (MTEHatchMaintenance module : mMaintenanceHatches) {
                    if (module instanceof IConfigurationMaintenance customMaintenanceHatch
                        && customMaintenanceHatch.isConfiguration()) {
                        mConfigSpeedBoost = customMaintenanceHatch.getConfigTime() / 100d;
                        found = true;
                    }
                }
                if (!found) {
                    mConfigSpeedBoost = 1;
                }
            }
        }
    }

    @Override
    public boolean onRunningTick(ItemStack stack) {
        if (useExtraGas && (this.mProgresstime + 1) % 20 == 0 && this.mProgresstime > 0) {
            startRecipeProcessing();

            if (!depleteInput(getExtraGas())) {
                stopMachine(ShutDownReasonRegistry.NONE);
                endRecipeProcessing();
                return false;
            }

            endRecipeProcessing();
        }
        return super.onRunningTick(stack);
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        if (tag.hasKey("mEUt")) {
            currentTip.add(
                StatCollector.translateToLocal("NaquadahReactor.Generates.0") + EnumChatFormatting.WHITE
                    + tag.getLong("mEUt")
                    + " EU/t"
                    + EnumChatFormatting.RESET);
        }
        if (tag.hasKey("useExtraGas")) {
            currentTip.add(
                StatCollector.translateToLocal("NaquadahReactor.Generates.1") + EnumChatFormatting.WHITE
                    + tag.getBoolean("useExtraGas")
                    + EnumChatFormatting.RESET);
        }
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null) {
            if (tileEntity.isActive()) {
                tag.setBoolean("useExtraGas", useExtraGas);
            }
        }
    }

    @Override
    public String[] getInfoData() {
        String[] info = super.getInfoData();
        info[4] = StatCollector.translateToLocal("NaquadahReactor.Generates") + EnumChatFormatting.RED
            + GTUtility.formatNumbers(Math.abs(this.lEUt))
            + EnumChatFormatting.RESET
            + " EU/t";
        return info;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("useExtraGas", useExtraGas);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        useExtraGas = aNBT.getBoolean("useExtraGas");
    }

    public static class LargeNaquadahReactor extends NaquadahReactor {

        private static final String STRUCTURE_PIECE_MAIN = "main";
        private static final String LNR_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
            + "multiblock/large_naquadah_reactor";
        private static final String[][] shape = StructureUtils.readStructureFromFile(LNR_STRUCTURE_FILE_PATH);
        private static final int HORIZONTAL_OFF_SET = 12;
        private static final int VERTICAL_OFF_SET = 12;
        private static final int DEPTH_OFF_SET = 0;

        public LargeNaquadahReactor(int aID, String aName, String aNameRegional) {
            super(aID, aName, aNameRegional);
        }

        public LargeNaquadahReactor(String aName) {
            super(aName);
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
            return new LargeNaquadahReactor(this.mName);
        }

        @Override
        public IStructureDefinition<LargeNaquadahReactor> getStructure_EM() {
            return StructureDefinition.<LargeNaquadahReactor>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement('A', ofBlock(BlockLoader.metaCasing, 4))
                .addElement('B', ofBlock(BlockLoader.metaCasing, 5))
                .addElement(
                    'C',
                    buildHatchAdder(LargeNaquadahReactor.class).casingIndex(getCasingTextureID())
                        .dot(1)
                        .atLeast(Maintenance, InputHatch, OutputHatch, Energy.or(EnergyMulti), Dynamo.or(DynamoMulti))
                        .buildAndChain(onElementPass(x -> ++x.tCountCasing, ofBlock(sBlockCasings8, 10))))
                .addElement('D', ofBlock(sBlockCasingsTT, 0))
                .addElement('E', ofFrame(Materials.Naquadria))
                .addElement('F', ofFrame(Materials.Trinium))
                .addElement('G', ofBlock(blockCasings4Misc, 10))
                .build();
        }

        @Override
        public int getCasingTextureID() {
            return StructureUtils.getTextureIndex(sBlockCasings8, 10);
        }

        @Override
        public MultiblockTooltipBuilder createTooltip() {
            MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
            tt.addMachineType(StatCollector.translateToLocal("LargeNaquadahReactorRecipeType"))
                .addInfo(
                    StatCollector.translateToLocalFormatted("Tooltip_LargeNaquadahReactor_00", getMaxParallelRecipes()))
                .addInfo(StatCollector.translateToLocal("Tooltip_LargeNaquadahReactor_01"))
                .addInfo(
                    StatCollector.translateToLocalFormatted(
                        "Tooltip_LargeNaquadahReactor_02",
                        getDurationMultiple(),
                        getEUtMultiple()))
                .addInfo(
                    StatCollector.translateToLocalFormatted("Tooltip_LargeNaquadahReactor_03", getExtraGas().amount))
                .addSeparator()
                .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
                .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
                .beginStructureBlock(25, 25, 9, true)
                .addInputHatch(StatCollector.translateToLocal("Tooltip_LargeNaquadahReactor_Casing"))
                .addOutputHatch(StatCollector.translateToLocal("Tooltip_LargeNaquadahReactor_Casing"))
                .addEnergyHatch(StatCollector.translateToLocal("Tooltip_LargeNaquadahReactor_Casing"))
                .addDynamoHatch(StatCollector.translateToLocal("Tooltip_LargeNaquadahReactor_Casing"))
                .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_LargeNaquadahReactor_Casing"))
                .toolTipFinisher();
            return tt;
        }

        @Override
        public int getReactorTier() {
            return 0;
        }

        @Override
        public int getDurationMultiple() {
            return 20;
        }

        @Override
        public int getEUtMultiple() {
            return 32;
        }

        @Override
        public int getMaxParallelRecipes() {
            return 4;
        }

        @Override
        public FluidStack getExtraGas() {
            return Materials.Oxygen.getGas(2500);
        }

        @Override
        public boolean checkMachine_EM(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
            return checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET)
                && mMaintenanceHatches.size() <= 1
                && tCountCasing >= 50;
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
}
