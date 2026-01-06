package com.science.gtnl.common.recipe.gtnl;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.Mods;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;

public class SteamWeatherModuleRecipes implements IRecipePool {

    public RecipeMap<?> SWMR = GTNLRecipeMaps.SteamWeatherModuleRecipes;

    @Override
    public void loadRecipes() {

        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(Mods.Natura.ID, "Cloud", 16, 3),
                GTModHandler.getModItem(Mods.Thaumcraft.ID, "blockCrystal", 1, 1))
            .specialValue(1)
            .duration(36000)
            .eut(0)
            .addTo(SWMR);

        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(Mods.Natura.ID, "Cloud", 16, 2),
                GTModHandler.getModItem(Mods.Thaumcraft.ID, "blockCrystal", 1, 2))
            .specialValue(2)
            .duration(36000)
            .eut(0)
            .addTo(SWMR);

        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(Mods.Natura.ID, "Cloud", 16, 1),
                GTModHandler.getModItem(Mods.Thaumcraft.ID, "blockCrystal", 1, 2),
                GTModHandler.getModItem(Mods.Thaumcraft.ID, "blockCrystal", 1))
            .specialValue(3)
            .duration(36000)
            .eut(0)
            .addTo(SWMR);
    }
}
