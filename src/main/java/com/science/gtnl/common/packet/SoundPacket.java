package com.science.gtnl.common.packet;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.ResourceLocation;

import com.github.bsideup.jabel.Desugar;
import com.science.gtnl.common.packet.client.SoundHandler;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class SoundPacket implements IMessage, IMessageHandler<SoundPacket, IMessage> {

    public ResourceLocation soundResource;
    public float volume;
    public float pitch;
    public long seekMs;
    public static Map<String, SoundInfo> soundsToSync;
    public boolean syncPacket;
    public boolean stopAll;

    public SoundPacket() {
        this.stopAll = true;
        this.syncPacket = false;
        this.soundResource = null;
        this.volume = 0;
        this.pitch = 0;
        this.seekMs = 0;
    }

    public SoundPacket(ResourceLocation soundResource, float volume, float pitch, long seekMs) {
        this.syncPacket = false;
        this.soundResource = soundResource;
        this.volume = volume;
        this.pitch = pitch;
        this.seekMs = seekMs;
    }

    public SoundPacket(boolean sync) {
        this.syncPacket = sync;
        this.soundResource = null;
        this.volume = 0;
        this.pitch = 0;
        this.seekMs = 0;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        boolean isStopAll = buf.readBoolean();
        boolean isSyncPacket = buf.readBoolean();
        if (isStopAll) {
            stopAll = true;
            soundResource = null;
            volume = 0;
            pitch = 0;
            seekMs = 0;
            if (soundsToSync != null) {
                soundsToSync.clear();
            }
            return;
        }
        if (isSyncPacket) {
            int mapSize = buf.readInt();
            soundsToSync = new HashMap<>(mapSize);
            for (int i = 0; i < mapSize; i++) {
                String key = ByteBufUtils.readUTF8String(buf);
                String resourceLocationString = ByteBufUtils.readUTF8String(buf);
                ResourceLocation resource = new ResourceLocation(resourceLocationString);
                float vol = buf.readFloat();
                float ptch = buf.readFloat();
                long seek = buf.readLong();
                soundsToSync.put(key, new SoundInfo(resource, vol, ptch, seek));
            }
            soundResource = null;
            volume = 0;
            pitch = 0;
            seekMs = 0;
        } else {
            soundResource = new ResourceLocation(ByteBufUtils.readUTF8String(buf));
            volume = buf.readFloat();
            pitch = buf.readFloat();
            seekMs = buf.readLong();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(stopAll);
        if (stopAll) {
            buf.writeBoolean(false);
            if (soundsToSync != null) soundsToSync.clear();
            return;
        }
        buf.writeBoolean(syncPacket);
        if (syncPacket && soundsToSync != null) {
            buf.writeInt(soundsToSync.size());
            for (Map.Entry<String, SoundInfo> entry : soundsToSync.entrySet()) {
                ByteBufUtils.writeUTF8String(buf, entry.getKey());
                ByteBufUtils.writeUTF8String(buf, entry.getValue().resourceLocation.toString());
                buf.writeFloat(entry.getValue().volume);
                buf.writeFloat(entry.getValue().pitch);
                buf.writeLong(entry.getValue().seekMs);
            }
        } else {
            if (soundResource != null) {
                ByteBufUtils.writeUTF8String(buf, soundResource.toString());
            } else {
                ByteBufUtils.writeUTF8String(buf, "");
            }
            buf.writeFloat(volume);
            buf.writeFloat(pitch);
            buf.writeLong(seekMs);
        }
    }

    @Override
    public IMessage onMessage(SoundPacket message, MessageContext ctx) {
        if (ctx.side.isClient()) {
            SoundHandler.handleSoundPacket(message);
        }
        return null;
    }

    @Desugar
    public record SoundInfo(ResourceLocation resourceLocation, float volume, float pitch, long seekMs) {

    }
}
