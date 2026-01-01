package com.science.gtnl.mixins.late.NotEnoughEnergistics;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.glodblock.github.nei.FluidPatternTerminalRecipeTransferHandler;
import com.glodblock.github.nei.object.OrderStack;
import com.glodblock.github.nei.recipes.FluidRecipe;

import codechicken.nei.recipe.IRecipeHandler;
import gregtech.api.util.GTOreDictUnificator;

@Mixin(value = FluidPatternTerminalRecipeTransferHandler.class, remap = false)
public abstract class MixinFluidPatternTerminalRecipeTransferHandler {

    @Shadow
    protected abstract boolean notUseOther(IRecipeHandler recipeHandler);

    @SuppressWarnings("unchecked")
    @Redirect(
        method = "overlayRecipe",
        at = @At(
            value = "INVOKE",
            target = "Lcom/glodblock/github/nei/recipes/FluidRecipe; getPackageOutputs(Lcodechicken/nei/recipe/IRecipeHandler;IZ) Ljava/util/List;"))
    private List<OrderStack<?>> gtnl$redirectGetPackageOutputs(IRecipeHandler recipe, int recipeIndex,
        boolean useOther) {
        List<OrderStack<?>> out = FluidRecipe.getPackageOutputs(recipe, recipeIndex, !notUseOther(recipe));
        if (out == null) return null;

        for (OrderStack<?> orderStack : out) {
            if (orderStack == null) continue;
            if (!(orderStack.getStack() instanceof ItemStack stack)) continue;

            int[] oreIDs = OreDictionary.getOreIDs(stack);
            for (int oreId : oreIDs) {
                String oreName = OreDictionary.getOreName(oreId);
                if (oreName == null || !oreName.startsWith("ingotHot")) continue;

                String cooledOreName = "ingot" + oreName.substring("ingotHot".length());
                List<ItemStack> cooledStacks = OreDictionary.getOres(cooledOreName);
                if (cooledStacks == null || cooledStacks.isEmpty()) continue;

                ItemStack replacement = cooledStacks.get(0);
                if (replacement == null) continue;

                ItemStack result = GTOreDictUnificator.setStack(replacement.copy());
                result.stackSize = stack.stackSize;

                if (stack.stackTagCompound != null) {
                    result.stackTagCompound = (NBTTagCompound) stack.stackTagCompound.copy();
                }

                ((OrderStack<ItemStack>) orderStack).putStack(result);
                break;
            }
        }

        return out;
    }
}
