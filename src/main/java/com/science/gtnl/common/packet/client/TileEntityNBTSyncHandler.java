package com.science.gtnl.common.packet.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import com.science.gtnl.utils.item.ItemUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityNBTSyncHandler {

    public void apply(int blockId, int metadata, NBTTagCompound tileEntityNBT) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityClientPlayerMP player = mc.thePlayer;
        if (player != null) {

            Block block = Block.getBlockById(blockId);

            if (block != null) {
                ItemStack result = new ItemStack(block, 1, metadata);

                if (tileEntityNBT != null && !tileEntityNBT.hasNoTags()) {
                    NBTTagCompound itemTag = new NBTTagCompound();
                    itemTag.setTag("BlockEntityTag", tileEntityNBT);

                    NBTTagCompound displayTag = new NBTTagCompound();
                    NBTTagList loreList = new NBTTagList();
                    loreList.appendTag(new NBTTagString("§5§o(+NBT)"));
                    displayTag.setTag("Lore", loreList);
                    itemTag.setTag("display", displayTag);
                    result.setTagCompound(itemTag);
                }

                boolean isCreative = player.capabilities.isCreativeMode;
                ItemUtils.placeItemInHotbar(player, result, isCreative, true);
            }
        }
    }

}
