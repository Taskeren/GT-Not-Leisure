package com.science.gtnl.api.mixinHelper;

import gregtech.api.recipe.RecipeMap;

public interface IMultiblockRecipeMap {

    default void setRecipeMapName(String recipeMap) {};

    default String getRecipeMapName() {
        return "";
    };

    default void setRecipeMap(RecipeMap<?> recipeMap) {};

    default RecipeMap<?> getRecipeMap() {
        return null;
    };
}
