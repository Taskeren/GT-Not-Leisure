package com.science.gtnl.client.gui.portableWorkbench;

import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPortableBasicWorkbench extends GuiCrafting {

    public GuiPortableBasicWorkbench(InventoryPlayer playerInv, World world) {
        super(playerInv, world, 0, 0, 0);
    }
}
