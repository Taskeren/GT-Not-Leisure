package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.util.GTRecipeBuilder.*;
import static gregtech.api.util.GTRecipeConstants.*;
import static gregtech.api.util.GTRecipeConstants.QFT_FOCUS_TIER;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.material.MaterialsElements;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;

public class QuantumForceTransformerRecipes implements IRecipePool {

    public RecipeMap<?> QFT = GTPPRecipeMaps.quantumForceTransformerRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(Materials.RareEarth.getDust(16))
            .itemOutputs(
                Materials.Cerium.getDust(64),
                Materials.Gadolinium.getDust(64),
                Materials.Samarium.getDust(64),
                MaterialsElements.getInstance().HAFNIUM.getDust(64),
                MaterialsElements.getInstance().ZIRCONIUM.getDust(64),
                ItemList.SuperconductorComposite.get(1))
            .duration(20 * SECONDS)
            .eut(TierEU.RECIPE_UHV)
            .metadata(QFT_CATALYST, GregtechItemList.RareEarthGroupCatalyst.get(0))
            .metadata(QFT_FOCUS_TIER, 2)
            .addTo(QFT);
    }
}
