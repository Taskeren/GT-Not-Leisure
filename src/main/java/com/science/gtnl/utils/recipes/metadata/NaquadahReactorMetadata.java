package com.science.gtnl.utils.recipes.metadata;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.StatCollector;

import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.api.util.MethodsReturnNonnullByDefault;
import gregtech.nei.RecipeDisplayInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class NaquadahReactorMetadata extends RecipeMetadataKey<Integer> {

    public static final NaquadahReactorMetadata INSTANCE = new NaquadahReactorMetadata();

    public NaquadahReactorMetadata() {
        super(Integer.class, "naquadah_reactor_data");
    }

    @Override
    public void drawInfo(RecipeDisplayInfo recipeInfo, @Nullable Object value) {
        int tier = cast(value, 0);
        if (tier == 1) {
            recipeInfo.drawText(StatCollector.translateToLocal("NaquadahReactorMetadata.0"));
        } else if (tier == 2) {
            recipeInfo.drawText(StatCollector.translateToLocal("NaquadahReactorMetadata.1"));
        }
    }
}
