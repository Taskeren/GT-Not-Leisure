package com.science.gtnl.common.packet;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.utils.item.ItemUtils;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class RequestGameProfilePacket implements IMessage, IMessageHandler<RequestGameProfilePacket, IMessage> {

    public String playerName;
    public boolean isCreative, useAE;

    public RequestGameProfilePacket() {}

    public RequestGameProfilePacket(String playerName, boolean isCreative, boolean useAE) {
        this.playerName = playerName;
        this.isCreative = isCreative;
        this.useAE = useAE;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, playerName);
        buf.writeBoolean(isCreative);
        buf.writeBoolean(useAE);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.playerName = ByteBufUtils.readUTF8String(buf);
        this.isCreative = buf.readBoolean();
        this.useAE = buf.readBoolean();
    }

    @Override
    public IMessage onMessage(RequestGameProfilePacket message, MessageContext ctx) {
        ScienceNotLeisure.network.sendTo(
            new PlaceItemInHotbarPacket(
                ItemUtils.getPlayerSkull(message.playerName),
                message.isCreative,
                message.useAE),
            ctx.getServerHandler().playerEntity);
        return null;
    }
}
