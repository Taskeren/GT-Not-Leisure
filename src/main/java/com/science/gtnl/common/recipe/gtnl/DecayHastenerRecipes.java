package com.science.gtnl.common.recipe.gtnl;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.MaterialPool;
import com.science.gtnl.common.material.RecipePool;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import bartworks.system.material.WerkstoffLoader;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gtPlusPlus.core.material.MaterialsElements;
import gtPlusPlus.core.util.minecraft.ItemUtils;
import gtnhlanth.common.register.WerkstoffMaterialPool;

public class DecayHastenerRecipes implements IRecipePool {

    public RecipeMap<?> DHR = RecipePool.DecayHastenerRecipes;

    @Override
    public void loadRecipes() {

        // 铀系衰变链
        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uranium, 1))
            .itemOutputs(WerkstoffMaterialPool.Thorium234.get(OrePrefixes.dust, 1))
            .duration(1000)
            .eut(TierEU.RECIPE_EV)
            .addTo(DHR);

        RecipeBuilder.builder()
            .itemInputs(WerkstoffMaterialPool.Thorium234.get(OrePrefixes.dust, 1))
            .itemOutputs(MaterialsElements.getInstance().PROTACTINIUM.getDust(1))
            .duration(1000)
            .eut(TierEU.RECIPE_EV)
            .addTo(DHR);

        RecipeBuilder.builder()
            .itemInputs(MaterialsElements.getInstance().PROTACTINIUM.getDust(1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Thorium, 1))
            .duration(1000)
            .eut(TierEU.RECIPE_EV)
            .addTo(DHR);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Thorium, 1))
            .itemOutputs(ItemUtils.getItemStackOfAmountFromOreDict("dustRadium226", 1))
            .duration(1000)
            .eut(TierEU.RECIPE_EV)
            .addTo(DHR);

        RecipeBuilder.builder()
            .itemInputs(ItemUtils.getItemStackOfAmountFromOreDict("dustRadium226", 1))
            .fluidOutputs(Materials.Radon.getGas(144))
            .duration(1000)
            .eut(TierEU.RECIPE_EV)
            .addTo(DHR);

        RecipeBuilder.builder()
            .fluidInputs(Materials.Radon.getGas(144))
            .itemOutputs(MaterialsElements.getInstance().POLONIUM.getDust(1))
            .duration(1000)
            .eut(TierEU.RECIPE_EV)
            .addTo(DHR);

        RecipeBuilder.builder()
            .itemInputs(MaterialsElements.getInstance().POLONIUM.getDust(1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bismuth, 1))
            .duration(1000)
            .eut(TierEU.RECIPE_EV)
            .addTo(DHR);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bismuth, 1))
            .itemOutputs(MaterialsElements.getInstance().THALLIUM.getDust(1))
            .duration(2000)
            .eut(TierEU.RECIPE_EV)
            .addTo(DHR);

        RecipeBuilder.builder()
            .itemInputs(MaterialsElements.getInstance().THALLIUM.getDust(1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lead, 1))
            .duration(200)
            .eut(TierEU.RECIPE_MV)
            .addTo(DHR);

        // 钍系衰变链
        RecipeBuilder.builder()
            .itemInputs(WerkstoffLoader.Thorium232.get(OrePrefixes.dust, 1))
            .itemOutputs(MaterialsElements.getInstance().RADIUM.getDust(1))
            .duration(2000)
            .eut(TierEU.RECIPE_HV)
            .addTo(DHR);

        RecipeBuilder.builder()
            .itemInputs(MaterialsElements.getInstance().RADIUM.getDust(1))
            .itemOutputs(MaterialPool.Actinium.get(OrePrefixes.dust, 1))
            .duration(2000)
            .eut(TierEU.RECIPE_HV)
            .addTo(DHR);

        RecipeBuilder.builder()
            .itemInputs(MaterialPool.Actinium.get(OrePrefixes.dust, 1))
            .fluidOutputs(Materials.Radon.getGas(144))
            .duration(2000)
            .eut(TierEU.RECIPE_HV)
            .addTo(DHR);

        // 镎系衰变链
        RecipeBuilder.builder()
            .itemInputs(MaterialsElements.getInstance().NEPTUNIUM.getDust(1))
            .itemOutputs(MaterialsElements.getInstance().URANIUM233.getDust(1))
            .duration(2000)
            .eut(TierEU.RECIPE_MV)
            .addTo(DHR);

        RecipeBuilder.builder()
            .itemInputs(MaterialsElements.getInstance().URANIUM233.getDust(1))
            .itemOutputs(MaterialPool.Actinium.get(OrePrefixes.dust, 1))
            .duration(2000)
            .eut(TierEU.RECIPE_HV)
            .addTo(DHR);

        RecipeBuilder.builder()
            .itemInputs(MaterialPool.Actinium.get(OrePrefixes.dust, 1))
            .itemOutputs(MaterialPool.Francium.get(OrePrefixes.dust, 1))
            .duration(200)
            .eut(TierEU.RECIPE_MV)
            .addTo(DHR);

        RecipeBuilder.builder()
            .itemInputs(MaterialPool.Francium.get(OrePrefixes.dust, 1))
            .itemOutputs(MaterialPool.Astatine.get(OrePrefixes.dust, 1))
            .duration(20)
            .eut(TierEU.RECIPE_LV)
            .addTo(DHR);

        RecipeBuilder.builder()
            .itemInputs(MaterialPool.Astatine.get(OrePrefixes.dust, 1))
            .itemOutputs(MaterialsElements.getInstance().POLONIUM.getDust(1))
            .duration(2000)
            .eut(TierEU.RECIPE_MV)
            .addTo(DHR);

    }
}
