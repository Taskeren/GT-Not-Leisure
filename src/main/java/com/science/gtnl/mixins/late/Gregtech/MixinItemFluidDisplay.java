package com.science.gtnl.mixins.late.Gregtech;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

import org.spongepowered.asm.mixin.Mixin;

import gregtech.common.items.ItemFluidDisplay;

@Deprecated
@Mixin(value = ItemFluidDisplay.class, remap = false)
public abstract class MixinItemFluidDisplay implements IFluidContainerItem {

    @Override
    public FluidStack getFluid(ItemStack aStack) {
        if (aStack == null) return null;

        Fluid fluid = FluidRegistry.getFluid(aStack.getItemDamage());
        if (fluid == null) return null;

        NBTTagCompound tag = aStack.getTagCompound();
        if (tag == null) return new FluidStack(fluid, 0);

        long amount = tag.getLong("mFluidDisplayAmount");
        return new FluidStack(fluid, (int) Math.min(amount, Integer.MAX_VALUE));
    }

    @Override
    public int getCapacity(ItemStack container) {
        return Integer.MAX_VALUE;
    }

    @Override
    public int fill(ItemStack container, FluidStack resource, boolean doFill) {
        if (resource == null || resource.amount <= 0) return 0;

        Fluid existingFluid = FluidRegistry.getFluid(container.getItemDamage());
        if (existingFluid == null || !existingFluid.getName()
            .equals(
                resource.getFluid()
                    .getName())) {
            return 0;
        }

        if (!doFill) {
            return resource.amount;
        }

        if (container.stackTagCompound == null) {
            container.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound tag = container.getTagCompound();
        long currentAmount = tag.getLong("mFluidDisplayAmount");
        long newAmount = Math.min(Integer.MAX_VALUE, currentAmount + resource.amount);

        tag.setLong("mFluidDisplayAmount", newAmount);
        tag.setLong(
            "mFluidDisplayHeat",
            resource.getFluid()
                .getTemperature());
        tag.setBoolean(
            "mFluidState",
            resource.getFluid()
                .isGaseous());

        return (int) (newAmount - currentAmount);
    }

    @Override
    public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
        Fluid fluid = FluidRegistry.getFluid(container.getItemDamage());
        if (fluid == null || maxDrain <= 0) return null;

        NBTTagCompound tag = container.getTagCompound();
        if (tag == null) return null;

        long currentAmount = tag.getLong("mFluidDisplayAmount");
        if (currentAmount <= 0) return null;

        int drainedAmount = (int) Math.min(maxDrain, currentAmount);
        FluidStack drained = new FluidStack(fluid, drainedAmount);

        if (doDrain) {
            long newAmount = currentAmount - drainedAmount;
            tag.setLong("mFluidDisplayAmount", newAmount);
            if (newAmount <= 0) {
                tag.removeTag("mFluidDisplayHeat");
                tag.removeTag("mFluidState");
            }
        }

        return drained;
    }
}
