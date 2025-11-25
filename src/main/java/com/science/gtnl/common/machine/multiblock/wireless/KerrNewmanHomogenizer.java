package com.science.gtnl.common.machine.multiblock.wireless;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.*;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.*;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.*;
import static gtPlusPlus.core.block.ModBlocks.*;
import static kubatech.loaders.BlockLoader.*;
import static tectech.thing.casing.TTCasingsContainer.*;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;

import goodgenerator.loader.Loaders;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.material.MaterialsAlloy;
import gtnhlanth.common.register.LanthItemList;

public class KerrNewmanHomogenizer extends WirelessEnergyMultiMachineBase<KerrNewmanHomogenizer> {

    private static final int HORIZONTAL_OFF_SET = 41;
    private static final int VERTICAL_OFF_SET = 12;
    private static final int DEPTH_OFF_SET = 7;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String KNH_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/kerr_newman_homogenizer";
    private static final String[][] shape = StructureUtils.readStructureFromFile(KNH_STRUCTURE_FILE_PATH);

    public KerrNewmanHomogenizer(String aName) {
        super(aName);
    }

    public KerrNewmanHomogenizer(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new KerrNewmanHomogenizer(this.mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("KerrNewmanHomogenizerRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_07"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_08"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_09"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_10"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(83, 36, 83, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_KerrNewmanHomogenizer_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_KerrNewmanHomogenizer_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_KerrNewmanHomogenizer_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_KerrNewmanHomogenizer_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_KerrNewmanHomogenizer_Casing"), 1)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(sBlockCasings8, 7);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_DTPF_ON)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_DTPF_OFF)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public IStructureDefinition<KerrNewmanHomogenizer> getStructureDefinition() {
        return StructureDefinition.<KerrNewmanHomogenizer>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', ofBlock(blockCasings3Misc, 13))
            .addElement('B', ofBlock(sBlockCasings10, 3))
            .addElement('C', ofBlock(sBlockCasings10, 11))
            .addElement('D', ofFrame(Materials.TungstenCarbide))
            .addElement(
                'E',
                buildHatchAdder(KerrNewmanHomogenizer.class)
                    .atLeast(
                        Maintenance,
                        InputBus,
                        OutputBus,
                        InputHatch,
                        OutputHatch,
                        Energy.or(ExoticEnergy),
                        ParallelCon)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasings8, 7))))
            .addElement('F', ofBlock(defcCasingBlock, 7))
            .addElement('G', ofBlock(blockCasings2Misc, 2))
            .addElement('H', ofBlock(BlockLoader.metaCasing, 5))
            .addElement('I', ofBlock(sBlockCasings10, 12))
            .addElement('J', ofBlock(sBlockCasings3, 12))
            .addElement('K', ofBlock(sBlockCasings11, 4))
            .addElement('L', ofBlock(sBlockCasings9, 0))
            .addElement('M', ofBlock(sBlockCasings9, 7))
            .addElement('N', ofBlock(sBlockTintedGlass, 1))
            .addElement('O', ofBlock(sBlockCasings10, 7))
            .addElement('P', ofBlock(BlockLoader.metaCasing, 4))
            .addElement('Q', ofFrame(Materials.Trinium))
            .addElement('R', ofBlock(Loaders.gravityStabilizationCasing, 0))
            .addElement('S', ofBlock(blockCasings6Misc, 0))
            .addElement('T', ofBlock(sBlockCasingsTT, 6))
            .addElement('U', ofBlock(LanthItemList.SHIELDED_ACCELERATOR_CASING, 0))
            .addElement('V', ofBlock(sBlockCasingsDyson, 1))
            .addElement('W', ofBlock(sBlockCasings9, 11))
            .addElement('X', ofBlock(blockCasings2Misc, 12))
            .addElement('Y', ofBlock(sBlockCasings1, 14))
            .addElement(
                'Z',
                ofBlockAnyMeta(
                    Block.getBlockFromItem(
                        MaterialsAlloy.HASTELLOY_N.getFrameBox(1)
                            .getItem())))
            .addElement('0', ofBlock(blockSpecialMultiCasings, 15))
            .addElement('1', ofBlock(sBlockCasings1, 14))
            .addElement(
                '2',
                ofBlockAnyMeta(
                    Block.getBlockFromItem(
                        MaterialsAlloy.HASTELLOY_C276.getFrameBox(1)
                            .getItem())))
            .addElement('3', ofFrame(Materials.NaquadahAlloy))
            .build();
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
        if (this.mMachine) return -1;
        return this.survivalBuildPiece(
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
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch())
            return false;
        setupParameters();
        return mCountCasing > 200;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTPPRecipeMaps.mixerNonCellRecipes;
    }

}
