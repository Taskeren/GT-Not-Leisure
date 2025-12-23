package com.science.gtnl.common.packet;

import java.util.UUID;

import com.science.gtnl.common.packet.client.SyncHPCAVariablesHandler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class SyncHPCAVariablesPacket implements IMessage, IMessageHandler<SyncHPCAVariablesPacket, IMessage> {

    private UUID uuid;
    private int totalLens;
    private int x, y, z;
    private boolean mMachine;

    public SyncHPCAVariablesPacket() {}

    public SyncHPCAVariablesPacket(UUID uuid, int totalLens, int x, int y, int z, boolean mMachine) {
        this.uuid = uuid;
        this.totalLens = totalLens;
        this.x = x;
        this.y = y;
        this.z = z;
        this.mMachine = mMachine;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
        buf.writeInt(totalLens);
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeBoolean(mMachine);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        long most = buf.readLong();
        long least = buf.readLong();
        uuid = new UUID(most, least);
        totalLens = buf.readInt();
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        mMachine = buf.readBoolean();
    }

    @Override
    public IMessage onMessage(SyncHPCAVariablesPacket message, MessageContext ctx) {
        SyncHPCAVariablesHandler
            .apply(message.x, message.y, message.z, message.uuid, message.totalLens, message.mMachine);
        return null;
    }

}
