package com.science.gtnl.common.recipe.gtnl;

import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.machine.multiblock.GrandAssemblyLine;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.ItemList;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTUtility;

public class GrandAssemblyLineSpecialRecipes implements IRecipePool {

    public RecipeMap<?> GALSR = GTNLRecipeMaps.GrandAssemblyLineSpecialRecipes;

    @Override
    public void loadRecipes() {
        registerSpecialCircuit(ItemList.Casing_Dim_Injector.get(1), 1);
        registerSpecialCircuit(ItemList.Casing_Dim_Trans.get(1), 2);
    }

    public void registerSpecialCircuit(ItemStack output, int circuit) {
        if (output == null || circuit < 0) return;

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(circuit))
            .itemOutputs(output)
            .fake()
            .duration(0)
            .eut(0)
            .addTo(GALSR);

        GrandAssemblyLine.specialRecipe.put(GTUtility.ItemId.create(output), circuit);
    }
}
