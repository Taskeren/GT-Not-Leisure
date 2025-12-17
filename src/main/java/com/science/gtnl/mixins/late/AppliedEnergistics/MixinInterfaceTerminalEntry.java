package com.science.gtnl.mixins.late.AppliedEnergistics;

import net.minecraft.util.StatCollector;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import appeng.client.gui.implementations.GuiInterfaceTerminal;

@Mixin(targets = "appeng.client.gui.implementations.GuiInterfaceTerminal$InterfaceTerminalEntry", remap = false)
public abstract class MixinInterfaceTerminalEntry {

    @Shadow
    String dispName;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void gtnl$onInit(GuiInterfaceTerminal init, long id, String name, int rows, int rowSize, boolean online,
        boolean p2pOutput, CallbackInfo ci) {
        if (name == null) return;

        boolean hasCircuit = name.startsWith("gt_circuit_");
        boolean hasExtra = name.contains("extra_start_");

        if (!hasCircuit && !hasExtra) {
            return;
        }

        String numberPart = null;
        String afterPrefix;

        if (hasCircuit) {
            String rest = name.substring("gt_circuit_".length());
            int firstSplit = rest.indexOf('_');
            if (firstSplit <= 0 || firstSplit >= rest.length() - 1) {
                return;
            }
            numberPart = rest.substring(0, firstSplit);
            afterPrefix = rest.substring(firstSplit + 1);
        } else {
            afterPrefix = name;
        }

        String extraKey = null;
        String mainKey;

        String extraStart = "extra_start_";
        String extraEnd = "_extra_end_";

        int startIdx = afterPrefix.indexOf(extraStart);
        int endIdx = afterPrefix.indexOf(extraEnd);

        if (startIdx == 0 && endIdx > extraStart.length()) {
            extraKey = afterPrefix.substring(extraStart.length(), endIdx);
            mainKey = afterPrefix.substring(endIdx + extraEnd.length());
        } else {
            mainKey = afterPrefix;
        }

        String mainText;
        if (StatCollector.canTranslate(mainKey)) {
            mainText = StatCollector.translateToLocal(mainKey);
        } else if (StatCollector.canTranslate(mainKey + ".name")) {
            mainText = StatCollector.translateToLocal(mainKey + ".name");
        } else {
            mainText = StatCollector.translateToFallback(mainKey);
        }

        String extraText = null;
        if (extraKey != null) {
            if (StatCollector.canTranslate(extraKey)) {
                extraText = StatCollector.translateToLocal(extraKey);
            } else if (StatCollector.canTranslate(extraKey + ".name")) {
                extraText = StatCollector.translateToLocal(extraKey + ".name");
            } else {
                extraText = StatCollector.translateToFallback(extraKey);
            }
        }

        StringBuilder sb = new StringBuilder(mainText);

        if (numberPart != null) {
            sb.append(" - ")
                .append(numberPart);
        }

        if (extraText != null) {
            sb.append(" - ")
                .append(extraText);
        }

        this.dispName = sb.toString();
    }

}
