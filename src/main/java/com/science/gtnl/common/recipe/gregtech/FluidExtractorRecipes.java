package com.science.gtnl.common.recipe.gregtech;

import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gtPlusPlus.core.material.MaterialsElements;

public class FluidExtractorRecipes implements IRecipePool {

    public RecipeMap<?> FER = RecipeMaps.fluidExtractionRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(MaterialsElements.getInstance().RADIUM.getDust(1))
            .fluidOutputs(new FluidStack(MaterialsElements.getInstance().RADIUM.getFluid(), 144))
            .duration(20)
            .eut(TierEU.RECIPE_EV)
            .addTo(FER);

        RecipeBuilder.builder()
            .itemInputs(MaterialsElements.getInstance().HAFNIUM.getDust(1))
            .fluidOutputs(new FluidStack(MaterialsElements.getInstance().HAFNIUM.getFluid(), 144))
            .duration(54)
            .eut(TierEU.RECIPE_MV)
            .addTo(FER);
    }
}
