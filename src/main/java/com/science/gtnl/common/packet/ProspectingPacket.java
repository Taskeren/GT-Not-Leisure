package com.science.gtnl.common.packet;

import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.github.bsideup.jabel.Desugar;
import com.google.common.base.Objects;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.utils.detrav.DetravMapTexture;
import com.science.gtnl.utils.detrav.DetravScannerGUI;

import bartworks.system.material.Werkstoff;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import detrav.utils.FluidColors;
import detrav.utils.GTppHelper;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.util.GTLanguageManager;
import gtPlusPlus.core.material.Material;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.bytes.Byte2ShortMap;
import it.unimi.dsi.fastutil.bytes.Byte2ShortOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;

public class ProspectingPacket implements IMessage {

    public int chunkX, chunkZ, posX, posZ, size, ptype;
    public Byte2ShortMap[][] map;
    public Object2IntMap<String> ores = new Object2IntOpenHashMap<>();
    public Short2ObjectMap<String> metaMap = new Short2ObjectOpenHashMap<>();

    public ProspectingPacket() {}

    public ProspectingPacket(int chunkX, int chunkZ, int posX, int posZ, int size, int ptype) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.posX = posX;
        this.posZ = posZ;
        this.size = size;
        this.ptype = ptype;
        this.map = new Byte2ShortMap[(size * 2 + 1) * 16][(size * 2 + 1) * 16];
        this.ores = new Object2IntOpenHashMap<>();
        this.metaMap = new Short2ObjectOpenHashMap<>();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        chunkX = buf.readInt();
        chunkZ = buf.readInt();
        posX = buf.readInt();
        posZ = buf.readInt();
        size = buf.readInt();
        ptype = buf.readInt();

        int len = (size * 2 + 1) * 16;
        map = new Byte2ShortMap[len][len];
        int total = 0;

        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                byte count = buf.readByte();
                if (count == 0) continue;
                map[i][j] = new Byte2ShortOpenHashMap();
                for (int k = 0; k < count; k++) {
                    byte y = buf.readByte();
                    short meta = buf.readShort();
                    map[i][j].put(y, meta);
                    if (ptype != 2 || y == 1) {
                        addOre(this, y, i, j, meta);
                    }
                    total++;
                }
            }
        }

        int check = buf.readInt();
        if (check != total) {
            map = null; // signal error
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(chunkX);
        buf.writeInt(chunkZ);
        buf.writeInt(posX);
        buf.writeInt(posZ);
        buf.writeInt(size);
        buf.writeInt(ptype);

        int len = (size * 2 + 1) * 16;
        int count = 0;

        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                Byte2ShortMap data = map[i][j];
                if (data == null) {
                    buf.writeByte(0);
                } else {
                    buf.writeByte(data.size());
                    for (Byte2ShortMap.Entry s : data.byte2ShortEntrySet()) {
                        buf.writeByte(s.getByteKey());
                        buf.writeShort(s.getShortValue());
                        count++;
                    }
                }
            }
        }

        buf.writeInt(count);
    }

    public static class Handler implements IMessageHandler<ProspectingPacket, IMessage> {

        @Override
        public IMessage onMessage(ProspectingPacket message, MessageContext ctx) {
            if (message.map == null) return null;

            ProspectingWrapper wrapper = new ProspectingWrapper(
                message.chunkX,
                message.chunkZ,
                message.posX,
                message.posZ,
                message.size,
                message.ptype,
                message.map,
                message.ores,
                message.metaMap);

            DetravScannerGUI.newMap(new DetravMapTexture(wrapper));
            ScienceNotLeisure.proxy.openProspectorGUI();
            return null;
        }
    }

    public static void addOre(ProspectingPacket packet, byte y, int i, int j, short meta) {
        final short[] rgba;
        final String name;
        try {
            if (packet.ptype == 0 || packet.ptype == 1) {
                if (meta < 7000 || meta > 7500) {
                    if (meta > 0) {
                        Materials mat = GregTechAPI.sGeneratedMaterials[meta % 1000];
                        rgba = mat.getRGBA();
                        name = mat.getLocalizedNameForItem(
                            GTLanguageManager.getTranslation("gt.blockores." + meta + ".name"));
                    } else {
                        Werkstoff w = Werkstoff.werkstoffHashMap.get((short) (-meta));
                        name = GTLanguageManager.getTranslation("bw.blocktype.ore")
                            .replace("%material", w.getLocalizedName());
                        rgba = w.getRGBA();
                    }
                } else {
                    Material mat = GTppHelper.getMatFromMeta(meta);
                    rgba = mat.getRGBA();
                    name = mat.getLocalizedName() + " Ore";
                }
            } else if (packet.ptype == 2) {
                rgba = FluidColors.getColor(meta);
                name = Objects.firstNonNull(
                    FluidRegistry.getFluid(meta)
                        .getLocalizedName(new FluidStack(FluidRegistry.getFluid(meta), 0)),
                    StatCollector.translateToLocal("gui.detrav.scanner.unknown_fluid"));
            } else if (packet.ptype == 3) {
                name = StatCollector.translateToLocal("gui.detrav.scanner.pollution");
                rgba = new short[] { 125, 123, 118, 0 };
            } else return;
        } catch (Exception e) {
            return;
        }
        packet.ores.put(name, ((rgba[0] & 0xFF) << 16) + ((rgba[1] & 0xFF) << 8) + ((rgba[2] & 0xFF)));
        packet.metaMap.put(meta, name);
    }

    public void addBlock(int x, int y, int z, short metaData) {
        int aX = x - (chunkX - size) * 16;
        int aZ = z - (chunkZ - size) * 16;
        if (map[aX][aZ] == null) map[aX][aZ] = new Byte2ShortOpenHashMap();
        map[aX][aZ].put((byte) y, metaData);
    }

    @Desugar
    public record ProspectingWrapper(int chunkX, int chunkZ, int posX, int posZ, int size, int ptype,
        Byte2ShortMap[][] map, Object2IntMap<String> ores, Short2ObjectMap<String> metaMap) {}
}
