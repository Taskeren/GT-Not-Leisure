package com.science.gtnl.common.render.tile;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.brandon3055.draconicevolution.common.lib.References;
import com.science.gtnl.common.machine.multiblock.NaquadahReactor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AdvancedHyperNaquadahReactorRenderer {

    public static IModelCustom reactorModel = AdvancedModelLoader
        .loadModel(new ResourceLocation(References.MODID.toLowerCase(), "models/reactorCoreModel.obj"));

    public static void renderTileEntity(NaquadahReactor.AdvancedHyperNaquadahReactor machine, double x, double y,
        double z, float partialTicks) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        ChunkCoordinates pos = machine.getRenderPos();
        GL11.glTranslated(pos.posX, pos.posY, pos.posZ);
        GL11.glTranslated(0.5, 0.5, 0.5);

        GL11.glScalef(13F, 13F, 13F);

        float rotation = machine.rotation + partialTicks;

        GL11.glDisable(GL11.GL_LIGHTING);
        float lastBrightnessX = OpenGlHelper.lastBrightnessX;
        float lastBrightnessY = OpenGlHelper.lastBrightnessY;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200F, 200F);

        ResourceHandler.bindResource("textures/models/reactorCore.png");
        GL11.glColor4d(0.0, 0.6667, 0.8863, 1.0);
        GL11.glRotatef(rotation, 0.5F, 1F, 0.5F);
        GL11.glScaled(0.5, 0.5, 0.5);
        reactorModel.renderAll();

        // Mid Render
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0F);

        ResourceHandler.bindResource("textures/blocks/draconic_block_blank.png");
        reactorModel.renderAll();

        // Render Outer
        ResourceHandler.bindResource("textures/models/reactorShieldPlate.png");
        GL11.glScaled(1.03F, 1.03F, 1.03F);
        GL11.glRotatef(2343, 0.5F, 1F, 0.5F);
        GL11.glRotatef(-rotation * 2, 0.5F, 1F, 0.5F);
        reactorModel.renderAll();

        // Post Render
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1f, 1f, 1f, 1f);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
        GL11.glPopMatrix();
    }
}
