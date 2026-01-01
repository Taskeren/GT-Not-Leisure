package com.science.gtnl.common.packet.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TitleDisplayHandler {

    public static String currentTitle = "";
    public static int ticksRemaining = 0;
    public static int colorText = 0xFFFFFF;
    public static double scaleText = 3;
    public static int fadeIn = 10;
    public static int fadeOut = 20;
    public static int ticksAlltime = 0;

    public static void displayTitle(String text, int durationTicks, int color, double scale, int customFadeIn,
        int customFadeOut) {
        currentTitle = text;
        colorText = color;
        scaleText = scale;
        fadeIn = customFadeIn;
        fadeOut = customFadeOut;
        ticksAlltime = durationTicks;
        ticksRemaining = fadeIn + durationTicks + fadeOut;
    }

    public static int getArgb() {
        float alpha;
        if (ticksRemaining > ticksAlltime + fadeOut) {
            // Fade in
            alpha = 1.0f - (ticksRemaining - (ticksAlltime + fadeOut)) / (float) fadeIn;
        } else if (ticksRemaining > fadeOut) {
            // Stay
            alpha = 1.0f;
        } else {
            // Fade out
            alpha = ticksRemaining / (float) fadeOut;
        }
        alpha = Math.min(Math.max(alpha, 0f), 1f);

        int r = (colorText >> 16) & 0xFF;
        int g = (colorText >> 8) & 0xFF;
        int b = colorText & 0xFF;
        int a = (int) (alpha * 255);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        if (ticksRemaining > 0 && currentTitle != null && !currentTitle.isEmpty()) {
            GL11.glPushMatrix();
            Minecraft mc = Minecraft.getMinecraft();
            ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
            FontRenderer fr = mc.fontRenderer;

            int stringWidth = fr.getStringWidth(currentTitle);
            int stringHeight = 9;

            double scale = scaleText;
            int x = (res.getScaledWidth() - (int) (stringWidth * scale)) / 2;
            int y = (res.getScaledHeight() - (int) (stringHeight * scale)) / 2;

            if (StatCollector.canTranslate(currentTitle)) {
                currentTitle = StatCollector.translateToLocal(currentTitle);
            }

            int argb = getArgb();

            GL11.glTranslated(x, y, 0);
            GL11.glScaled(scale, scale, 1);

            fr.drawStringWithShadow(currentTitle, 0, 0, argb);

            GL11.glPopMatrix();

            ticksRemaining--;
        }
    }
}
