package com.science.gtnl.common.packet;

import java.nio.charset.StandardCharsets;

import net.minecraft.client.Minecraft;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class SudoPacket implements IMessage, IMessageHandler<SudoPacket, IMessage> {

    public String message;

    public SudoPacket() {}

    public SudoPacket(String message) {
        this.message = message;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int length = buf.readInt();
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        this.message = new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
    }

    @Override
    public IMessage onMessage(SudoPacket message, MessageContext ctx) {
        if (ctx.side.isServer()) return null;
        sendChatMessage(message.message);
        return null;
    }

    @SideOnly(Side.CLIENT)
    public void sendChatMessage(String message) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.thePlayer.sendChatMessage(message);
    }
}
