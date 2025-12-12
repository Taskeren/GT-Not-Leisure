package com.science.gtnl.mixins.early.Minecraft;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.management.ServerConfigurationManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.config.MainConfig;
import com.science.gtnl.loader.EffectLoader;

@Mixin(ServerConfigurationManager.class)
public abstract class MixinServerConfigurationManager {

    @Inject(method = "respawnPlayer", at = @At("RETURN"), cancellable = true)
    private void onRespawnPlayer(EntityPlayerMP player, int dimension, boolean conqueredEnd,
        CallbackInfoReturnable<EntityPlayerMP> cir) {
        if (!MainConfig.enableGhostlyShape) return;
        EntityPlayerMP entityplayermp1 = cir.getReturnValue();

        if (entityplayermp1 != null) {
            entityplayermp1.addPotionEffect(new PotionEffect(EffectLoader.ghostly_shape.id, 6000, 0));
        }

        cir.setReturnValue(entityplayermp1);
    }
}
