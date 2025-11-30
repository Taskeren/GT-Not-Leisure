package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.NAQUADAH_REACTOR_SOLID_FRONT;
import static gregtech.api.enums.Textures.BlockIcons.NAQUADAH_REACTOR_SOLID_FRONT_ACTIVE;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;
import static gtPlusPlus.core.block.ModBlocks.blockCasings4Misc;
import static kubatech.loaders.BlockLoader.*;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;
import static tectech.thing.metaTileEntity.multi.base.TTMultiblockBase.HatchElement.*;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
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
import com.science.gtnl.common.render.tile.AdvancedHyperNaquadahReactorRenderer;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.item.ItemUtils;
import com.science.gtnl.utils.recipes.GTNL_OverclockCalculator;
import com.science.gtnl.utils.recipes.GTNL_ProcessingLogic;
import com.science.gtnl.utils.recipes.metadata.NaquadahReactorMetadata;

import goodgenerator.items.GGMaterial;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.GregTechTileClientEvents;
import gregtech.api.metatileentity.implementations.MTEHatchMaintenance;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gregtech.common.render.IMTERenderer;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;

public abstract class NaquadahReactor extends TTMultiblockBase implements IConstructable, ISurvivalConstructable {

    public int mCountCasing;
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
                doExplosion(lEUt);
                stopMachine(ShutDownReasonRegistry.NONE);
                endRecipeProcessing();
                return false;
            }

            endRecipeProcessing();
        }
        return super.onRunningTick(stack);
    }

    @Override
    public void doExplosion(long aExplosionPower) {
        float tStrength = GTValues.getExplosionPowerForVoltage(aExplosionPower);
        final int tX = getBaseMetaTileEntity().getXCoord();
        final int tY = getBaseMetaTileEntity().getYCoord();
        final int tZ = getBaseMetaTileEntity().getZCoord();
        final World tWorld = getBaseMetaTileEntity().getWorld();
        GTUtility.sendSoundToPlayers(tWorld, SoundResource.IC2_MACHINES_MACHINE_OVERLOAD, 1.0F, -1, tX, tY, tZ);
        tWorld.setBlock(tX, tY, tZ, Blocks.air);
        if (GregTechAPI.sMachineExplosions) tWorld.createExplosion(null, tX + 0.5, tY + 0.5, tZ + 0.5, tStrength, true);
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
        if (tag.getBoolean("useExtraGas")) {
            currentTip.add(StatCollector.translateToLocal("NaquadahReactor.Generates.1"));
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
                        .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasings8, 10))))
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
            tt.addMachineType(StatCollector.translateToLocal("NaquadahReactorRecipeType"))
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
                && mCountCasing >= 50;
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

    public static class HyperNaquadahReactor extends NaquadahReactor {

        private static final String STRUCTURE_PIECE_MAIN = "main";
        private static final String HNR_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
            + "multiblock/hyper_naquadah_reactor";
        private static final String[][] shape = StructureUtils.readStructureFromFile(HNR_STRUCTURE_FILE_PATH);
        private static final int HORIZONTAL_OFF_SET = 13;
        private static final int VERTICAL_OFF_SET = 10;
        private static final int DEPTH_OFF_SET = 7;

        public HyperNaquadahReactor(int aID, String aName, String aNameRegional) {
            super(aID, aName, aNameRegional);
        }

        public HyperNaquadahReactor(String aName) {
            super(aName);
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
            return new HyperNaquadahReactor(this.mName);
        }

        @Override
        public IStructureDefinition<HyperNaquadahReactor> getStructure_EM() {
            return StructureDefinition.<HyperNaquadahReactor>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement('A', ofBlock(sBlockCasingsTT, 0))
                .addElement('B', ofBlock(BlockLoader.metaCasing, 18))
                .addElement('C', ofBlock(sBlockCasingsTT, 6))
                .addElement('D', ofBlock(BlockLoader.metaCasing, 4))
                .addElement('E', ofBlock(sBlockCasingsSE, 0))
                .addElement('F', ofBlock(sBlockCasingsTT, 4))
                .addElement(
                    'G',
                    buildHatchAdder(HyperNaquadahReactor.class).casingIndex(getCasingTextureID())
                        .dot(1)
                        .atLeast(Maintenance, InputHatch, OutputHatch, Energy.or(EnergyMulti), Dynamo.or(DynamoMulti))
                        .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(defcCasingBlock, 7))))
                .addElement('H', ofBlock(BlockLoader.metaBlockGlass, 2))
                .addElement('I', ofFrame(Materials.Neutronium))
                .build();
        }

        @Override
        public int getCasingTextureID() {
            return (1 << 7) + (15 + 48);
        }

        @Override
        public MultiblockTooltipBuilder createTooltip() {
            MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
            tt.addMachineType(StatCollector.translateToLocal("NaquadahReactorRecipeType"))
                .addInfo(
                    StatCollector.translateToLocalFormatted("Tooltip_HyperNaquadahReactor_00", getMaxParallelRecipes()))
                .addInfo(StatCollector.translateToLocal("Tooltip_HyperNaquadahReactor_01"))
                .addInfo(
                    StatCollector.translateToLocalFormatted(
                        "Tooltip_HyperNaquadahReactor_02",
                        getDurationMultiple(),
                        getEUtMultiple()))
                .addInfo(
                    StatCollector.translateToLocalFormatted("Tooltip_HyperNaquadahReactor_03", getExtraGas().amount))
                .addSeparator()
                .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
                .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
                .beginStructureBlock(27, 21, 21, true)
                .addInputHatch(StatCollector.translateToLocal("Tooltip_HyperNaquadahReactor_Casing"))
                .addOutputHatch(StatCollector.translateToLocal("Tooltip_HyperNaquadahReactor_Casing"))
                .addEnergyHatch(StatCollector.translateToLocal("Tooltip_HyperNaquadahReactor_Casing"))
                .addDynamoHatch(StatCollector.translateToLocal("Tooltip_HyperNaquadahReactor_Casing"))
                .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_HyperNaquadahReactor_Casing"))
                .toolTipFinisher();
            return tt;
        }

        @Override
        public int getReactorTier() {
            return 1;
        }

        @Override
        public int getDurationMultiple() {
            return 10;
        }

        @Override
        public int getEUtMultiple() {
            return 64;
        }

        @Override
        public int getMaxParallelRecipes() {
            return 64;
        }

        @Override
        public FluidStack getExtraGas() {
            return GGMaterial.naquadahGas.getFluidOrGas(100);
        }

        @Override
        public boolean checkMachine_EM(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
            return checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET)
                && mMaintenanceHatches.size() <= 1
                && mCountCasing >= 50;
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

    public static class AdvancedHyperNaquadahReactor extends NaquadahReactor implements IMTERenderer {

        private static final String STRUCTURE_PIECE_MAIN = "main";
        private static final String STRUCTURE_PIECE_SPHERE = "sphere";
        private static final String STRUCTURE_PIECE_SPHERE_AIR = "sphere_air";
        private static final String AHNR_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
            + "multiblock/advanced_hyper_naquadah_reactor";
        private static final String AHNRS_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
            + "multiblock/advanced_hyper_naquadah_reactor_sphere";
        private static final String[][] shape = StructureUtils.readStructureFromFile(AHNR_STRUCTURE_FILE_PATH);
        private static final String[][] shapeSphere = StructureUtils.readStructureFromFile(AHNRS_STRUCTURE_FILE_PATH);
        private static final String[][] shapeSphereAir = StructureUtils.replaceLetters(shapeSphere, "I");
        private static final int HORIZONTAL_OFF_SET = 17;
        private static final int VERTICAL_OFF_SET = 16;
        private static final int DEPTH_OFF_SET = 0;
        private static final int HORIZONTAL_OFF_SET_SPHERE = 5;
        private static final int VERTICAL_OFF_SET_SPHERE = 13;
        private static final int DEPTH_OFF_SET_SPHERE = -13;

        public boolean enableRender = true;
        public boolean isRenderActive = false;
        public float rotation = 0;

        public AdvancedHyperNaquadahReactor(int aID, String aName, String aNameRegional) {
            super(aID, aName, aNameRegional);
        }

        public AdvancedHyperNaquadahReactor(String aName) {
            super(aName);
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
            return new AdvancedHyperNaquadahReactor(this.mName);
        }

        @Override
        public void renderTESR(double x, double y, double z, float timeSinceLastTick) {
            if (!isRenderActive || !enableRender) return;
            AdvancedHyperNaquadahReactorRenderer.renderTileEntity(this, x, y, z, timeSinceLastTick);
        }

        @Override
        public IStructureDefinition<AdvancedHyperNaquadahReactor> getStructure_EM() {
            return StructureDefinition.<AdvancedHyperNaquadahReactor>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addShape(STRUCTURE_PIECE_SPHERE, transpose(shapeSphere))
                .addShape(STRUCTURE_PIECE_SPHERE_AIR, transpose(shapeSphereAir))
                .addElement('A', ofBlock(sBlockCasingsTT, 6))
                .addElement('B', ofBlock(sBlockCasings1, 14))
                .addElement('C', ofBlock(sBlockCasingsDyson, 0))
                .addElement('D', ofBlock(sBlockCasings9, 13))
                .addElement('E', ofBlock(sBlockCasings1, 12))
                .addElement(
                    'F',
                    buildHatchAdder(AdvancedHyperNaquadahReactor.class).casingIndex(getCasingTextureID())
                        .dot(1)
                        .atLeast(Maintenance, InputHatch, OutputHatch, Energy.or(EnergyMulti), Dynamo.or(DynamoMulti))
                        .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(defcCasingBlock, 7))))
                .addElement('G', ofFrame(Materials.Naquadria))
                .addElement('H', ofBlock(BlockLoader.metaCasing, 18))
                .addElement('I', isAir())
                .build();
        }

        @Override
        public int getCasingTextureID() {
            return (1 << 7) + (15 + 48);
        }

        @Override
        public void onValueUpdate(byte aValue) {
            isRenderActive = (aValue & 0x01) != 0;
            enableRender = (aValue & 0x02) != 0;
        }

        @Override
        public byte getUpdateData() {
            byte data = 0;
            if (isRenderActive) data |= 0x01;
            if (enableRender) data |= 0x02;
            return data;
        }

        @Override
        public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
            super.onPostTick(aBaseMetaTileEntity, aTick);
            rotation += 0.5F;
        }

        @Override
        public boolean onWireCutterRightClick(ForgeDirection side, ForgeDirection wrenchingSide, EntityPlayer aPlayer,
            float aX, float aY, float aZ, ItemStack aTool) {
            if (getBaseMetaTileEntity().isServerSide()) {
                enableRender = !enableRender;
                GTUtility.sendChatToPlayer(
                    aPlayer,
                    StatCollector.translateToLocal("Info_Render_" + (enableRender ? "Enabled" : "Disabled")));
                checkStructure(true);
            }
            return true;
        }

        public void destroySphere() {
            buildPiece(
                STRUCTURE_PIECE_SPHERE_AIR,
                null,
                false,
                HORIZONTAL_OFF_SET_SPHERE,
                VERTICAL_OFF_SET_SPHERE,
                DEPTH_OFF_SET_SPHERE);
            isRenderActive = true;
        }

        public void buildSphere() {
            buildPiece(
                STRUCTURE_PIECE_SPHERE,
                null,
                false,
                HORIZONTAL_OFF_SET_SPHERE,
                VERTICAL_OFF_SET_SPHERE,
                DEPTH_OFF_SET_SPHERE);
            isRenderActive = false;
        }

        public ChunkCoordinates getRenderPos() {
            ForgeDirection back = getExtendedFacing().getRelativeBackInWorld();
            ForgeDirection up = getExtendedFacing().getRelativeUpInWorld();

            int xOffset = 18 * back.offsetX + 8 * up.offsetX;
            int yOffset = 18 * back.offsetY + 8 * up.offsetY;
            int zOffset = 18 * back.offsetZ + 8 * up.offsetZ;

            return new ChunkCoordinates(xOffset, yOffset, zOffset);
        }

        @Override
        public MultiblockTooltipBuilder createTooltip() {
            MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
            tt.addMachineType(StatCollector.translateToLocal("NaquadahReactorRecipeType"))
                .addInfo(
                    StatCollector
                        .translateToLocalFormatted("Tooltip_AdvancedHyperNaquadahReactor_00", getMaxParallelRecipes()))
                .addInfo(StatCollector.translateToLocal("Tooltip_AdvancedHyperNaquadahReactor_01"))
                .addInfo(
                    StatCollector.translateToLocalFormatted(
                        "Tooltip_AdvancedHyperNaquadahReactor_02",
                        getDurationMultiple(),
                        getEUtMultiple()))
                .addInfo(
                    StatCollector
                        .translateToLocalFormatted("Tooltip_AdvancedHyperNaquadahReactor_03", getExtraGas().amount))
                .addSeparator()
                .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
                .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
                .beginStructureBlock(35, 19, 36, true)
                .addInputHatch(StatCollector.translateToLocal("Tooltip_AdvancedHyperNaquadahReactor_Casing"))
                .addOutputHatch(StatCollector.translateToLocal("Tooltip_AdvancedHyperNaquadahReactor_Casing"))
                .addEnergyHatch(StatCollector.translateToLocal("Tooltip_AdvancedHyperNaquadahReactor_Casing"))
                .addDynamoHatch(StatCollector.translateToLocal("Tooltip_AdvancedHyperNaquadahReactor_Casing"))
                .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_AdvancedHyperNaquadahReactor_Casing"))
                .toolTipFinisher();
            return tt;
        }

        @Override
        public int getReactorTier() {
            return 2;
        }

        @Override
        public int getDurationMultiple() {
            return 4;
        }

        @Override
        public int getEUtMultiple() {
            return 1024;
        }

        @Override
        public int getMaxParallelRecipes() {
            return 4096;
        }

        @Override
        public FluidStack getExtraGas() {
            return MaterialsUEVplus.SpaceTime.getMolten(100);
        }

        @Override
        public boolean checkMachine_EM(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
            if (isRenderActive) {
                if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET)
                    || !checkPiece(
                        STRUCTURE_PIECE_SPHERE_AIR,
                        HORIZONTAL_OFF_SET_SPHERE,
                        VERTICAL_OFF_SET_SPHERE,
                        DEPTH_OFF_SET_SPHERE)) {
                    buildSphere();
                    return false;
                }
            } else if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET)
                || !checkPiece(
                    STRUCTURE_PIECE_SPHERE,
                    HORIZONTAL_OFF_SET_SPHERE,
                    VERTICAL_OFF_SET_SPHERE,
                    DEPTH_OFF_SET_SPHERE)) {
                        return false;
                    }

            if (mCountCasing < 50 || mMaintenanceHatches.size() > 1) return false;

            if (!isRenderActive && enableRender && mTotalRunTime > 0) {
                destroySphere();
            } else if (isRenderActive && !enableRender) {
                buildSphere();
            }

            getBaseMetaTileEntity().sendBlockEvent(GregTechTileClientEvents.CHANGE_CUSTOM_DATA, getUpdateData());

            return true;
        }

        @Override
        public boolean isFlipChangeAllowed() {
            if (mMachine || isRenderActive) return false;
            return super.isFlipChangeAllowed();
        }

        @Override
        public boolean isRotationChangeAllowed() {
            if (mMachine || isRenderActive) return false;
            return super.isRotationChangeAllowed();
        }

        @Override
        public void onBlockDestroyed() {
            super.onBlockDestroyed();
            if (isRenderActive) {
                buildSphere();
            }
        }

        @Override
        public void saveNBTData(NBTTagCompound aNBT) {
            super.saveNBTData(aNBT);
            aNBT.setBoolean("isRenderActive", isRenderActive);
            aNBT.setBoolean("enableRender", enableRender);
        }

        @Override
        public void loadNBTData(NBTTagCompound aNBT) {
            super.loadNBTData(aNBT);
            isRenderActive = aNBT.getBoolean("isRenderActive");
            enableRender = aNBT.getBoolean("enableRender");
        }

        @Override
        public void construct(ItemStack stackSize, boolean hintsOnly) {
            buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
            buildPiece(
                STRUCTURE_PIECE_SPHERE,
                stackSize,
                hintsOnly,
                HORIZONTAL_OFF_SET_SPHERE,
                VERTICAL_OFF_SET_SPHERE,
                DEPTH_OFF_SET_SPHERE);
        }

        @Override
        public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
            if (this.mMachine) return -1;
            int realBudget = elementBudget >= 500 ? elementBudget : Math.min(500, elementBudget * 5);

            int built;
            built = survivalBuildPiece(
                STRUCTURE_PIECE_MAIN,
                stackSize,
                HORIZONTAL_OFF_SET,
                VERTICAL_OFF_SET,
                DEPTH_OFF_SET,
                realBudget,
                env,
                false,
                true);

            if (built >= 0) return built;

            built += survivalBuildPiece(
                STRUCTURE_PIECE_SPHERE,
                stackSize,
                HORIZONTAL_OFF_SET_SPHERE,
                VERTICAL_OFF_SET_SPHERE,
                DEPTH_OFF_SET_SPHERE,
                realBudget,
                env,
                false,
                true);
            return built;
        }
    }
}
