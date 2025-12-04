package com.reavaritia.common.block.extremeAnvil;

import java.util.Objects;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import org.apache.commons.lang3.StringUtils;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

public class ContainerExtremeAnvil extends Container {

    public World world;
    public int anvilX;
    public int anvilY;
    public int anvilZ;
    public EntityPlayer player;

    public IInventory inputSlots;
    public IInventory outputSlot;

    public String repairedItemName = "";

    public int canTake = 0;

    public ContainerExtremeAnvil(InventoryPlayer playerInventory, World world, int x, int y, int z,
        IInventory inputSlots, IInventory outputSlot, EntityPlayer player) {

        this.inputSlots = inputSlots;
        this.outputSlot = outputSlot;
        this.world = world;
        this.anvilX = x;
        this.anvilY = y;
        this.anvilZ = z;
        this.player = player;

        this.addSlotToContainer(new Slot(this.inputSlots, 0, 27, 47));
        this.addSlotToContainer(new Slot(this.inputSlots, 1, 76, 47));
        this.addSlotToContainer(new Slot(this.outputSlot, 2, 134, 47) {

            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }

            @Override
            public boolean canTakeStack(EntityPlayer player) {
                return this.getHasStack();
            }

            @Override
            public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
                if (!repairedItemName.isEmpty() && !repairedItemName.equals(stack.getDisplayName())) {
                    stack.setStackDisplayName(repairedItemName);
                }
                repairedItemName = "";

                ItemStack input2 = ContainerExtremeAnvil.this.inputSlots.getStackInSlot(1);
                if (input2 != null) {
                    input2.stackSize--;
                    if (input2.stackSize <= 0) {
                        input2 = null;
                    }
                }

                ContainerExtremeAnvil.this.inputSlots.setInventorySlotContents(0, null);
                ContainerExtremeAnvil.this.inputSlots.setInventorySlotContents(1, input2);

                if (!world.isRemote) {
                    world.playAuxSFX(1021, anvilX, anvilY, anvilZ, 0);
                }
            }
        });

        int i;
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public void addCraftingToCrafters(ICrafting crafter) {
        if (this.crafters.contains(crafter)) {
            return;
        }
        this.crafters.add(crafter);
        crafter.sendProgressBarUpdate(this, 0, this.canTake);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (ICrafting icrafting : this.crafters) {
            icrafting.sendProgressBarUpdate(this, 0, this.canTake);
        }
    }

    public String getItemName() {
        return this.repairedItemName;
    }

    public void updateItemName(String name) {
        this.repairedItemName = name;
        if (this.getSlot(2)
            .getHasStack()) {
            ItemStack itemstack = this.getSlot(2)
                .getStack();

            if (StringUtils.isBlank(name)) {
                itemstack.func_135074_t();
            } else if (!this.repairedItemName.equals(itemstack.getDisplayName())) {
                itemstack.setStackDisplayName(this.repairedItemName);
            }
        }
        this.updateRepairOutput();
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventory) {
        super.onCraftMatrixChanged(inventory);
        if (inventory == this.inputSlots) {
            this.updateRepairOutput();
        }
    }

    public void updateRepairOutput() {
        ItemStack input = inputSlots.getStackInSlot(0);
        ItemStack material = inputSlots.getStackInSlot(1);
        ItemStack resultStack = null;

        if (input != null) {
            if (material != null) {
                if (input.getItem() instanceof ItemEnchantedBook && material.getItem() instanceof ItemEnchantedBook) {
                    resultStack = new ItemStack(Items.enchanted_book);
                    resultStack = this.applyMergedEnchantments(resultStack, input, material);
                } else if (material.getItem() instanceof ItemEnchantedBook) {
                    resultStack = input.copy();
                    resultStack = this.applyMergedEnchantments(resultStack, input, material);
                } else if (Objects.requireNonNull(input.getItem())
                    .getIsRepairable(input, material)) {
                        resultStack = input.copy();
                        resultStack.setItemDamage(0);
                    } else if (input.getItem() == material.getItem()) {
                        if (input.getItem()
                            .isDamageable()) {
                            resultStack = input.copy();
                            int inputDurability = input.getMaxDamage() - input.getItemDamage();
                            int materialDurability = material.getMaxDamage() - material.getItemDamage();
                            int repairedDurabilityAdded = (int) (materialDurability * 1.1);
                            int totalRepairedDurability = inputDurability + repairedDurabilityAdded;
                            int newDamage = Math.max(0, resultStack.getMaxDamage() - totalRepairedDurability);
                            resultStack.setItemDamage(newDamage);
                            resultStack = this.applyMergedEnchantments(resultStack, input, material);
                        } else {
                            outputSlot.setInventorySlotContents(2, null);
                            this.canTake = 0;
                            detectAndSendChanges();
                            return;
                        }
                    } else {
                        outputSlot.setInventorySlotContents(2, null);
                        this.canTake = 0;
                        detectAndSendChanges();
                        return;
                    }
            } else {
                if (input.getDisplayName() != null) {
                    resultStack = input.copy();
                }
            }
        } else {
            outputSlot.setInventorySlotContents(2, null);
        }

        if (resultStack != null) {
            if (!this.repairedItemName.isEmpty() && !this.repairedItemName.equals(resultStack.getDisplayName())) {
                resultStack.setStackDisplayName(this.repairedItemName);
            }
        }

        outputSlot.setInventorySlotContents(2, resultStack);

        this.canTake = 1;
        detectAndSendChanges();
    }

    public ItemStack applyMergedEnchantments(ItemStack baseStack, ItemStack inputStack, ItemStack materialStack) {
        if (baseStack == null) return null;

        Int2IntMap enchantments = new Int2IntOpenHashMap();
        mergeEnchantments(enchantments, inputStack);
        mergeEnchantments(enchantments, materialStack);

        NBTTagList enchList = new NBTTagList();
        enchantments.forEach((id, lvl) -> {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("id", id);
            tag.setInteger("lvl", lvl);
            enchList.appendTag(tag);
        });

        NBTTagCompound finalTag;

        if (baseStack.getItem() instanceof ItemEnchantedBook) {
            finalTag = new NBTTagCompound();
            if (baseStack.hasTagCompound()) {
                NBTTagCompound baseTagCopy = baseStack.getTagCompound();
                for (String key : baseTagCopy.func_150296_c()) {
                    if (!key.equals("StoredEnchantments") && !key.equals("RepairCost")) {
                        finalTag.setTag(
                            key,
                            baseTagCopy.getTag(key)
                                .copy());
                    }
                }
            }

            if (enchList.tagCount() > 0) {
                finalTag.setTag("StoredEnchantments", enchList);
            }

        } else {
            if (baseStack.hasTagCompound()) {
                finalTag = baseStack.getTagCompound();
                finalTag.removeTag("ench");
                finalTag.removeTag("RepairCost");
            } else {
                finalTag = new NBTTagCompound();
            }

            if (enchList.tagCount() > 0) {
                finalTag.setTag("ench", enchList);
            }
        }

        if (!finalTag.func_150296_c()
            .isEmpty()) {
            baseStack.setTagCompound(finalTag);
        } else {
            baseStack.setTagCompound(null);
        }

        return baseStack;
    }

    public void mergeEnchantments(Int2IntMap map, ItemStack stack) {
        if (stack == null) return;

        NBTTagList list = null;
        if (stack.isItemEnchanted()) {
            list = stack.getEnchantmentTagList();
        } else if (stack.getItem() instanceof ItemEnchantedBook) {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag != null && tag.hasKey("StoredEnchantments")) {
                list = tag.getTagList("StoredEnchantments", 10);
            }
        }

        if (list != null) {
            for (int i = 0; i < list.tagCount(); ++i) {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                int id = tag.getInteger("id");
                int lvl = tag.getInteger("lvl");

                Enchantment ench = Enchantment.enchantmentsList[id];
                if (ench != null) {
                    map.put(id, map.getOrDefault(id, 0) + lvl);
                }
            }
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = null;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack stackInSlot = slot.getStack();
            stack = stackInSlot.copy();
            if (index == 2) {
                if (!this.mergeItemStack(stackInSlot, 3, 39, true)) {
                    return null;
                }
            } else if (index >= 3) {
                if (!this.mergeItemStack(stackInSlot, 0, 2, false)) {
                    return null;
                }
            } else if (!this.mergeItemStack(stackInSlot, 3, 39, false)) {
                return null;
            }
            if (stackInSlot.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            if (stackInSlot.stackSize == stack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(player, stackInSlot);
        }

        return stack;
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);

        for (int i = 0; i < 2; ++i) {
            ItemStack stackToReturn = inputSlots.getStackInSlot(i);

            if (stackToReturn != null) {
                if (!player.inventory.addItemStackToInventory(stackToReturn)) {
                    player.dropPlayerItemWithRandomChoice(stackToReturn, false);
                }
                inputSlots.setInventorySlotContents(i, null);
            }
        }
    }
}
