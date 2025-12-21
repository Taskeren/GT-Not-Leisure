package com.science.gtnl.utils;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import com.science.gtnl.utils.gui.ContainerDirePatternEncoder;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import appeng.util.item.ItemList;
import lombok.Getter;

public class DireCraftingPatternDetails implements ICraftingPatternDetails {

    private final ItemStack pattern;
    private final IAEItemStack[] inputs;
    private IAEItemStack[] condensedInputs;
    private final IAEItemStack[] output;
    private final long baseOutput;
    @Getter
    private int multiply = 1;

    public DireCraftingPatternDetails(ItemStack is) {
        pattern = is;

        if (is.hasTagCompound()) {
            inputs = new IAEItemStack[81];
            output = new IAEItemStack[1];
            var list = new ItemList();
            var tag = is.getTagCompound();
            var in = tag.getTagList("in", Constants.NBT.TAG_COMPOUND);
            for (var i = 0; i < in.tagCount(); i++) {
                var item = fromTagCreateAEItem(in.getCompoundTagAt(i));
                inputs[i] = item;
                list.addStorage(item);
            }
            output[0] = fromTagCreateAEItem(tag.getCompoundTag("out"));

            this.condensedInputs = list.toArray(new IAEItemStack[0]);
            this.baseOutput = output[0].getStackSize();
        } else {
            throw new IllegalArgumentException("No pattern here!");
        }
    }

    public DireCraftingPatternDetails(ICraftingPatternDetails is) {
        pattern = is.getPattern()
            .copy();
        inputs = is.getInputs()
            .clone();
        var list = new ItemList();
        for (var i = 0; i < inputs.length; i++) {
            var ii = inputs[i];
            if (ii == null) continue;
            inputs[i] = ii.copy();
            list.addStorage(ii);
        }
        condensedInputs = list.toArray(new IAEItemStack[0]);
        output = is.getCondensedOutputs()
            .clone();
        for (var i = 0; i < output.length; i++) {
            output[i] = output[i].copy();
        }
        this.baseOutput = output[0].getStackSize();
    }

    public void setMultiply(int multiply) {
        this.multiply = multiply;
        for (var input : inputs) {
            if (input == null) continue;
            input.setStackSize(multiply);
        }
        var list = new ItemList();
        for (var i = 0; i < inputs.length; i++) {
            var ii = inputs[i];
            if (ii == null) continue;
            inputs[i] = ii.copy();
            list.addStorage(ii);
        }
        condensedInputs = list.toArray(new IAEItemStack[0]);
        output[0].setStackSize(baseOutput * multiply);
    }

    private static AEItemStack fromTagCreateAEItem(final NBTTagCompound i) {
        if (ContainerDirePatternEncoder.empty.equals(i)) return null;
        return AEItemStack.create(Platform.loadItemStackFromNBT(i));
    }

    @Override
    public ItemStack getPattern() {
        return pattern;
    }

    @Override
    public boolean isValidItemForSlot(int slotIndex, ItemStack itemStack, World world) {
        return false;
    }

    @Override
    public boolean isCraftable() {
        return false;
    }

    @Override
    public IAEItemStack[] getInputs() {
        return inputs;
    }

    @Override
    public IAEItemStack[] getCondensedInputs() {
        return condensedInputs;
    }

    @Override
    public IAEItemStack[] getCondensedOutputs() {
        return output;
    }

    @Override
    public IAEItemStack[] getOutputs() {
        return output;
    }

    @Override
    public boolean canSubstitute() {
        return false;
    }

    @Override
    public ItemStack getOutput(InventoryCrafting craftingInv, World world) {
        return null;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void setPriority(int priority) {

    }
}
