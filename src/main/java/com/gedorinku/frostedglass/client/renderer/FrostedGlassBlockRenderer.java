package com.gedorinku.frostedglass.client.renderer;

import com.gedorinku.frostedglass.FrostedGlassMod;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlUtil;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL12.GL_BGRA;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_INT_8_8_8_8_REV;

public class FrostedGlassBlockRenderer {
    private static int textureID;
    public static final RenderType RENDER_TYPE = RenderType
            .create(FrostedGlassMod.ID + ":frosted_glass", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 2097152, true, true, RenderType.CompositeState.builder()
                    .setLightmapState(new RenderStateShard.LightmapStateShard(true))
                    .setShaderState(new RenderStateShard.ShaderStateShard(() -> FrostedGlassMod.RENDER_TYPE_FROSTED_GLASS_SHADER))
                    .setTextureState(new RenderStateShard.EmptyTextureStateShard(() -> {
                        Minecraft.getInstance().getProfiler().push(FrostedGlassMod.ID + ":setTextureStateBeforeRender");

                        RenderSystem.enableTexture();

                        var height = Minecraft.getInstance().getWindow().getHeight();
                        var width = Minecraft.getInstance().getWindow().getWidth();
                        ByteBuffer bytebuffer = GlUtil.allocateMemory(width * height * 4 * 4);
                        RenderSystem.readPixels(0, 0, width, height, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, bytebuffer);
                        textureID = GlStateManager._genTexture(); // TODO: 毎回 allocate しない
                        RenderSystem.bindTextureForSetup(textureID);
                        TextureUtil.initTexture(bytebuffer.asIntBuffer(), width, height);
                        RenderSystem.setShaderTexture(1, textureID);
                        GlUtil.freeMemory(bytebuffer);

                        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
                        textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, true);
                        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);

                        Minecraft.getInstance().getProfiler().pop();
                    }, () -> {
                        Minecraft.getInstance().getProfiler().push(FrostedGlassMod.ID + ":setTextureStateAfterRender");

                        TextureUtil.releaseTextureId(textureID);

                        Minecraft.getInstance().getProfiler().pop();
                    }))
                    .setTransparencyState(new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
                        RenderSystem.enableBlend();
                        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                    }, () -> {
                        RenderSystem.disableBlend();
                        RenderSystem.defaultBlendFunc();
                    })).setOutputState(new RenderStateShard.OutputStateShard("translucent_target", () -> {
                        if (Minecraft.useShaderTransparency()) {
                            Minecraft.getInstance().levelRenderer.getTranslucentTarget().bindWrite(false);
                        }

                    }, () -> {
                        if (Minecraft.useShaderTransparency()) {
                            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
                        }

                    })).createCompositeState(true));

    public void renderFrostedGlassChunkLayer(
            ObjectArrayList<LevelRenderer.RenderChunkInfo> renderChunksInFrustum,
            PoseStack poseStack,
            double cameraX,
            double cameraY,
            double cameraZ,
            Matrix4f projection
    ) {
        Minecraft.getInstance().getProfiler().push(FrostedGlassMod.ID + ":frostedGlassChunkLayer");

        RenderSystem.assertOnRenderThread();

        renderFrostedGlassBlocksAs(RenderType.cutout(), renderChunksInFrustum, poseStack, cameraX, cameraY, cameraZ, projection);
        renderFrostedGlassBlocksAs(RENDER_TYPE, renderChunksInFrustum, poseStack, cameraX, cameraY, cameraZ, projection);

        Minecraft.getInstance().getProfiler().pop();
    }

    private void renderFrostedGlassBlocksAs(
            RenderType renderType,
            ObjectArrayList<LevelRenderer.RenderChunkInfo> renderChunksInFrustum,
            PoseStack poseStack,
            double cameraX,
            double cameraY,
            double cameraZ,
            Matrix4f projection
    ) {
        ObjectListIterator<LevelRenderer.RenderChunkInfo> objectlistiterator = renderChunksInFrustum.listIterator(renderChunksInFrustum.size());
        ShaderInstance shaderinstance = RenderSystem.getShader();

        Uniform uniform = shaderinstance.CHUNK_OFFSET;

        setupRenderState(renderType, poseStack, projection);

        while (true) {
            if (!objectlistiterator.hasPrevious()) {
                break;
            }

            LevelRenderer.RenderChunkInfo levelrenderer$renderchunkinfo1 = objectlistiterator.previous();
            ChunkRenderDispatcher.RenderChunk chunkrenderdispatcher$renderchunk = levelrenderer$renderchunkinfo1.chunk;
            if (!chunkrenderdispatcher$renderchunk.getCompiledChunk().isEmpty(RENDER_TYPE)) {
                VertexBuffer vertexbuffer = chunkrenderdispatcher$renderchunk.getBuffer(RENDER_TYPE);
                BlockPos blockpos = chunkrenderdispatcher$renderchunk.getOrigin();
                if (uniform != null) {
                    uniform.set((float) ((double) blockpos.getX() - cameraX), (float) ((double) blockpos.getY() - cameraY), (float) ((double) blockpos.getZ() - cameraZ));
                    uniform.upload();
                }

                vertexbuffer.drawChunkLayer();
            }
        }

        shaderinstance.clear();
        VertexBuffer.unbind();
        VertexBuffer.unbindVertexArray();
        renderType.clearRenderState();

        if (uniform != null) {
            uniform.set(Vector3f.ZERO);
        }
    }

    private void setupRenderState(RenderType renderType, PoseStack poseStack, Matrix4f projection) {
        renderType.setupRenderState();

        ShaderInstance shaderinstance = RenderSystem.getShader();
        BufferUploader.reset();

        for (int k = 0; k < 12; ++k) {
            int i = RenderSystem.getShaderTexture(k);
            shaderinstance.setSampler("Sampler" + k, i);
        }

        if (shaderinstance.MODEL_VIEW_MATRIX != null) {
            shaderinstance.MODEL_VIEW_MATRIX.set(poseStack.last().pose());
        }

        if (shaderinstance.PROJECTION_MATRIX != null) {
            shaderinstance.PROJECTION_MATRIX.set(projection);
        }

        if (shaderinstance.COLOR_MODULATOR != null) {
            shaderinstance.COLOR_MODULATOR.set(RenderSystem.getShaderColor());
        }

        if (shaderinstance.FOG_START != null) {
            shaderinstance.FOG_START.set(RenderSystem.getShaderFogStart());
        }

        if (shaderinstance.FOG_END != null) {
            shaderinstance.FOG_END.set(RenderSystem.getShaderFogEnd());
        }

        if (shaderinstance.FOG_COLOR != null) {
            shaderinstance.FOG_COLOR.set(RenderSystem.getShaderFogColor());
        }

        if (shaderinstance.FOG_SHAPE != null) {
            shaderinstance.FOG_SHAPE.set(RenderSystem.getShaderFogShape().getIndex());
        }

        if (shaderinstance.TEXTURE_MATRIX != null) {
            shaderinstance.TEXTURE_MATRIX.set(RenderSystem.getTextureMatrix());
        }

        if (shaderinstance.GAME_TIME != null) {
            shaderinstance.GAME_TIME.set(RenderSystem.getShaderGameTime());
        }

        RenderSystem.setupShaderLights(shaderinstance);
        shaderinstance.apply();
    }
}
