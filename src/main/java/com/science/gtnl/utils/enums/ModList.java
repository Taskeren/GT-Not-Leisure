package com.science.gtnl.utils.enums;

import java.util.Locale;

import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.common.Loader;

public enum ModList {

    ScienceNotLeisure(ModIds.SCIENCE_NOT_LEISURE, Names.SCIENCE_NOT_LEISURE),
    TakoTech(ModIds.TAKO_TECH, Names.TAKO_TECH),
    EyeOfHarmonyBuffer(ModIds.EYE_OF_HARMONY_BUFFER, Names.EYE_OF_HARMONY_BUFFER),
    ProgrammableHatches(ModIds.PROGRAMMABLE_HATCHES, Names.PROGRAMMABLE_HATCHES),
    TwistSpaceTechnology(ModIds.TWIST_SPACE_TECHNOLOGY, Names.TWIST_SPACE_TECHNOLOGY),
    BoxPlusPlus(ModIds.BOX_PLUS_PLUS, Names.BOX_PLUS_PLUS),
    NHUtilities(ModIds.NH_UTILITIES, Names.NH_UTILITIES),
    AE2Thing(ModIds.AE2_THING, Names.AE2_THING),
    QzMiner(ModIds.QZ_MINER, Names.QZ_MINER),
    OTHTechnology(ModIds.OTH_TECHNOLOGY, Names.OTH_TECHNOLOGY),
    Baubles(ModIds.BAUBLES, Names.BAUBLES),
    Overpowered(ModIds.OVER_POWERED, Names.OVER_POWERED),
    ThinkTech(ModIds.THINK_TECH, Names.THINK_TECH),
    VMTweak(ModIds.VMT_TWEAK, Names.VMT_TWEAK),
    ReAvaritia(ModIds.RE_AVARITIA, Names.RE_AVARITIA),
    Sudoku(ModIds.SUDOKU, Names.SUDOKU),
    GiveCount(ModIds.GIVE_COUNT, Names.GIVECOUNT),
    ChromaticTooltips(ModIds.CHROMATIC_TOOLTIPS, Names.CHROMATIC_TOOLTIPS),
    ChromaticTooltipsCompat(ModIds.CHROMATIC_TOOLTIPS_COMPAT, Names.CHROMATIC_TOOLTIPS_COMPAT);

    public static class ModIds {

        public static final String SCIENCE_NOT_LEISURE = "sciencenotleisure";
        public static final String EYE_OF_HARMONY_BUFFER = "eyeofharmonybuffer";
        public static final String PROGRAMMABLE_HATCHES = "programmablehatches";
        public static final String TWIST_SPACE_TECHNOLOGY = "TwistSpaceTechnology";
        public static final String BOX_PLUS_PLUS = "boxplusplus";
        public static final String NH_UTILITIES = "NHUtilities";
        public static final String AE2_THING = "ae2thing";
        public static final String QZ_MINER = "qz_miner";
        public static final String OTH_TECHNOLOGY = "123Technology";
        public static final String BAUBLES = "Baubles";
        public static final String OVER_POWERED = "Overpowered";
        public static final String THINK_TECH = "thinktech";
        public static final String VMT_TWEAK = "vmtweak";
        public static final String GIVE_COUNT = "givecount";
        public static final String RE_AVARITIA = "reavaritia";
        public static final String SUDOKU = "sudoku";
        public static final String TAKO_TECH = "TakoTech";
        public static final String CHROMATIC_TOOLTIPS = "chromatictooltips";
        public static final String CHROMATIC_TOOLTIPS_COMPAT = "chromatictooltipscompat";
    }

    public static class Names {

        public static final String SCIENCE_NOT_LEISURE = "ScienceNotLeisure";
        public static final String EYE_OF_HARMONY_BUFFER = "EyeOfHarmonyBuffer";
        public static final String PROGRAMMABLE_HATCHES = "ProgrammableHatches";
        public static final String TWIST_SPACE_TECHNOLOGY = "TwistSpaceTechnology";
        public static final String BOX_PLUS_PLUS = "BoxPlusPlus";
        public static final String NH_UTILITIES = "NH-Utilities";
        public static final String AE2_THING = "AE2Things";
        public static final String QZ_MINER = "QzMiner";
        public static final String OTH_TECHNOLOGY = "123Technology";
        public static final String BAUBLES = "Baubles";
        public static final String OVER_POWERED = "Overpowered";
        public static final String THINK_TECH = "ThinkTech";
        public static final String VMT_TWEAK = "VoidMinerTweak";
        public static final String GIVECOUNT = "GiveCount";
        public static final String RE_AVARITIA = "ReAvaritia";
        public static final String SUDOKU = "Sudoku";
        public static final String TAKO_TECH = "TakoTech";
        public static final String CHROMATIC_TOOLTIPS = "ChromaticTooltips";
        public static final String CHROMATIC_TOOLTIPS_COMPAT = "ChromaticTooltipsCompat";
    }

    public final String ID;
    public final String resourceDomain;
    public final String displayName;
    private Boolean modLoaded;

    ModList(String ID, String displayName) {
        this.ID = ID;
        this.resourceDomain = ID.toLowerCase(Locale.ENGLISH);
        this.displayName = displayName;
    }

    public boolean isModLoaded() {
        if (this.modLoaded == null) {
            this.modLoaded = Loader.isModLoaded(ID);
        }
        return this.modLoaded;
    }

    public String getModId() {
        return this.ID;
    }

    public String getResourcePath(String... path) {
        return this.getResourceLocation(path)
            .toString();
    }

    public ResourceLocation getResourceLocation(String... path) {
        return new ResourceLocation(this.resourceDomain, String.join("/", path));
    }
}
