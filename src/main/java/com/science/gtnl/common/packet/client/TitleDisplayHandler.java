package com.science.gtnl.common.packet.client;

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
}
