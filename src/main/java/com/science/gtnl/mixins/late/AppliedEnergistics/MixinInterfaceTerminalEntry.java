package com.science.gtnl.mixins.late.AppliedEnergistics;

import java.util.ArrayList;
import java.util.List;

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

        final String extraStart = "extra_start_";
        final String extraEnd = "_extra_end_";

        List<String> extraKeys = new ArrayList<>();

        while (afterPrefix.startsWith(extraStart)) {
            int endIdx = afterPrefix.indexOf(extraEnd);
            if (endIdx <= extraStart.length()) {
                break;
            }

            String extraKey = afterPrefix.substring(extraStart.length(), endIdx);
            extraKeys.add(extraKey);

            afterPrefix = afterPrefix.substring(endIdx + extraEnd.length());
        }

        String mainKey = afterPrefix;

        String mainText;
        if (StatCollector.canTranslate(mainKey)) {
            mainText = StatCollector.translateToLocal(mainKey);
        } else if (StatCollector.canTranslate(mainKey + ".name")) {
            mainText = StatCollector.translateToLocal(mainKey + ".name");
        } else {
            mainText = StatCollector.translateToFallback(mainKey);
        }

        List<String> extraTexts = new ArrayList<>();
        for (String extraKey : extraKeys) {
            if (StatCollector.canTranslate(extraKey)) {
                extraTexts.add(StatCollector.translateToLocal(extraKey));
            } else if (StatCollector.canTranslate(extraKey + ".name")) {
                extraTexts.add(StatCollector.translateToLocal(extraKey + ".name"));
            } else {
                extraTexts.add(StatCollector.translateToFallback(extraKey));
            }
        }

        StringBuilder sb = new StringBuilder(mainText);

        if (numberPart != null) {
            sb.append(" - ")
                .append(numberPart);
        }

        for (String extraText : extraTexts) {
            sb.append(" - ")
                .append(extraText);
        }

        this.dispName = sb.toString();
    }

}
