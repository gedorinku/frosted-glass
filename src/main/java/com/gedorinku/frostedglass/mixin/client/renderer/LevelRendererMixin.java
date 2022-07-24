package com.gedorinku.frostedglass.mixin.client.renderer;

import com.gedorinku.frostedglass.FrostedGlassMod;
import com.gedorinku.frostedglass.client.renderer.FrostedGlassBlockRenderer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Shadow
    @Final
    private ObjectArrayList<LevelRenderer.RenderChunkInfo> renderChunksInFrustum;

    @Inject(method = "renderChunkLayer", at = @At("RETURN"))
    private void renderChunkLayer(RenderType p_172994_, PoseStack p_172995_, double p_172996_, double p_172997_, double p_172998_, Matrix4f p_172999_, CallbackInfo ci) {
        if (p_172994_ != RenderType.translucent()) {
            return;
        }

        renderFrostedGlassChunkLayer(this.renderChunksInFrustum, p_172995_, p_172996_, p_172997_, p_172998_, p_172999_);
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

        for (var dir : new FrostedGlassBlockRenderer.BlurDirection[]{FrostedGlassBlockRenderer.BlurDirection.VERTICAL, FrostedGlassBlockRenderer.BlurDirection.HORIZONTAL}) {
            renderFrostedGlassBlocksAs(FrostedGlassBlockRenderer.RENDER_TYPE, renderChunksInFrustum, poseStack, cameraX, cameraY, cameraZ, projection, dir);
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
            @Nullable FrostedGlassBlockRenderer.BlurDirection blurDirection
    ) {
        setupFrostedGlassRenderState(renderType, poseStack, projection, blurDirection);

        ObjectListIterator<LevelRenderer.RenderChunkInfo> objectlistiterator = renderChunksInFrustum.listIterator(renderChunksInFrustum.size());
        ShaderInstance shaderinstance = RenderSystem.getShader();

        var chunkOffsetUniform = shaderinstance.CHUNK_OFFSET;

        while (true) {
            if (!objectlistiterator.hasPrevious()) {
                break;
            }

            LevelRenderer.RenderChunkInfo levelrenderer$renderchunkinfo1 = objectlistiterator.previous();
            ChunkRenderDispatcher.RenderChunk chunkrenderdispatcher$renderchunk = levelrenderer$renderchunkinfo1.chunk;
            if (!chunkrenderdispatcher$renderchunk.getCompiledChunk().isEmpty(FrostedGlassBlockRenderer.RENDER_TYPE)) {
                VertexBuffer vertexbuffer = chunkrenderdispatcher$renderchunk.getBuffer(FrostedGlassBlockRenderer.RENDER_TYPE);
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

        FrostedGlassBlockRenderer.setBlurDirection(shaderinstance, FrostedGlassBlockRenderer.BlurDirection.VERTICAL_AND_HORIZONTAL);
    }

    private void setupFrostedGlassRenderState(RenderType renderType, PoseStack poseStack, Matrix4f projection, @Nullable FrostedGlassBlockRenderer.BlurDirection blurDirection) {
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

        FrostedGlassBlockRenderer.setWindowSize(shaderinstance);

        FrostedGlassBlockRenderer.setBlurDirection(shaderinstance, blurDirection);

        RenderSystem.setupShaderLights(shaderinstance);
        shaderinstance.apply();
    }
}
