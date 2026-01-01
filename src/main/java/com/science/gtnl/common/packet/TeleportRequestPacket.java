package com.science.gtnl.common.packet;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class TeleportRequestPacket implements IMessage, IMessageHandler<TeleportRequestPacket, IMessage> {

    private int worldX, worldZ;

    public TeleportRequestPacket() {}

    public TeleportRequestPacket(int x, int z) {
        this.worldX = x;
        this.worldZ = z;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(worldX);
        buf.writeInt(worldZ);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        worldX = buf.readInt();
        worldZ = buf.readInt();
    }

    @Override
    public IMessage onMessage(TeleportRequestPacket message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        WorldServer world = player.getServerForPlayer();

        int x = message.worldX;
        int z = message.worldZ;

        int y = world.getTopSolidOrLiquidBlock(x, z);

        if (world.provider.dimensionId == -1) {
            if (y > 127) {
                y = 125;
                while (y > 1 && !isSafe(world, x, y, z)) {
                    y--;
                }

                if (!isSafe(world, x, y, z)) {
                    for (int dx = -10; dx <= 10; dx++) {
                        for (int dz = -10; dz <= 10; dz++) {
                            int nx = x + dx;
                            int nz = z + dz;
                            int ny = world.getTopSolidOrLiquidBlock(nx, nz);
                            if (ny > 128) ny = 125;
                            while (ny > 1 && !isSafe(world, nx, ny, nz)) {
                                ny--;
                            }
                            if (isSafe(world, nx, ny, nz)) {
                                x = nx;
                                z = nz;
                                y = ny;
                                break;
                            }
                        }
                    }
                }
            }
        }

        player.setPositionAndUpdate(x + 0.5, y + 1, z + 0.5);
        return null;
    }

    public boolean isSafe(World world, int x, int y, int z) {
        return world.getBlock(x, y - 1, z)
            .getMaterial()
            .isSolid() && world.isAirBlock(x, y, z)
            && world.isAirBlock(x, y + 1, z);
    }
}
