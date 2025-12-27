package com.science.gtnl.common.recipe.gregtech;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;

public class MaceratorRecipes implements IRecipePool {

    public RecipeMap<?> MR = RecipeMaps.maceratorRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.AlgaeBiomass.get(6))
            .itemOutputs(GregtechItemList.Compost.get(1))
            .duration(400)
            .eut(TierEU.RECIPE_ULV)
            .addTo(MR);
    }
}
