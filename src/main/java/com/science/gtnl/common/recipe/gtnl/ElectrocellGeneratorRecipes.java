package com.science.gtnl.common.recipe.gtnl;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.MaterialPool;
import com.science.gtnl.common.material.RecipePool;
import com.science.gtnl.utils.recipes.RecipeBuilder;
import com.science.gtnl.utils.recipes.metadata.ElectrocellGeneratorMetadata;

import goodgenerator.items.GGMaterial;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gtPlusPlus.core.util.minecraft.ItemUtils;

public class ElectrocellGeneratorRecipes implements IRecipePool {

    public RecipeMap<?> EGR = RecipePool.ElectrocellGeneratorRecipes;
    public ElectrocellGeneratorMetadata GENERATOR_EUT = ElectrocellGeneratorMetadata.INSTANCE;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Zinc, 1),
                ItemUtils.getItemStackOfAmountFromOreDict("dustBerylliumHydroxide", 6))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Zinc, 1))
            .fluidInputs(Materials.Mercury.getFluid(35))
            .fluidOutputs(MaterialPool.ToxicMercurySludge.getFluidOrGas(40))
            .outputChances(10000)
            .eut(0)
            .specialValue(110)
            .metadata(GENERATOR_EUT, 10240L)
            .duration(1000)
            .addTo(EGR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.Aluminium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.SodiumBisulfate, 4))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.SodiumSulfide, 24))
            .fluidInputs(Materials.Sodium.getFluid(400))
            .fluidOutputs(Materials.Hydrogen.getGas(120))
            .outputChances(10000)
            .eut(0)
            .specialValue(100)
            .metadata(GENERATOR_EUT, 8192L)
            .duration(600)
            .addTo(EGR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.TungstenSteel, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.SodiumBisulfate, 6))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.SodiumSulfide, 30))
            .fluidInputs(Materials.Sodium.getFluid(400))
            .fluidOutputs(Materials.Hydrogen.getGas(100))
            .outputChances(8000)
            .eut(0)
            .specialValue(110)
            .metadata(GENERATOR_EUT, 14400L)
            .duration(1000)
            .addTo(EGR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.TungstenSteel, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.SodiumHydroxide, 7))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Thorium, 5))
            .fluidInputs(GGMaterial.thoriumNitrate.getFluidOrGas(150))
            .fluidOutputs(Materials.NitricAcid.getFluid(600))
            .outputChances(8000)
            .eut(0)
            .specialValue(110)
            .metadata(GENERATOR_EUT, 20480L)
            .duration(1400)
            .addTo(EGR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.TungstenSteel, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.MeteoricIron, 5))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sunnarium, 1))
            .fluidInputs(MaterialPool.GlowThorium.getFluidOrGas(100))
            .fluidOutputs(Materials.NitricAcid.getFluid(200))
            .outputChances(10000)
            .eut(0)
            .specialValue(110)
            .metadata(GENERATOR_EUT, 30720L)
            .duration(1400)
            .addTo(EGR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.TungstenSteel, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 3))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustSmall, Materials.Uranium235, 1))
            .fluidInputs(MaterialPool.UraniumFuel.getFluidOrGas(8))
            .fluidOutputs(MaterialPool.UraniumWaste.getFluidOrGas(8))
            .outputChances(8000)
            .eut(0)
            .specialValue(110)
            .metadata(GENERATOR_EUT, 49152L)
            .duration(900)
            .addTo(EGR);
    }
}
