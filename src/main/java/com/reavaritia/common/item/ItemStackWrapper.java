package com.reavaritia.common.item;

import java.util.Objects;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import com.github.bsideup.jabel.Desugar;

@Desugar
public record ItemStackWrapper(ItemStack stack) {

    @Override
    public boolean equals(Object otherobj) {
        if (!(otherobj instanceof ItemStackWrapper other)) return false;

        if (!Objects.equals(this.stack.getItem(), other.stack.getItem())) return false;
        if (this.stack.getItemDamage() != other.stack.getItemDamage()) return false;

        if (this.stack.stackTagCompound == null && other.stack.stackTagCompound == null) return true;
        if (this.stack.stackTagCompound == null || other.stack.stackTagCompound == null) return false;

        return this.stack.stackTagCompound.equals(other.stack.stackTagCompound);
    }

    @Override
    public int hashCode() {
        int h = Objects.requireNonNull(this.stack.getItem())
            .hashCode();
        h = 31 * h + this.stack.getItemDamage();
        if (this.stack.stackTagCompound != null) {
            h = 31 * h + this.stack.stackTagCompound.hashCode();
        }
        return h;
    }

    @Override
    public @NotNull String toString() {
        return this.stack.toString();
    }
}
