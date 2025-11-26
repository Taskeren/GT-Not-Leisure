package com.science.gtnl.common.render.tile;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.client.render.entity.RenderEntityChaosVortex;
import com.gtnewhorizon.structurelib.alignment.enumerable.Rotation;
import com.science.gtnl.common.machine.multiblock.wireless.KerrNewmanHomogenizer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;

@SideOnly(Side.CLIENT)
public class KerrNewmanHomogenizerRenderer {

    public static void renderTileEntityAt(KerrNewmanHomogenizer machine, double x, double y, double z,
        float partialTicks) {

        ForgeDirection back = machine.getExtendedFacing()
            .getRelativeBackInWorld();
        Rotation rot = machine.getExtendedFacing()
            .getRotation();

        int xOffset = 34 * back.offsetX;
        int yOffset = 34 * back.offsetY;
        int zOffset = 34 * back.offsetZ;

        GL11.glPushMatrix();

        GL11.glTranslated(x + 0.5 + xOffset, y + 0.5 + yOffset, z + 0.5 + zOffset);

        KuangBiaoOneGiantNuclearFusionReactorRenderer.applyFacingRotation(back, rot);

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor3f(0f, 0f, 0f);
        GL11.glScaled(4.5f, 4.5f, 4.5f);
        RenderEntityChaosVortex.uvSphere.renderAll();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glColor3f(1f, 1f, 1f);
        GL11.glPopMatrix();

        double rotation = machine.getInterpolatedRotation(partialTicks);

        GL11.glRotated(rotation, 1, 1, 1);
        KuangBiaoOneGiantNuclearFusionReactorRenderer.renderRing(x, y, z, partialTicks, 20f, 0.9f, 128, 32);

        GL11.glRotated(rotation, 0, 0, 1);
        KuangBiaoOneGiantNuclearFusionReactorRenderer.renderRing(x, y, z, partialTicks, 24f, 0.9f, 128, 32);

        GL11.glRotated(rotation, 0, 0, -1);
        GL11.glRotated(rotation, 1, 0, 0);
        KuangBiaoOneGiantNuclearFusionReactorRenderer.renderRing(x, y, z, partialTicks, 28f, 0.9f, 128, 32);

        GL11.glPopMatrix();

        if (machine.getTotalRunTime() % 5 == 0) {
            IGregTechTileEntity tileEntity = machine.getBaseMetaTileEntity();
            World world = tileEntity.getWorld();
            double posX = tileEntity.getXCoord() + xOffset;
            double posY = tileEntity.getYCoord() + yOffset;
            double posZ = tileEntity.getZCoord() + zOffset;
            List<EntityPlayer> players = world.getEntitiesWithinAABB(
                EntityPlayer.class,
                AxisAlignedBB.getBoundingBox(posX - 32, posY - 32, posZ - 32, posX + 32, posY + 32, posZ + 32));

            for (EntityPlayer player : players) {
                double dx = player.posX - posX;
                double dy = player.posY - posY;
                double dz = player.posZ - posZ;
                double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

                if (distance > 32) continue;

                double intensity = (32 - distance) / 32.0;

                double offsetXShake = (world.rand.nextDouble() - 0.5) * 2 * intensity;
                double offsetZShake = (world.rand.nextDouble() - 0.5) * 2 * intensity;

                player.moveEntity(offsetXShake / 5.0, 0, offsetZShake / 5.0);
                player.rotationYaw -= (float) offsetXShake * 1.5f;
                player.rotationPitch -= (float) offsetZShake * 1.5f;
            }
        }
    }
}
