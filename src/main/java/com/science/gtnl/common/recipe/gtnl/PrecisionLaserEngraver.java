package com.science.gtnl.common.recipe.gtnl;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTUtility;

public class PrecisionLaserEngraver implements IRecipePool {

    public RecipeMap<?> PLE = GTNLRecipeMaps.PrecisionLaserEngraverRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(ItemList.Circuit_Silicon_Wafer7.get(1))
            .itemOutputs(GTUtility.copyAmountUnsafe(4096, ItemList.Circuit_Wafer_CPU.get(1)))
            .duration(300)
            .eut(TierEU.RECIPE_UEV)
            .addTo(PLE);
    }
}
