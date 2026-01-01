package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.util.GTRecipeConstants.FUSION_THRESHOLD;

import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.MaterialPool;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gtPlusPlus.core.material.MaterialsElements;

public class FusionReactorRecipes implements IRecipePool {

    public RecipeMap<?> fR = RecipeMaps.fusionRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .fluidInputs(Materials.Hydrogen.getGas(144), Materials.Boron.getMolten(144))
            .fluidOutputs(Materials.Carbon.getPlasma(144))
            .duration(10)
            .eut(TierEU.RECIPE_IV)
            .metadata(FUSION_THRESHOLD, 20000000L)
            .addTo(fR);

        RecipeBuilder.builder()
            .fluidInputs(Materials.DraconiumAwakened.getMolten(432), Materials.Radon.getPlasma(144))
            .fluidOutputs(MaterialsElements.STANDALONE.DRAGON_METAL.getFluidStack(288))
            .duration(10)
            .eut(491520)
            .metadata(FUSION_THRESHOLD, 1000000000L)
            .addTo(fR);

        RecipeBuilder.builder()
            .fluidInputs(
                Materials.Arsenic.getMolten(32),
                new FluidStack(MaterialsElements.getInstance().RUTHENIUM.getFluid(), 16))
            .fluidOutputs(MaterialPool.Darmstadtium.getMolten(16))
            .duration(32)
            .eut(TierEU.RECIPE_LuV)
            .metadata(FUSION_THRESHOLD, 200000000L)
            .addTo(fR);

        RecipeBuilder.builder()
            .fluidInputs(Materials.Molybdenum.getMolten(144), Materials.Palladium.getMolten(144))
            .fluidOutputs(new FluidStack(MaterialsElements.getInstance().RADIUM.getFluid(), 288))
            .duration(32)
            .eut(TierEU.RECIPE_LuV)
            .metadata(FUSION_THRESHOLD, 200000000L)
            .addTo(fR);

        RecipeBuilder.builder()
            .fluidInputs(Materials.Oxygen.getGas(14), Materials.Copper.getMolten(14))
            .fluidOutputs(Materials.Strontium.getMolten(16))
            .duration(16)
            .eut(TierEU.RECIPE_LuV)
            .metadata(FUSION_THRESHOLD, 300000000L)
            .addTo(fR);

        RecipeBuilder.builder()
            .fluidInputs(Materials.Magnesium.getMolten(129), Materials.Iridium.getMolten(129))
            .fluidOutputs(MaterialPool.Actinium.getMolten(144))
            .duration(20)
            .eut(TierEU.RECIPE_UV)
            .metadata(FUSION_THRESHOLD, 1000000000L)
            .addTo(fR);
    }
}
