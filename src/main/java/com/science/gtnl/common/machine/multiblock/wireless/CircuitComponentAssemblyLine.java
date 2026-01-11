package com.science.gtnl.common.machine.multiblock.wireless;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.*;
import static goodgenerator.loader.Loaders.FRF_Coil_1;
import static gregtech.api.GregTechAPI.sBlockCasings2;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.*;
import static gtPlusPlus.core.block.ModBlocks.blockCasings3Misc;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.common.machine.hatch.NanitesInputBus;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.enums.GTNLStructureChannels;

import goodgenerator.loader.Loaders;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.IGTHatchAdder;
import gregtech.api.util.MultiblockTooltipBuilder;
import tectech.thing.block.BlockQuantumGlass;
import tectech.thing.casing.BlockGTCasingsTT;

public class CircuitComponentAssemblyLine extends WirelessEnergyMultiMachineBase<CircuitComponentAssemblyLine>
    implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String ACAL_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/advanced_circuit_assembly_line";
    private static final String[][] shape = StructureUtils.readStructureFromFile(ACAL_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 0;
    private static final int VERTICAL_OFF_SET = 2;
    private static final int DEPTH_OFF_SET = 0;

    public int casingTier;

    public NanitesInputBus nanitesInputBus;

    public double euDiscount = 1;
    public double speedBonus = 1;
    public double failureBonus = 1;
    public double outputCoefficient = 1;
    public int nanitesParallel = 1;
    public int maxTierSkip = 1;

    public CircuitComponentAssemblyLine(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public CircuitComponentAssemblyLine(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new CircuitComponentAssemblyLine(this.mName);
    }

    @Override
    public IStructureDefinition<CircuitComponentAssemblyLine> getStructureDefinition() {
        return StructureDefinition.<CircuitComponentAssemblyLine>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', ofBlock(FRF_Coil_1, 0))
            .addElement(
                'B',
                GTNLStructureChannels.COMPONENT_ASSEMBLY_LINE_CASING.use(
                    ofBlocksTiered(
                        (block, meta) -> block == Loaders.componentAssemblylineCasing ? meta : -1,
                        IntStream.range(0, 14)
                            .mapToObj(i -> Pair.of(Loaders.componentAssemblylineCasing, i))
                            .collect(Collectors.toList()),
                        -2,
                        (t, meta) -> t.casingTier = meta,
                        t -> t.casingTier)))
            .addElement('C', ofBlock(sBlockCasings2, 5))
            .addElement('D', ofBlock(sBlockCasingsTT, 0))
            .addElement('E', ofBlock(sBlockCasingsTT, 1))
            .addElement('F', ofBlock(sBlockCasingsTT, 2))
            .addElement(
                'G',
                buildHatchAdder(CircuitComponentAssemblyLine.class).casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(
                        Maintenance,
                        InputHatch,
                        InputBus,
                        OutputBus,
                        Maintenance,
                        Energy.or(ExoticEnergy),
                        ParallelCon,
                        CustomHatchElement.NanitesInputBus)
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasingsTT, 3))))
            .addElement('H', ofBlock(sBlockCasingsTT, 7))
            .addElement('I', ofBlock(sBlockCasingsTT, 8))
            .addElement('J', ofBlock(blockCasings3Misc, 15))
            .addElement('K', ofBlock(BlockQuantumGlass.INSTANCE, 0))
            .build();
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("CircuitComponentAssemblyLineRecipes"))
            .addInfo(StatCollector.translateToLocal("Tooltip_CircuitComponentAssemblyLine_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_CircuitComponentAssemblyLine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_CircuitComponentAssemblyLine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_CircuitComponentAssemblyLine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PerfectOverclock"))
            .addTecTechHatchInfo()
            .beginStructureBlock(32, 5, 5, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_CircuitComponentAssemblyLine_Casing_00"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_CircuitComponentAssemblyLine_Casing_00"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_CircuitComponentAssemblyLine_Casing_00"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_CircuitComponentAssemblyLine_Casing_00"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_CircuitComponentAssemblyLine_Casing_00"))
            .addSubChannelUsage(GTNLStructureChannels.COMPONENT_ASSEMBLY_LINE_CASING)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int aColorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return BlockGTCasingsTT.textureOffset + 3;
    }

    @Override
    public @NotNull CheckRecipeResult checkProcessing() {
        return super.checkProcessing();
    }

    @Override
    public void setupProcessingLogic(ProcessingLogic logic) {
        super.setupProcessingLogic(logic);
        logic.setMaxTierSkips(maxTierSkip);
    }

    @Override
    public double getEUtDiscount() {
        return ((wirelessUpgrade ? 0.5 : 1) - (mParallelTier / 50.0)) * euDiscount;
    }

    @Override
    public double getDurationModifier() {
        return (1.0 / (wirelessUpgrade ? 2 : 1) - (Math.max(0, mParallelTier - 1) / 50.0)) * speedBonus;
    }

    @Override
    public int getMaxParallelRecipes() {
        return Math.min(nanitesParallel, super.getMaxParallelRecipes());
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        this.buildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            hintsOnly,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET);
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
    public boolean checkMachine(IGregTechTileEntity iGregTechTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return mCountCasing >= 30;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.CircuitComponentAssemblyLineRecipes;
    }

    public boolean addNanitesInputBusToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) return false;
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) return false;
        if (aMetaTileEntity instanceof NanitesInputBus bus) {
            bus.updateTexture(aBaseCasingIndex);
            bus.updateCraftingIcon(this.getMachineCraftingIcon());
            nanitesInputBus = bus;
            return true;
        }
        return false;
    }

    public enum CustomHatchElement implements IHatchElement<CircuitComponentAssemblyLine> {

        NanitesInputBus("GT5U.MBTT.InputBus", CircuitComponentAssemblyLine::addNanitesInputBusToMachineList,
            NanitesInputBus.class) {

            @Override
            public long count(CircuitComponentAssemblyLine t) {
                return t.nanitesInputBus != null ? 1 : 0;
            }
        };

        private final String name;
        private final List<Class<? extends IMetaTileEntity>> mteClasses;
        private final IGTHatchAdder<CircuitComponentAssemblyLine> adder;

        @SafeVarargs
        CustomHatchElement(String name, IGTHatchAdder<CircuitComponentAssemblyLine> adder,
            Class<? extends IMetaTileEntity>... mteClasses) {
            this.name = name;
            this.mteClasses = Collections.unmodifiableList(Arrays.asList(mteClasses));
            this.adder = adder;
        }

        @Override
        public List<? extends Class<? extends IMetaTileEntity>> mteClasses() {
            return mteClasses;
        }

        @Override
        public String getDisplayName() {
            return GTUtility.translate(name);
        }

        public IGTHatchAdder<? super CircuitComponentAssemblyLine> adder() {
            return adder;
        }
    }

}
