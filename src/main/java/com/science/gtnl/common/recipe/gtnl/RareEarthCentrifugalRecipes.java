package com.science.gtnl.common.recipe.gtnl;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;

public class RareEarthCentrifugalRecipes implements IRecipePool {

    public RecipeMap<?> RECR = GTNLRecipeMaps.RareEarthCentrifugalRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(GTNLMaterials.RareEarthMetal.get(OrePrefixes.dust, 1))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lanthanum, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cerium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Neodymium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Promethium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Samarium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Praseodymium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gadolinium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Terbium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Dysprosium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Holmium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Erbium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Thulium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Ytterbium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Scandium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lutetium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Yttrium, 1))
            .outputChances(
                7000,
                7000,
                7000,
                7000,
                7000,
                7000,
                7000,
                7000,
                7000,
                7000,
                7000,
                7000,
                7000,
                7000,
                7000,
                7000,
                7000)
            .duration(100)
            .eut(TierEU.RECIPE_IV)
            .addTo(RECR);
    }
}
