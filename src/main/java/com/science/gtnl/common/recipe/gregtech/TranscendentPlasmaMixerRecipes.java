package com.science.gtnl.common.recipe.gregtech;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.MaterialPool;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import goodgenerator.items.GGMaterial;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTUtility;

public class TranscendentPlasmaMixerRecipes implements IRecipePool {

    public RecipeMap<?> TPMP = RecipeMaps.transcendentPlasmaMixerRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(6))
            .fluidInputs(
                MaterialsUEVplus.Time.getMolten(710),
                MaterialsUEVplus.Space.getMolten(710),
                GGMaterial.naquadahBasedFuelMkI.getFluidOrGas(1000),
                GGMaterial.naquadahBasedFuelMkII.getFluidOrGas(1000),
                GGMaterial.naquadahBasedFuelMkIII.getFluidOrGas(1000),
                GGMaterial.naquadahBasedFuelMkIV.getFluidOrGas(1000),
                GGMaterial.naquadahBasedFuelMkV.getFluidOrGas(1000),
                GGMaterial.naquadahBasedFuelMkVI.getFluidOrGas(1000),
                Materials.LiquidAir.getFluid(85200))
            .fluidOutputs(MaterialPool.ExcitedNaquadahFuel.getFluidOrGas(1000))
            .duration(20)
            .eut(TierEU.RECIPE_UXV)
            .addTo(TPMP);
    }
}
