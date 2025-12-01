package com.science.gtnl.common.recipe.gtnl;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.RecipePool;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;

public class SteamWoodcutterRecipes implements IRecipePool {

    public RecipeMap<?> SWR = RecipePool.WoodcutterRecipes;

    @Override
    public void loadRecipes() {

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Blocks.cactus, 0))
            .itemOutputs(new ItemStack(Blocks.cactus, 48))
            .duration(40)
            .eut(TierEU.RECIPE_LV)
            .addTo(SWR);

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Items.reeds, 0))
            .itemOutputs(new ItemStack(Items.reeds, 48))
            .duration(40)
            .eut(TierEU.RECIPE_LV)
            .addTo(SWR);
    }
}
