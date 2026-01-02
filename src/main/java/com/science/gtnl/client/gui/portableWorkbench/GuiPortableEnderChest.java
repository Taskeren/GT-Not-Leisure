package com.science.gtnl.client.gui.portableWorkbench;

import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPortableEnderChest extends GuiChest {

    public GuiPortableEnderChest(InventoryPlayer playerInv, IInventory enderInv) {
        super(playerInv, enderInv);
    }
}
