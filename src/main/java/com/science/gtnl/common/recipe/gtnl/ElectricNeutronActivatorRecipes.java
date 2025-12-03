package com.science.gtnl.common.recipe.gtnl;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.RecipePool;

import goodgenerator.api.recipe.GoodGeneratorRecipeMaps;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTRecipe;

public class ElectricNeutronActivatorRecipes implements IRecipePool {

    public RecipeMap<?> ENAR = RecipePool.ElectricNeutronActivatorRecipes;

    @Override
    public void loadRecipes() {
        for (GTRecipe recipe : GoodGeneratorRecipeMaps.neutronActivatorRecipes.getAllRecipes()) {
            GTRecipe recipeNew = recipe.copy();
            int minNKE = recipeNew.mSpecialValue % 10000;
            int maxNKE = recipeNew.mSpecialValue / 10000;
            recipeNew.mEUt = minNKE * maxNKE;
            ENAR.add(recipeNew);
        }
    }
}
