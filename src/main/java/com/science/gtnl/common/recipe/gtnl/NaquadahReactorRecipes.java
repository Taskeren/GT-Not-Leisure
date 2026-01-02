package com.science.gtnl.common.recipe.gtnl;

import org.apache.commons.lang3.tuple.Pair;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.RecipePool;
import com.science.gtnl.utils.recipes.RecipeBuilder;
import com.science.gtnl.utils.recipes.metadata.NaquadahReactorMetadata;

import goodgenerator.items.GGMaterial;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
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
            .metadata(REACTOR_TIER, Pair.of(0, TierEU.RECIPE_UV))
            .addTo(NRR);

        RecipeBuilder.builder()
            .fluidInputs(GGMaterial.naquadahBasedFuelMkI.getFluidOrGas(100), Materials.Oxygen.getPlasma(40))
            .fluidOutputs(GGMaterial.naquadahBasedFuelMkIDepleted.getFluidOrGas(100))
            .duration(10000)
            .eut(0)
            .metadata(REACTOR_TIER, Pair.of(0, TierEU.RECIPE_UEV))
            .addTo(NRR);

        RecipeBuilder.builder()
            .fluidInputs(GGMaterial.naquadahBasedFuelMkII.getFluidOrGas(16), Materials.Hydrogen.getGas(1600))
            .fluidOutputs(GGMaterial.naquadahBasedFuelMkIIDepleted.getFluidOrGas(16))
            .duration(1250)
            .eut(0)
            .metadata(REACTOR_TIER, Pair.of(0, TierEU.RECIPE_UEV))
            .addTo(NRR);

        RecipeBuilder.builder()
            .fluidInputs(GGMaterial.naquadahBasedFuelMkII.getFluidOrGas(125), Materials.Nitrogen.getPlasma(80))
            .fluidOutputs(GGMaterial.naquadahBasedFuelMkIIDepleted.getFluidOrGas(125))
            .duration(12500)
            .eut(0)
            .metadata(REACTOR_TIER, Pair.of(0, TierEU.RECIPE_UMV))
            .addTo(NRR);

        RecipeBuilder.builder()
            .fluidInputs(GGMaterial.naquadahBasedFuelMkIII.getFluidOrGas(16), Materials.Iron.getPlasma(40))
            .fluidOutputs(GGMaterial.naquadahBasedFuelMkIIIDepleted.getFluidOrGas(16))
            .duration(2000)
            .eut(0)
            .metadata(REACTOR_TIER, Pair.of(1, TierEU.RECIPE_UMV))
            .addTo(NRR);

        RecipeBuilder.builder()
            .fluidInputs(GGMaterial.naquadahBasedFuelMkIII.getFluidOrGas(125), Materials.Radon.getPlasma(100))
            .fluidOutputs(GGMaterial.naquadahBasedFuelMkIIIDepleted.getFluidOrGas(125))
            .duration(20000)
            .eut(0)
            .metadata(REACTOR_TIER, Pair.of(1, TierEU.RECIPE_UXV))
            .addTo(NRR);

    }
}
