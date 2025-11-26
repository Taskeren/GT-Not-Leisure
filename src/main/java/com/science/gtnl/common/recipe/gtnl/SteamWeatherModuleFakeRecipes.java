package com.science.gtnl.common.recipe.gtnl;

import static com.dreammaster.scripts.IScriptLoader.missing;
import static gregtech.api.enums.Mods.*;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.RecipePool;

import gregtech.api.enums.GTValues;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;

public class SteamWeatherModuleFakeRecipes implements IRecipePool {

    public RecipeMap<?> SWMFR = RecipePool.SteamWeatherModuleFakeRecipes;

    @Override
    public void loadRecipes() {

        GTValues.RA.stdBuilder()
            .itemInputs(GTModHandler.getModItem(AdvancedSolarPanel.ID, "asp_crafting_items", 1, 9, missing))
            .specialValue(1)
            .duration(72000)
            .eut(0)
            .addTo(SWMFR);
    }
}
