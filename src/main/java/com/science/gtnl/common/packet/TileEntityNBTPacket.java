package com.science.gtnl.common.packet;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import com.science.gtnl.utils.item.ItemUtils;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class TileEntityNBTPacket implements IMessage, IMessageHandler<TileEntityNBTPacket, IMessage> {

    private int blockId;
    private int metadata;
    private NBTTagCompound nbt;

    public TileEntityNBTPacket() {}

    public TileEntityNBTPacket(int blockId, int metadata, NBTTagCompound nbt) {
        this.blockId = blockId;
        this.metadata = metadata;
        this.nbt = nbt;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.blockId = buf.readInt();
        this.metadata = buf.readInt();
        this.nbt = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.blockId);
        buf.writeInt(this.metadata);
        ByteBufUtils.writeTag(buf, this.nbt);
    }

    @Override
    public IMessage onMessage(TileEntityNBTPacket message, MessageContext ctx) {
        if (ctx.side.isServer()) return null;
        apply(message.blockId, message.metadata, message.nbt);
        return null;
    }

    @SideOnly(Side.CLIENT)
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
