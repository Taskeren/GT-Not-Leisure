package com.science.gtnl.common.block.casings;

import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

@SideOnly(Side.CLIENT)
public class BlockStaticDataClientOnly {

    public static final Int2ObjectMap<IIcon> iconsBlockMapBase = new Int2ObjectOpenHashMap<>();

    public static final Int2ObjectMap<IIcon> iconsBlockMapGlow = new Int2ObjectOpenHashMap<>();

    public static final Int2ObjectMap<IIcon> iconsBlockMapGlass = new Int2ObjectOpenHashMap<>();
}
