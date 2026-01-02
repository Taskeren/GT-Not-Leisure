package com.science.gtnl.container.portableWorkbench;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import com.science.gtnl.common.item.items.PortableItem;

public class ContainerPortableCompressedChest extends ContainerPortableAvaritiaddonsChest {

    public ContainerPortableCompressedChest(ItemStack stack, InventoryPlayer playerInv) {
        super(stack, playerInv, false);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        ItemStack held = player.getHeldItem();
        return PortableItem.matchesPortableID(held, portableID);
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        ItemStack held = player.getHeldItem();
        if (PortableItem.matchesPortableID(held, portableID)) {
            type.saveInventory(held, chestInventory);
            itemStack = held;
        }
    }
}
