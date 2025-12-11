package com.science.gtnl.utils.recipes;

import java.util.Optional;

import net.minecraft.item.ItemStack;

import com.science.gtnl.ScienceNotLeisure;

import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

public class ReversedRecipeRegistry {

    public static ObjectList<GTCraftingRecipe> REGISTRY = new ObjectArrayList<>();

    public interface GTCraftingRecipe {

        Object[] getInputs();

        ItemStack getOutput();

        Throwable getAdderStackTrace();

        Optional<GTRecipe> toReversed();

        default GTRecipe toReversedSafe() {
            try {
                Optional<GTRecipe> r = toReversed();
                if (r.isPresent()) {
                    return r.get();
                } else {
                    ScienceNotLeisure.LOG.warn("Invalid reversed recipe found during reversing recipe");
                }
            } catch (IllegalStateException ignored) {
                ScienceNotLeisure.LOG.warn("Invalid recipe found during reversing recipe");
            }

            return null;
        }
    }

    public static class Shaped implements GTCraftingRecipe {

        private final Object[] inputs;
        private final ItemStack output;
        private final Throwable adderStackTrace;

        public Shaped(Object[] inputs, ItemStack output) {
            this(inputs, output, new Exception());
        }

        public Shaped(Object[] inputs, ItemStack output, Throwable adderStackTrace) {
            this.inputs = inputs;
            this.output = output;
            this.adderStackTrace = adderStackTrace;
        }

        @Override
        public Object[] getInputs() {
            return inputs;
        }

        @Override
        public ItemStack getOutput() {
            return output;
        }

        @Override
        public Throwable getAdderStackTrace() {
            return adderStackTrace;
        }

        @Override
        public Optional<GTRecipe> toReversed() {
            return GTUtility.reverseShapedRecipe(output, inputs);
        }
    }

    public static class Shapeless implements GTCraftingRecipe {

        private final Object[] inputs;
        private final ItemStack output;
        private final Throwable adderStackTrace;

        public Shapeless(Object[] inputs, ItemStack output) {
            this(inputs, output, new Exception());
        }

        public Shapeless(Object[] inputs, ItemStack output, Throwable adderStackTrace) {
            this.inputs = inputs;
            this.output = output;
            this.adderStackTrace = adderStackTrace;
        }

        @Override
        public Object[] getInputs() {
            return inputs;
        }

        @Override
        public ItemStack getOutput() {
            return output;
        }

        @Override
        public Throwable getAdderStackTrace() {
            return adderStackTrace;
        }

        @Override
        public Optional<GTRecipe> toReversed() {
            return GTUtility.reverseShapelessRecipe(output, inputs);
        }
    }

    public static void registerShaped(ItemStack output, Object[] recipe) {
        REGISTRY.add(new Shaped(recipe, output));
    }

    public static void registerShapeless(ItemStack output, Object[] recipe) {
        REGISTRY.add(new Shapeless(recipe, output));
    }

    public static void registerAllReversedRecipes() {
        for (GTCraftingRecipe recipe : REGISTRY) {
            DisassemblerHelper.handleGTCraftingRecipe(recipe);
        }
    }
}
