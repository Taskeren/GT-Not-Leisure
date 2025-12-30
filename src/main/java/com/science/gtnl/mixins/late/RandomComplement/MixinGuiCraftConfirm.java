package com.science.gtnl.mixins.late.RandomComplement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.GTNLInputHandler;
import com.science.gtnl.common.packet.ContainerRollBACK;

import appeng.client.gui.implementations.GuiCraftConfirm;

@Mixin(value = GuiCraftConfirm.class, remap = false)
public abstract class MixinGuiCraftConfirm {

    @Shadow
    public abstract void switchToOriginalGUI();

    @Shadow
    private GuiButton start;

    @Shadow
    private GuiButton startWithFollow;

    @Redirect(
        method = "actionPerformed",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/client/gui/implementations/GuiCraftConfirm;switchToOriginalGUI()V"))
    public void onActionPerformed0(GuiCraftConfirm instance) {
        GuiScreen oldGui;
        if ((oldGui = GTNLInputHandler.oldGui) != null) {
            ScienceNotLeisure.network.sendToServer(new ContainerRollBACK());
            return;
        }
        this.switchToOriginalGUI();
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"))
    public void onActionPerformed1(GuiButton btn, CallbackInfo ci) {
        if (btn == this.start || btn == this.startWithFollow) {
            GuiScreen oldGui;
            if ((oldGui = GTNLInputHandler.oldGui) != null) {
                GTNLInputHandler.delayMethod = () -> Minecraft.getMinecraft()
                    .displayGuiScreen(oldGui);
            }
        }
    }

}
