package com.science.gtnl.common.recipe.gtnl;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.RecipePool;
import com.science.gtnl.utils.recipes.RecipeBuilder;
import com.science.gtnl.utils.recipes.metadata.NaquadahReactorMetadata;

import goodgenerator.items.GGMaterial;
import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMap;

public class NaquadahReactorRecipes implements IRecipePool {

    public NaquadahReactorMetadata REACTOR_TIER = NaquadahReactorMetadata.INSTANCE;
    public RecipeMap<?> NRR = RecipePool.NaquadahReactorRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .fluidInputs(GGMaterial.naquadahBasedFuelMkI.getFluidOrGas(16), Materials.Hydrogen.getGas(1600))
            .fluidOutputs(GGMaterial.naquadahBasedFuelMkIDepleted.getFluidOrGas(16))
            .duration(1000)
            .eut(0)
            .specialValue(524288)
            .addTo(NRR);

        RecipeBuilder.builder()
            .fluidInputs(GGMaterial.naquadahBasedFuelMkI.getFluidOrGas(100), Materials.Oxygen.getPlasma(72))
            .fluidOutputs(GGMaterial.naquadahBasedFuelMkIDepleted.getFluidOrGas(100))
            .duration(20000)
            .eut(0)
            .specialValue(524288)
            .addTo(NRR);

        RecipeBuilder.builder()
            .fluidInputs(GGMaterial.naquadahBasedFuelMkII.getFluidOrGas(16), Materials.Hydrogen.getGas(1600))
            .fluidOutputs(GGMaterial.naquadahBasedFuelMkIIDepleted.getFluidOrGas(16))
            .duration(1250)
            .eut(0)
            .specialValue(524288)
            .addTo(NRR);

        RecipeBuilder.builder()
            .fluidInputs(GGMaterial.naquadahBasedFuelMkII.getFluidOrGas(125), Materials.Nitrogen.getPlasma(120))
            .fluidOutputs(GGMaterial.naquadahBasedFuelMkIIDepleted.getFluidOrGas(125))
            .duration(30000)
            .eut(0)
            .specialValue(524288)
            .addTo(NRR);

    }
}
