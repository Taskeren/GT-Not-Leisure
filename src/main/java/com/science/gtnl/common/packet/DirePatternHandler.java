package com.science.gtnl.common.packet;

import com.science.gtnl.utils.gui.ContainerDirePatternEncoder;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class DirePatternHandler implements IMessage, IMessageHandler<DirePatternHandler, IMessage> {

    private byte id;
    private boolean isShift;

    public DirePatternHandler() {

    }

    public DirePatternHandler(byte id) {
        this.id = id;
    }

    public DirePatternHandler(byte id, boolean shift) {
        this.id = id;
        isShift = shift;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        id = buf.readByte();
        isShift = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(id);
        buf.writeBoolean(isShift);
    }

    @Override
    public IMessage onMessage(DirePatternHandler message, MessageContext ctx) {
        if (ctx.getServerHandler().playerEntity.openContainer instanceof ContainerDirePatternEncoder c) {
            switch (message.id) {
                case 0 -> c.encode(message.isShift);
                case 1 -> c.clear();
            }
        }
        return null;
    }
}
