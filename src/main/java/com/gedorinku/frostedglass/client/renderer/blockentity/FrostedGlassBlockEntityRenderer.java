package com.gedorinku.frostedglass.client.renderer.blockentity;

import com.gedorinku.frostedglass.FrostedGlassMod;
import com.gedorinku.frostedglass.block.entity.FrostedGlassBlockEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlUtil;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

import java.nio.ByteBuffer;

public class FrostedGlassBlockEntityRenderer implements BlockEntityRenderer<FrostedGlassBlockEntity> {
    private static final RenderType RENDER_TYPE = RenderType
            .create(FrostedGlassMod.ID + ":frosted_glass", DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS, 256, false, false, RenderType.CompositeState
                    .builder()
                    .setLightmapState(new RenderStateShard.LightmapStateShard(true))
                    //.setCullState(new RenderStateShard.CullStateShard(true))
                    //.setDepthTestState(new RenderStateShard.DepthTestStateShard("always", 519))
                    .setShaderState(new RenderStateShard.ShaderStateShard(() -> FrostedGlassMod.FROSTED_GLASS_BLOCK_ENTITY_SHADER))
                    .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation("textures/block/glass.png"), false, true))
//                    .setTextureState(new RenderStateShard.EmptyTextureStateShard(() -> {
//                        RenderSystem.enableTexture();
////                        TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
////                        texturemanager.getTexture(p_110333_).setFilter(p_110334_, p_110335_);
////                        RenderSystem.setShaderTexture(0, p_110333_);
//                        ByteBuffer bytebuffer = GlUtil.allocateMemory(128 * 128 * 4);
//                        RenderSystem.readPixels(0, 0, 128, 128, 32992, 5121, bytebuffer);
//                        int id = GlStateManager._genTexture();
//                        RenderSystem.bindTextureForSetup(id);
//                        TextureUtil.initTexture(bytebuffer.asIntBuffer(), 128, 128);
//                        RenderSystem.setShaderTexture(0, id);
//                    }, () -> {
//                    }))
                    .setTransparencyState(new RenderStateShard.TransparencyStateShard("additive_transparency", () -> {
                        RenderSystem.enableBlend();
                        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                    }, () -> {
                        RenderSystem.disableBlend();
                        RenderSystem.defaultBlendFunc();
                    }))
                    .createCompositeState(false));

    public FrostedGlassBlockEntityRenderer(BlockEntityRendererProvider.Context renderer) {
    }

    @Override
    public void render(FrostedGlassBlockEntity blockEntity, float partialTicks, PoseStack ps, MultiBufferSource buffer, int light, int overlay) {
        Matrix4f matrix4f = ps.last().pose();
        this.renderCube(blockEntity, matrix4f, buffer.getBuffer(this.renderType()));
    }

    private void renderCube(FrostedGlassBlockEntity p_173691_, Matrix4f p_173692_, VertexConsumer p_173693_) {
        float $$3 = this.getOffsetDown();
        float $$4 = this.getOffsetUp();
        this.renderFace(p_173691_, p_173692_, p_173693_, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, Direction.SOUTH);
        this.renderFace(p_173691_, p_173692_, p_173693_, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Direction.NORTH);
        this.renderFace(p_173691_, p_173692_, p_173693_, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.EAST);
        this.renderFace(p_173691_, p_173692_, p_173693_, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.WEST);
        this.renderFace(p_173691_, p_173692_, p_173693_, 0.0F, 1.0F, $$3, $$3, 0.0F, 0.0F, 1.0F, 1.0F, Direction.DOWN);
        this.renderFace(p_173691_, p_173692_, p_173693_, 0.0F, 1.0F, $$4, $$4, 1.0F, 1.0F, 0.0F, 0.0F, Direction.UP);
    }

    private void renderFace(FrostedGlassBlockEntity p_173695_, Matrix4f p_173696_, VertexConsumer p_173697_, float p_173698_, float p_173699_, float p_173700_, float p_173701_, float p_173702_, float p_173703_, float p_173704_, float p_173705_, Direction p_173706_) {
        if (p_173695_.shouldRenderFace(p_173706_)) {
            p_173697_.vertex(p_173696_, p_173698_, p_173700_, p_173702_).endVertex();
            p_173697_.vertex(p_173696_, p_173699_, p_173700_, p_173703_).endVertex();
            p_173697_.vertex(p_173696_, p_173699_, p_173701_, p_173704_).endVertex();
            p_173697_.vertex(p_173696_, p_173698_, p_173701_, p_173705_).endVertex();
        }

    }

    protected float getOffsetUp() {
        return 0.75F;
    }

    protected float getOffsetDown() {
        return 0.375F;
    }

    protected RenderType renderType() {
        return RENDER_TYPE;
    }
}
