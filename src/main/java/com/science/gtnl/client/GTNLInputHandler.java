package com.science.gtnl.client;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.lwjgl.input.Mouse;

import com.glodblock.github.client.gui.GuiItemMonitor;
import com.gtnewhorizons.modularui.api.KeyboardUtil;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.item.items.Stick;
import com.science.gtnl.common.packet.KeyBindingHandler;
import com.science.gtnl.utils.ClientUtils;

import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.client.gui.implementations.GuiMEMonitorable;
import appeng.container.implementations.ContainerCraftAmount;
import appeng.container.implementations.ContainerCraftConfirm;
import codechicken.nei.BookmarkPanel;
import codechicken.nei.LayoutManager;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.Widget;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.guihook.IContainerInputHandler;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.util.AssemblyLineUtils;

@SideOnly(Side.CLIENT)
public class GTNLInputHandler implements IContainerInputHandler {

    public static GTNLInputHandler INSTANCE = new GTNLInputHandler();
    public static Map<String, BooleanSupplier> keys = new HashMap<>() {

        {
            put("gui.ae_retrieve_item", () -> KeyboardUtil.isCtrlKeyDown() && Mouse.isButtonDown(2));
            put("gui.ae_start_craft", () -> KeyboardUtil.isAltKeyDown() && Mouse.isButtonDown(2));
        }
    };

    public static final Minecraft mc = Minecraft.getMinecraft();
    public static int inputCooldownTicks = 0;
    public static int animationTick = 0;
    public static int frameCounter = 0;
    public static GuiScreen oldGui = null;
    public static Runnable delayMethod = null;

    public GTNLInputHandler() {
        GuiContainerManager.addInputHandler(this);
    }

    @Override
    public boolean lastKeyTyped(GuiContainer gui, char keyChar, int keyCode) {
        ItemStack stack = GuiContainerManager.getStackMouseOver(gui);
        if (stack == null) return false;
        Item item = stack.getItem();

        if (item instanceof ICraftingPatternItem pattern) {
            ICraftingPatternDetails details = pattern.getPatternForItem(stack, Minecraft.getMinecraft().theWorld);
            if (details == null) return false;

            stack = details.getCondensedOutputs()[0].getItemStack();
        }

        if (item instanceof Stick) {
            ItemStack fake = Stick.getDisguisedStack(stack);
            if (fake == null) return false;

            stack = fake;
        }

        ItemStack dataStickOutput = AssemblyLineUtils.getDataStickOutput(stack);
        if (dataStickOutput != null) stack = dataStickOutput;

        if (NEIClientConfig.isKeyHashDown("gui.recipe")) {
            return GuiCraftingRecipe.openRecipeGui("item", stack);
        }

        if (NEIClientConfig.isKeyHashDown("gui.usage")) {
            return GuiUsageRecipe.openRecipeGui("item", stack);
        }
        return false;
    }

    @Override
    public boolean mouseClicked(GuiContainer gui, int mousex, int mousey, int button) {
        ItemStack stack = GuiContainerManager.getStackMouseOver(gui);
        if (stack == null) return false;
        return startAEWork(stack, mousex, mousey);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (inputCooldownTicks > 0) {
            inputCooldownTicks--;
        }
        animationTick = (animationTick + ((++frameCounter & 1) == 0 ? 1 : 0)) % 14;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onInputEvent(final InputEvent.KeyInputEvent event) {
        if (!mc.thePlayer.capabilities.isCreativeMode && inputCooldownTicks == 0
            && mc.gameSettings.keyBindPickBlock.isPressed()) {
            EntityClientPlayerMP player = mc.thePlayer;
            World world = player.worldObj;
            ClientUtils.onBeforePickBlock(player, world, true);
            inputCooldownTicks = 10;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onInputEvent(final InputEvent.MouseInputEvent event) {
        if (!mc.thePlayer.capabilities.isCreativeMode && inputCooldownTicks == 0
            && mc.gameSettings.keyBindPickBlock.isPressed()) {
            EntityClientPlayerMP player = mc.thePlayer;
            World world = player.worldObj;
            ClientUtils.onBeforePickBlock(player, world, true);
            inputCooldownTicks = 10;
        }
    }

    public boolean startAEWork(ItemStack item, int mouseX, int mouseY) {
        for (Map.Entry<String, BooleanSupplier> key : keys.entrySet()) {
            if (!key.getValue()
                .getAsBoolean()) continue;
            final Widget focused = LayoutManager.instance()
                .getWidgetUnderMouse(mouseX, mouseY);

            if (!(focused instanceof BookmarkPanel)) return false;
            final var oldGui = Minecraft.getMinecraft().currentScreen;
            ScienceNotLeisure.network.sendToServer(
                new KeyBindingHandler(
                    key.getKey(),
                    item,
                    oldGui instanceof GuiMEMonitorable || oldGui instanceof GuiItemMonitor));
            if (key.getKey()
                .equals("gui.ae_start_craft")) {
                var player = Minecraft.getMinecraft().thePlayer;
                if (player.openContainer instanceof ContainerCraftAmount
                    || player.openContainer instanceof ContainerCraftConfirm) return false;
                GTNLInputHandler.oldGui = oldGui;
            }
            return true;
        }

        return false;
    }

    @Override
    public void onKeyTyped(GuiContainer gui, char keyChar, int keyID) {}

    @Override
    public void onMouseClicked(GuiContainer gui, int mousex, int mousey, int button) {}

    @Override
    public void onMouseUp(GuiContainer gui, int mousex, int mousey, int button) {}

    @Override
    public boolean keyTyped(GuiContainer gui, char keyChar, int keyID) {
        return false;
    }

    @Override
    public boolean mouseScrolled(GuiContainer gui, int mousex, int mousey, int scrolled) {
        return false;
    }

    @Override
    public void onMouseScrolled(GuiContainer gui, int mousex, int mousey, int scrolled) {}

    @Override
    public void onMouseDragged(GuiContainer gui, int mousex, int mousey, int button, long heldTime) {}

}
