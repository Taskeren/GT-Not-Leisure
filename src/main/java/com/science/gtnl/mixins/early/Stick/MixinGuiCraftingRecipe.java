package com.science.gtnl.mixins.early.Stick;

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
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.ICraftingHandler;

@Mixin(value = GuiCraftingRecipe.class, remap = false)
public abstract class MixinGuiCraftingRecipe extends GuiRecipe<ICraftingHandler> {

    public MixinGuiCraftingRecipe(GuiScreen prevgui) {
        super(prevgui);
    }

    @Inject(
        method = "createRecipeGui",
        at = @At(
            value = "INVOKE",
            target = "Lcodechicken/nei/recipe/GuiCraftingRecipe;getRecipeId(Lnet/minecraft/client/gui/GuiScreen;Lnet/minecraft/item/ItemStack;)Lcodechicken/nei/recipe/Recipe$RecipeId;",
            shift = At.Shift.BEFORE))
    private static void afterAllNormalize(String outputId, boolean open, Object[] results,
        CallbackInfoReturnable<GuiRecipe<?>> cir) {
        for (int i = 0; i < results.length; i++) {
            Object obj = results[i];
            if (!(obj instanceof ItemStack stack)) continue;

            Item item = stack.getItem();
            if (item == null) continue;

            if (item instanceof ICraftingPatternItem pattern) {
                ICraftingPatternDetails details = pattern.getPatternForItem(stack, Minecraft.getMinecraft().theWorld);
                if (details == null) continue;

                ItemStack output = details.getCondensedOutputs()[0].getItemStack();
                results[i] = output;
            }

            if (item instanceof Stick) {
                ItemStack fake = Stick.getDisguisedStack(stack);
                if (fake == null) continue;

                results[i] = fake;
            }
        }
    }
}
