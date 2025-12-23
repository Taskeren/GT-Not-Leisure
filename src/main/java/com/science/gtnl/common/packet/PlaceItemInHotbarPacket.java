package com.science.gtnl.common.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import com.science.gtnl.utils.item.ItemUtils;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class PlaceItemInHotbarPacket implements IMessage, IMessageHandler<PlaceItemInHotbarPacket, IMessage> {

    public ItemStack result;
    public boolean isCreative, useAE;

    public PlaceItemInHotbarPacket() {}

    public PlaceItemInHotbarPacket(ItemStack result, boolean isCreative, boolean useAE) {
        this.result = result;
        this.isCreative = isCreative;
        this.useAE = useAE;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, result);
        buf.writeBoolean(isCreative);
        buf.writeBoolean(useAE);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.result = ByteBufUtils.readItemStack(buf);
        this.isCreative = buf.readBoolean();
        this.useAE = buf.readBoolean();
    }

    @Override
    public IMessage onMessage(PlaceItemInHotbarPacket message, MessageContext ctx) {
        if (ctx.side.isServer()) return null;
        ItemStack skull = message.result;
        if (skull != null) apply(skull, message.isCreative, message.useAE);
        return null;
    }

    @SideOnly(Side.CLIENT)
    public void apply(ItemStack skull, boolean isCreative, boolean useAE) {
        if (skull != null) ItemUtils.placeItemInHotbar(Minecraft.getMinecraft().thePlayer, skull, isCreative, useAE);
    }
}
