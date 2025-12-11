package com.science.gtnl.common.recipe.gtnl;

import java.util.Objects;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.utils.recipes.DisassemblerHelper;
import com.science.gtnl.utils.recipes.ReversedRecipeRegistry;

import gregtech.api.util.GTUtility;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

public class ShimmerRecipes implements IRecipePool {

    public static Object2ObjectOpenHashMap<Item, ObjectArrayList<ConversionEntry>> conversionMap = new Object2ObjectOpenHashMap<>();

    @Override
    public void loadRecipes() {
        DisassemblerHelper.loadAssemblerRecipesToDisassembler();
        ReversedRecipeRegistry.registerAllReversedRecipes();
    }

    public static void registerConversion(ItemStack input, ObjectList<ItemStack> outputs) {
        if (input == null || outputs == null || outputs.isEmpty()) return;

        // Filter non-null outputs
        ObjectArrayList<ItemStack> filteredOutputs = outputs.stream()
            .filter(Objects::nonNull)
            .collect(ObjectArrayList::new, ObjectArrayList::add, ObjectArrayList::addAll);

        if (filteredOutputs.isEmpty()) return;

        ConversionEntry entry = new ConversionEntry(input, filteredOutputs);

        conversionMap.computeIfAbsent(input.getItem(), k -> new ObjectArrayList<>())
            .add(entry);
    }

    @Nullable
    public static ObjectList<ItemStack> getConversionResult(ItemStack input) {
        if (input == null || input.stackSize <= 0) return null;
        ObjectList<ConversionEntry> entries = conversionMap.get(input.getItem());
        if (entries == null) return null;

        for (ConversionEntry entry : entries) {
            if (entry.matches(input)) {
                return entry.getScaledOutputs(input.stackSize);
            }
        }

        return null;
    }

    public static boolean isInConversions(ItemStack input) {
        if (input == null) return false;
        ObjectList<ConversionEntry> entries = conversionMap.get(input.getItem());
        if (entries == null) return false;
        for (ConversionEntry entry : entries) {
            if (entry.matches(input)) return true;
        }
        return false;
    }

    public static class ConversionEntry {

        final ItemStack input;
        final ObjectArrayList<ItemStack> outputs;

        public ConversionEntry(ItemStack input, ObjectArrayList<ItemStack> outputs) {
            this.input = input.copy();
            this.outputs = outputs.stream()
                .map(ItemStack::copy)
                .collect(ObjectArrayList::new, ObjectArrayList::add, ObjectArrayList::addAll);
        }

        boolean matches(ItemStack stack) {
            return stack != null && stack.stackSize > 0 && GTUtility.areStacksEqual(stack, input, true);
        }

        public ObjectArrayList<ItemStack> getScaledOutputs(int scale) {
            return outputs.stream()
                .map(out -> {
                    ItemStack scaled = out.copy();
                    scaled.stackSize = scaled.stackSize * scale;
                    return scaled;
                })
                .collect(ObjectArrayList::new, ObjectArrayList::add, ObjectArrayList::addAll);
        }
    }
}
