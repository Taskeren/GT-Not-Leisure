package com.science.gtnl.common.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.container.portableWorkbench.ContainerPortableInfinityChest;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PortableInfinityChestSyncPacket
    implements IMessage, IMessageHandler<PortableInfinityChestSyncPacket, IMessage> {

    private ItemStack itemStack;
    private int slot;
    private int stackSize;

    public PortableInfinityChestSyncPacket() {}

    public PortableInfinityChestSyncPacket(final ItemStack itemStack, final int slot) {
        if (itemStack != null) this.stackSize = (this.itemStack = itemStack).stackSize;
        else this.itemStack = null;
        this.slot = slot;
    }

    @Override
    public void fromBytes(final ByteBuf buf) {
        itemStack = ByteBufUtils.readItemStack(buf);
        slot = ByteBufUtils.readVarShort(buf);
        stackSize = ByteBufUtils.readVarInt(buf, 5);
    }

    @Override
    public void toBytes(final ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, itemStack);
        ByteBufUtils.writeVarShort(buf, slot);
        ByteBufUtils.writeVarInt(buf, stackSize, 5);
    }

    @Override
    public IMessage onMessage(final PortableInfinityChestSyncPacket message, final MessageContext ctx) {
        final EntityPlayer entityPlayer = ScienceNotLeisure.proxy.getEntityPlayerFromContext(ctx);
        if (entityPlayer.openContainer instanceof ContainerPortableInfinityChest container)
            container.syncData(message.itemStack, message.slot, message.stackSize);
        return null;
    }
}
