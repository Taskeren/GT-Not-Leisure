package com.science.gtnl.mixins.early.Stick;

import static codechicken.nei.recipe.GuiUsageRecipe.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.common.item.items.Stick;

import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import codechicken.nei.recipe.IUsageHandler;

@Mixin(value = GuiUsageRecipe.class, remap = false)
public abstract class MixinGuiUsageRecipe extends GuiRecipe<IUsageHandler> {

    public MixinGuiUsageRecipe(GuiScreen prevgui) {
        super(prevgui);
    }

    @Inject(
        method = "openRecipeGui",
        at = @At(
            value = "INVOKE",
            target = "Lcodechicken/nei/recipe/GuiUsageRecipe;getUsageHandlers(Ljava/lang/String;[Ljava/lang/Object;)Ljava/util/ArrayList;",
            shift = At.Shift.BEFORE))
    private static void afterNormalizeItemStack(String inputId, Object[] ingredients,
        CallbackInfoReturnable<Boolean> cir) {
        for (int i = 0; i < ingredients.length; i++) {
            Object obj = ingredients[i];
            if (!(obj instanceof ItemStack stack)) continue;

            Item item = stack.getItem();
            if (item == null) continue;

            if (item instanceof ICraftingPatternItem pattern) {
                ICraftingPatternDetails details = pattern.getPatternForItem(stack, Minecraft.getMinecraft().theWorld);
                if (details == null) continue;

                ItemStack output = details.getCondensedOutputs()[0].getItemStack();
                ingredients[i] = output;
            }

            if (item instanceof Stick) {
                ItemStack fake = Stick.getDisguisedStack(stack);
                if (fake == null) continue;

                ingredients[i] = fake;
            }
        }
    }
}
