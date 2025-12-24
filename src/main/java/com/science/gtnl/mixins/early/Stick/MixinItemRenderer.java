package com.science.gtnl.mixins.early.Stick;

import static net.minecraft.client.renderer.ItemRenderer.*;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.item.items.Stick;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {

    @Shadow
    private Minecraft mc;

    @Shadow
    private RenderBlocks renderBlocksIr;

    @Final
    @Shadow
    private static ResourceLocation RES_ITEM_GLINT;

    @Inject(
        method = "renderItem(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/item/ItemStack;ILnet/minecraftforge/client/IItemRenderer$ItemRenderType;)V",
        at = @At("HEAD"),
        cancellable = true,
        remap = false)
    public void renderItem(EntityLivingBase entityLivingBase, ItemStack itemStack, int p_78443_3_,
        IItemRenderer.ItemRenderType type, CallbackInfo ci) {
        Item item = itemStack.getItem();
        Block block = Block.getBlockFromItem(item);

        if (!(item instanceof Stick stick)) return;
        if (stick.isShiftDown()) return;

        GL11.glPushMatrix();
        TextureManager texturemanager = this.mc.getTextureManager();

        ItemStack fakeItemStack;
        fakeItemStack = Stick.getDisguisedStack(itemStack);
        if (fakeItemStack != null) {
            item = fakeItemStack.getItem();
            block = Block.getBlockFromItem(item);
        }

        if (block != null && block.getRenderBlockPass() != 0) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_CULL_FACE);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        }
        IItemRenderer customRenderer = MinecraftForgeClient
            .getItemRenderer(fakeItemStack != null ? fakeItemStack : itemStack, type);
        if (customRenderer != null) {
            texturemanager.bindTexture(
                texturemanager.getResourceLocation(
                    fakeItemStack != null ? fakeItemStack.getItemSpriteNumber() : itemStack.getItemSpriteNumber()));
            ForgeHooksClient.renderEquippedItem(
                type,
                customRenderer,
                renderBlocksIr,
                entityLivingBase,
                fakeItemStack != null ? fakeItemStack : itemStack);
        } else if ((fakeItemStack != null ? fakeItemStack.getItemSpriteNumber() : itemStack.getItemSpriteNumber()) == 0
            && item instanceof ItemBlock
            && RenderBlocks.renderItemIn3d(block.getRenderType())) {
                texturemanager.bindTexture(texturemanager.getResourceLocation(0));
                if (block.getRenderBlockPass() != 0) {
                    GL11.glDepthMask(false);
                    this.renderBlocksIr.renderBlockAsItem(block, fakeItemStack.getItemDamage(), 1.0F);
                    GL11.glDepthMask(true);
                } else {
                    this.renderBlocksIr.renderBlockAsItem(block, fakeItemStack.getItemDamage(), 1.0F);
                }
            } else {
                IIcon iicon = entityLivingBase
                    .getItemIcon(fakeItemStack != null ? fakeItemStack : itemStack, p_78443_3_);

                if (iicon == null) {
                    GL11.glPopMatrix();
                    return;
                }

                texturemanager.bindTexture(
                    texturemanager.getResourceLocation(
                        fakeItemStack != null ? fakeItemStack.getItemSpriteNumber() : itemStack.getItemSpriteNumber()));
                TextureUtil.func_152777_a(false, false, 1.0F);
                Tessellator tessellator = Tessellator.instance;
                float f = iicon.getMinU();
                float f1 = iicon.getMaxU();
                float f2 = iicon.getMinV();
                float f3 = iicon.getMaxV();
                float f4 = 0.0F;
                float f5 = 0.3F;
                GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                GL11.glTranslatef(-f4, -f5, 0.0F);
                float f6 = 1.5F;
                GL11.glScalef(f6, f6, f6);
                GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
                GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F);
                renderItemIn2D(tessellator, f1, f2, f, f3, iicon.getIconWidth(), iicon.getIconHeight(), 0.0625F);

                if (fakeItemStack != null ? fakeItemStack.hasEffect(p_78443_3_) : itemStack.hasEffect(p_78443_3_)) {
                    GL11.glDepthFunc(GL11.GL_EQUAL);
                    GL11.glDisable(GL11.GL_LIGHTING);
                    texturemanager.bindTexture(RES_ITEM_GLINT);
                    GL11.glEnable(GL11.GL_BLEND);
                    OpenGlHelper.glBlendFunc(768, 1, 1, 0);
                    float f7 = 0.76F;
                    GL11.glColor4f(0.5F * f7, 0.25F * f7, 0.8F * f7, 1.0F);
                    GL11.glMatrixMode(GL11.GL_TEXTURE);
                    GL11.glPushMatrix();
                    float f8 = 0.125F;
                    GL11.glScalef(f8, f8, f8);
                    float f9 = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
                    GL11.glTranslatef(f9, 0.0F, 0.0F);
                    GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                    renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
                    GL11.glPopMatrix();
                    GL11.glPushMatrix();
                    GL11.glScalef(f8, f8, f8);
                    f9 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
                    GL11.glTranslatef(-f9, 0.0F, 0.0F);
                    GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                    renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
                    GL11.glPopMatrix();
                    GL11.glMatrixMode(GL11.GL_MODELVIEW);
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glEnable(GL11.GL_LIGHTING);
                    GL11.glDepthFunc(GL11.GL_LEQUAL);
                }

                GL11.glDisable(GL12.GL_RESCALE_NORMAL);
                texturemanager.bindTexture(
                    texturemanager.getResourceLocation(
                        fakeItemStack != null ? fakeItemStack.getItemSpriteNumber() : itemStack.getItemSpriteNumber()));
                TextureUtil.func_147945_b();
            }

        if (block != null && block.getRenderBlockPass() != 0) {
            GL11.glDisable(GL11.GL_BLEND);
        }

        GL11.glPopMatrix();
        ci.cancel();
    }
}
