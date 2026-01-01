package com.science.gtnl.common.block.blocks.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityArtificialStar extends TileEntity {

    // 当前的旋转角度
    public double rotation = 0;
    // 当前的大小
    public double size = 0;
    // 目标大小
    public double targetSize = 12;
    // 初始大小
    public double initialSize = 0;
    // 初始旋转速度
    public double initialRotationSpeed = 0;
    // 目标旋转速度
    public double targetRotationSpeed = 0.5;
    // 记录已更新的tick
    public int ticks = 0;
    // 变大的时间（以tick为单位）
    public int duration = 100;
    // 当前放大到第几个模型
    public int currentModelIndex = 0;

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 65536;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setDouble("size", size);
        nbt.setDouble("rotation", rotation);
        nbt.setInteger("currentModelIndex", currentModelIndex);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        size = nbt.getDouble("size");
        rotation = nbt.getDouble("rotation");
        currentModelIndex = nbt.getInteger("currentModelIndex");
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (ticks < duration) {
            double t = (double) ticks / duration;
            double easedT = cubicEaseOut(t);
            size = initialSize + (targetSize - initialSize) * easedT;
            rotation += initialRotationSpeed + (targetRotationSpeed - initialRotationSpeed) * easedT;
            ticks++;
        } else {
            size = targetSize;
            rotation = (rotation + targetRotationSpeed) % 360d;
            currentModelIndex++;
            ticks = 0;
        }
    }

    public double cubicEaseOut(double t) {
        return 1 - Math.pow(1 - t, 3);
    }
}
