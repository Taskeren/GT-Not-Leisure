package Forge;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class NullPointerException extends Entity implements IProjectile {

    private int hitBlockX = -1;
    private int hitBlockY = -1;
    private int hitBlockZ = -1;
    private Block hitBlock;
    private int inData;
    private boolean inGround;
    private int ticksInGround;

    public NullPointerException(World world) {
        super(world);
        this.renderDistanceWeight = 10.0D;
        this.setSize(0.5F, 0.5F);
    }

    public NullPointerException(World world, EntityLivingBase shooter, float velocity) {
        super(world);
        this.renderDistanceWeight = 10.0D;

        this.setSize(0.5F, 0.5F);
        this.setLocationAndAngles(
            shooter.posX,
            shooter.posY + shooter.getEyeHeight(),
            shooter.posZ,
            shooter.rotationYaw,
            shooter.rotationPitch);

        this.posX -= MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F) * 0.16F;
        this.posY -= 0.1D;
        this.posZ -= MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F) * 0.16F;
        this.setPosition(this.posX, this.posY, this.posZ);
        this.yOffset = 0.0F;

        this.motionX = -MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F)
            * MathHelper.cos(this.rotationPitch * (float) Math.PI / 180.0F);
        this.motionZ = MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F)
            * MathHelper.cos(this.rotationPitch * (float) Math.PI / 180.0F);
        this.motionY = -MathHelper.sin(this.rotationPitch * (float) Math.PI / 180.0F);

        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, velocity * 1.5F, 1.0F);
    }

    @Override
    protected void entityInit() {
        this.dataWatcher.addObject(16, (byte) 0);
    }

    /**
     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction.
     */
    @Override
    public void setThrowableHeading(double x, double y, double z, float velocity, float inaccuracy) {
        float magnitude = MathHelper.sqrt_double(x * x + y * y + z * z);
        x /= magnitude;
        y /= magnitude;
        z /= magnitude;

        x += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.0075D * inaccuracy;
        y += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.0075D * inaccuracy;
        z += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.0075D * inaccuracy;

        x *= velocity;
        y *= velocity;
        z *= velocity;

        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;

        float horizontalMag = MathHelper.sqrt_double(x * x + z * z);
        this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(x, z) * 180.0D / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(y, horizontalMag) * 180.0D / Math.PI);

        this.ticksInGround = 0;
    }

    /**
     * Sets the position and rotation. Only difference from the other one is no bounding on the rotation. Args: posX,
     * posY, posZ, yaw, pitch
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int rotationIncrements) {
        this.setPosition(x, y, z);
        this.setRotation(yaw, pitch);
    }

    /**
     * Sets the velocity to the args. Args: x, y, z
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void setVelocity(double x, double y, double z) {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
            this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(x, z) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch;
            this.prevRotationYaw = this.rotationYaw;
            this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            this.ticksInGround = 0;
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
            float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D
                / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(this.motionY, f) * 180.0D / Math.PI);
        }

        Block block = this.worldObj.getBlock(this.hitBlockX, this.hitBlockY, this.hitBlockZ);

        if (block.getMaterial() != Material.air) {
            block.setBlockBoundsBasedOnState(this.worldObj, this.hitBlockX, this.hitBlockY, this.hitBlockZ);
            AxisAlignedBB axisalignedbb = block
                .getCollisionBoundingBoxFromPool(this.worldObj, this.hitBlockX, this.hitBlockY, this.hitBlockZ);

            if (axisalignedbb != null
                && axisalignedbb.isVecInside(Vec3.createVectorHelper(this.posX, this.posY, this.posZ))) {
                this.inGround = true;
            }
        }

        if (this.inGround) {
            int j = this.worldObj.getBlockMetadata(this.hitBlockX, this.hitBlockY, this.hitBlockZ);

            if (block == this.hitBlock && j == this.inData) {
                worldObj.removeEntity(this);
                if (worldObj.isRemote) {
                    throw new java.lang.NullPointerException("java.lang.NullPointerException");
                }
            } else {
                this.inGround = false;
                this.motionX *= this.rand.nextFloat() * 0.2F;
                this.motionY *= this.rand.nextFloat() * 0.2F;
                this.motionZ *= this.rand.nextFloat() * 0.2F;
                this.ticksInGround = 0;
            }
        } else {
            Vec3 startVec = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
            Vec3 endVec = Vec3
                .createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            MovingObjectPosition hitResult = this.worldObj.func_147447_a(startVec, endVec, false, true, false);

            if (hitResult != null) {
                this.hitBlockX = hitResult.blockX;
                this.hitBlockY = hitResult.blockY;
                this.hitBlockZ = hitResult.blockZ;
                this.hitBlock = this.worldObj.getBlock(this.hitBlockX, this.hitBlockY, this.hitBlockZ);
                this.inData = this.worldObj.getBlockMetadata(this.hitBlockX, this.hitBlockY, this.hitBlockZ);
                this.motionX = hitResult.hitVec.xCoord - this.posX;
                this.motionY = hitResult.hitVec.yCoord - this.posY;
                this.motionZ = hitResult.hitVec.zCoord - this.posZ;
                float hitMag = MathHelper.sqrt_double(
                    this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                this.posX -= this.motionX / hitMag * 0.05D;
                this.posY -= this.motionY / hitMag * 0.05D;
                this.posZ -= this.motionZ / hitMag * 0.05D;
                this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
                this.inGround = true;

                if (this.hitBlock.getMaterial() != Material.air) {
                    this.hitBlock
                        .onEntityCollidedWithBlock(this.worldObj, this.hitBlockX, this.hitBlockY, this.hitBlockZ, this);
                }
            }
        }

        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;

        float motionMagXZ = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
        this.rotationPitch = (float) (Math.atan2(this.motionY, motionMagXZ) * 180.0D / Math.PI);

        while (this.rotationPitch - this.prevRotationPitch < -180.0F) this.prevRotationPitch -= 360.0F;
        while (this.rotationPitch - this.prevRotationPitch >= 180.0F) this.prevRotationPitch += 360.0F;
        while (this.rotationYaw - this.prevRotationYaw < -180.0F) this.prevRotationYaw -= 360.0F;
        while (this.rotationYaw - this.prevRotationYaw >= 180.0F) this.prevRotationYaw += 360.0F;

        this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
        this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;

        float friction = 0.99F;
        float gravity = 0.05F;

        this.motionX *= friction;
        this.motionY *= friction;
        this.motionZ *= friction;
        this.motionY -= gravity;

        this.setPosition(this.posX, this.posY, this.posZ);
        this.func_145775_I();

    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        tagCompound.setShort("xTile", (short) this.hitBlockX);
        tagCompound.setShort("yTile", (short) this.hitBlockY);
        tagCompound.setShort("zTile", (short) this.hitBlockZ);
        tagCompound.setShort("life", (short) this.ticksInGround);
        tagCompound.setByte("inTile", (byte) Block.getIdFromBlock(this.hitBlock));
        tagCompound.setByte("inData", (byte) this.inData);
        tagCompound.setByte("inGround", (byte) (this.inGround ? 1 : 0));
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        this.hitBlockX = tagCompund.getShort("xTile");
        this.hitBlockY = tagCompund.getShort("yTile");
        this.hitBlockZ = tagCompund.getShort("zTile");
        this.ticksInGround = tagCompund.getShort("life");
        this.hitBlock = Block.getBlockById(tagCompund.getByte("inTile") & 255);
        this.inData = tagCompund.getByte("inData") & 255;
        this.inGround = tagCompund.getByte("inGround") == 1;
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    @Override
    public boolean canTriggerWalking() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public float getShadowSize() {
        return 0.0F;
    }

    /**
     * If returns false, the item will not inflict any damage against entities.
     */
    @Override
    public boolean canAttackWithItem() {
        return false;
    }
}
