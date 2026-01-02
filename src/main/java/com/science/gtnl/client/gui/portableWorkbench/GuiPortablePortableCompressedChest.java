package com.science.gtnl.client.gui.portableWorkbench;

import com.science.gtnl.container.portableWorkbench.ContainerPortableCompressedChest;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPortablePortableCompressedChest extends GuiPortableAvaritiaddonsChest {

    public GuiPortablePortableCompressedChest(ItemStack stack, InventoryPlayer playerInv) {
        super(new ContainerPortableCompressedChest(stack, playerInv));
    }

    @Override
    @NotNull
    public String getContainerName() {
        return "container.CompressedChest";
    }
}
