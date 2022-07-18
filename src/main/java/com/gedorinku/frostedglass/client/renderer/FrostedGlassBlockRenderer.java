package com.gedorinku.frostedglass.client.renderer;

import com.gedorinku.frostedglass.FrostedGlassMod;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;

import static org.lwjgl.opengl.GL21.GL_TEXTURE_2D;

public class FrostedGlassBlockRenderer {
    private static int textureID = -1;
    private static int lastWindowWidth = -1;
    private static int lastWindowHeight = -1;

    private static final String WINDOW_SIZE = "WindowSize";
    private static final String BLUR_DIRECTION = "BlurDirection";

    private static final RenderStateShard.EmptyTextureStateShard FROSTED_GLASS_TEXTURE_STATE_SHARD = new RenderStateShard.EmptyTextureStateShard(() -> {
        Minecraft.getInstance().getProfiler().push(FrostedGlassMod.ID + ":setTextureStateBeforeRender");

        RenderSystem.enableTexture();

        var width = Minecraft.getInstance().getWindow().getWidth();
        var height = Minecraft.getInstance().getWindow().getHeight();
        if (lastWindowWidth != width || lastWindowHeight != height) {
            initializeTexture(width, height);
            lastWindowWidth = width;
            lastWindowHeight = height;
        }

        RenderSystem.bindTextureForSetup(textureID);
        Minecraft.getInstance().getProfiler().push(FrostedGlassMod.ID + ":glCopyTexSubImage2D");
        GlStateManager._glCopyTexSubImage2D(
                GL_TEXTURE_2D,
                0,
                0,
                0,
                0,
                0,
                width,
                height
        );
        Minecraft.getInstance().getProfiler().pop();
        RenderSystem.setShaderTexture(1, textureID);

        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, true);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);

        Minecraft.getInstance().getProfiler().pop();
    }, () -> {
    });

    public static final RenderType RENDER_TYPE = RenderType
            .create(FrostedGlassMod.ID + ":frosted_glass", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 2097152, true, true, RenderType.CompositeState.builder()
                    .setLightmapState(new RenderStateShard.LightmapStateShard(true))
                    .setShaderState(new RenderStateShard.ShaderStateShard(() -> FrostedGlassMod.RENDER_TYPE_FROSTED_GLASS_SHADER))
                    .setTextureState(FROSTED_GLASS_TEXTURE_STATE_SHARD)
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

    public static final RenderType RENDER_TYPE_ITEM_ENTITY = RenderType
            .create(FrostedGlassMod.ID + ":item_entity_frosted_glass", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false,
                    RenderType.CompositeState.builder()
                            .setShaderState(new RenderStateShard.ShaderStateShard(() -> FrostedGlassMod.RENDER_TYPE_ITEM_ENTITY_FROSTED_GLASS_SHADER))
                            .setTextureState(FROSTED_GLASS_TEXTURE_STATE_SHARD)
                            .setTransparencyState(new RenderStateShard.TransparencyStateShard("no_transparency", RenderSystem::disableBlend, () -> {
                            }))
                            .setLightmapState(new RenderStateShard.LightmapStateShard(true))
                            .setOverlayState(new RenderStateShard.OverlayStateShard(true))
                            .createCompositeState(true)
            );

    private static void initializeTexture(int width, int height) {
        if (textureID == -1) {
            textureID = GlStateManager._genTexture();
        }

        RenderSystem.bindTextureForSetup(textureID);
        TextureUtil.initTexture(null, width, height);
    }

    public enum BlurDirection {
        VERTICAL_AND_HORIZONTAL(0),
        VERTICAL(1),
        HORIZONTAL(2);

        public final int shaderEnum;

        BlurDirection(int shaderEnum) {
            this.shaderEnum = shaderEnum;
        }
    }

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

        for (var dir : new BlurDirection[]{BlurDirection.VERTICAL, BlurDirection.HORIZONTAL}) {
            renderFrostedGlassBlocksAs(RENDER_TYPE, renderChunksInFrustum, poseStack, cameraX, cameraY, cameraZ, projection, dir);
        }

        Minecraft.getInstance().getProfiler().pop();
    }

    private void renderFrostedGlassBlocksAs(
            RenderType renderType,
            ObjectArrayList<LevelRenderer.RenderChunkInfo> renderChunksInFrustum,
            PoseStack poseStack,
            double cameraX,
            double cameraY,
            double cameraZ,
            Matrix4f projection,
            @Nullable BlurDirection blurDirection
    ) {
        setupRenderState(renderType, poseStack, projection, blurDirection);

        ObjectListIterator<LevelRenderer.RenderChunkInfo> objectlistiterator = renderChunksInFrustum.listIterator(renderChunksInFrustum.size());
        ShaderInstance shaderinstance = RenderSystem.getShader();

        var chunkOffsetUniform = shaderinstance.CHUNK_OFFSET;

        while (true) {
            if (!objectlistiterator.hasPrevious()) {
                break;
            }

            LevelRenderer.RenderChunkInfo levelrenderer$renderchunkinfo1 = objectlistiterator.previous();
            ChunkRenderDispatcher.RenderChunk chunkrenderdispatcher$renderchunk = levelrenderer$renderchunkinfo1.chunk;
            if (!chunkrenderdispatcher$renderchunk.getCompiledChunk().isEmpty(RENDER_TYPE)) {
                VertexBuffer vertexbuffer = chunkrenderdispatcher$renderchunk.getBuffer(RENDER_TYPE);
                BlockPos blockpos = chunkrenderdispatcher$renderchunk.getOrigin();
                if (chunkOffsetUniform != null) {
                    chunkOffsetUniform.set((float) ((double) blockpos.getX() - cameraX), (float) ((double) blockpos.getY() - cameraY), (float) ((double) blockpos.getZ() - cameraZ));
                    chunkOffsetUniform.upload();
                }

                vertexbuffer.drawChunkLayer();
            }
        }

        shaderinstance.clear();
        VertexBuffer.unbind();
        VertexBuffer.unbindVertexArray();
        renderType.clearRenderState();

        if (chunkOffsetUniform != null) {
            chunkOffsetUniform.set(Vector3f.ZERO);
        }

        setBlurDirection(shaderinstance, BlurDirection.VERTICAL_AND_HORIZONTAL);
    }

    private void setupRenderState(RenderType renderType, PoseStack poseStack, Matrix4f projection, @Nullable BlurDirection blurDirection) {
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

        var windowSizeUniform = shaderinstance.getUniform(WINDOW_SIZE);
        if (windowSizeUniform != null) {
            var width = Minecraft.getInstance().getWindow().getWidth();
            var height = Minecraft.getInstance().getWindow().getHeight();
            windowSizeUniform.set(width, height);
        }

        setBlurDirection(shaderinstance, blurDirection);

        RenderSystem.setupShaderLights(shaderinstance);
        shaderinstance.apply();
    }

    public static void setBlurDirection(ShaderInstance shaderInstance, BlurDirection blurDirection) {
        var blurDirectionUniform = shaderInstance.getUniform(BLUR_DIRECTION);
        if (blurDirectionUniform != null && blurDirection != null) {
            blurDirectionUniform.set(blurDirection.shaderEnum);
        }
    }
}
