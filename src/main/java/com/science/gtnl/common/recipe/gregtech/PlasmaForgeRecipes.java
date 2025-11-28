package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.util.GTRecipeConstants.COIL_HEAT;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.MaterialPool;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;

public class PlasmaForgeRecipes implements IRecipePool {

    public RecipeMap<?> PFR = RecipeMaps.plasmaForgeRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 4, 124),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 4, 32415),
                GTModHandler.getModItem("miscutils", "itemPlateDenseDragonblood", 16, 0),
                GTModHandler.getModItem("appliedenergistics2", "item.ItemMultiMaterial", 16, 34),
                GTModHandler.getModItem("Avaritia", "Singularity", 2, 0),
                GTModHandler.getModItem("GoodGenerator", "fluidCore", 2, 7))
            .fluidInputs(MaterialPool.ExcitedNaquadahFuel.getFluidOrGas(1000))
            .itemOutputs(GTNLItemList.DepletedExcitedNaquadahFuelRod.get(1))
            .duration(20)
            .metadata(COIL_HEAT, 13501)
            .eut(31457280)
            .addTo(PFR);
    }
}
