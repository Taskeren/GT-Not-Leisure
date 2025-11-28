package com.science.gtnl.common.recipe.gregtech;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;

public class HammerRecipes implements IRecipePool {

    public RecipeMap<?> HR = RecipeMaps.hammerRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.stick, Materials.Netherite, 2))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.stickLong, Materials.Netherite, 1))
            .duration(100)
            .eut(TierEU.RECIPE_IV)
            .addTo(HR);
    }
}
