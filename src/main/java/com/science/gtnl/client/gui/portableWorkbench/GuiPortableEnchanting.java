package com.science.gtnl.client.gui.portableWorkbench;

import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPortableEnchanting extends GuiEnchantment {

    public GuiPortableEnchanting(InventoryPlayer playerInv, World world) {
        super(playerInv, world, 0, 0, 0, null);
    }
}
