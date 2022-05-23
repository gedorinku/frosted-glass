package com.gedorinku.frostedglass.mixin.client.renderer;

import com.gedorinku.frostedglass.client.renderer.FrostedGlassBlockRenderType;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
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
    protected abstract void renderChunkLayer(RenderType p_172994_, PoseStack p_172995_, double p_172996_, double p_172997_, double p_172998_, Matrix4f p_172999_);

    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    private double xTransparentOld;
    @Shadow
    private double yTransparentOld;
    @Shadow
    private double zTransparentOld;

    @Shadow
    @Final
    private ObjectArrayList<LevelRenderer.RenderChunkInfo> renderChunksInFrustum;

    @Shadow
    @Nullable
    private ChunkRenderDispatcher chunkRenderDispatcher;

    @Inject(method = "renderChunkLayer", at = @At("RETURN"))
    private void renderChunkLayer(RenderType p_172994_, PoseStack p_172995_, double p_172996_, double p_172997_, double p_172998_, Matrix4f p_172999_, CallbackInfo ci) {
        if (p_172994_ != RenderType.translucent()) {
            return;
        }

        renderFrostedGlassChunkLayer(FrostedGlassBlockRenderType.RENDER_TYPE, p_172995_, p_172996_, p_172997_, p_172998_, p_172999_);
    }

    private void renderFrostedGlassChunkLayer(RenderType renderType, PoseStack poseStack, double cameraX, double cameraY, double cameraZ, Matrix4f projection) {
        RenderSystem.assertOnRenderThread();

        this.minecraft.getProfiler().push("translucent_sort");
        double d0 = cameraX - this.xTransparentOld;
        double d1 = cameraY - this.yTransparentOld;
        double d2 = cameraZ - this.zTransparentOld;
        if (d0 * d0 + d1 * d1 + d2 * d2 > 1.0D) {
            this.xTransparentOld = cameraX;
            this.yTransparentOld = cameraY;
            this.zTransparentOld = cameraZ;
            int j = 0;

            for (LevelRenderer.RenderChunkInfo levelrenderer$renderchunkinfo : this.renderChunksInFrustum) {
                if (j < 15 && levelrenderer$renderchunkinfo.chunk.resortTransparency(renderType, this.chunkRenderDispatcher)) {
                    ++j;
                }
            }
        }

        this.minecraft.getProfiler().pop();

        this.minecraft.getProfiler().push("filterempty");
        this.minecraft.getProfiler().popPush(() -> {
            return "render_" + renderType;
        });
        ObjectListIterator<LevelRenderer.RenderChunkInfo> objectlistiterator = this.renderChunksInFrustum.listIterator(this.renderChunksInFrustum.size());
        VertexFormat vertexformat = renderType.format();
        ShaderInstance shaderinstance = RenderSystem.getShader();

        Uniform uniform = shaderinstance.CHUNK_OFFSET;

        while (true) {
            if (!objectlistiterator.hasPrevious()) {
                break;
            }

            LevelRenderer.RenderChunkInfo levelrenderer$renderchunkinfo1 = objectlistiterator.previous();
            ChunkRenderDispatcher.RenderChunk chunkrenderdispatcher$renderchunk = levelrenderer$renderchunkinfo1.chunk;
            if (!chunkrenderdispatcher$renderchunk.getCompiledChunk().isEmpty(renderType)) {
                setupRenderState(renderType, poseStack, projection);

                VertexBuffer vertexbuffer = chunkrenderdispatcher$renderchunk.getBuffer(renderType);
                BlockPos blockpos = chunkrenderdispatcher$renderchunk.getOrigin();
                if (uniform != null) {
                    uniform.set((float) ((double) blockpos.getX() - cameraX), (float) ((double) blockpos.getY() - cameraY), (float) ((double) blockpos.getZ() - cameraZ));
                    uniform.upload();
                }

                vertexbuffer.drawChunkLayer();

                shaderinstance.clear();
                VertexBuffer.unbind();
                VertexBuffer.unbindVertexArray();
                renderType.clearRenderState();
            }
        }

        if (uniform != null) {
            uniform.set(Vector3f.ZERO);
        }

        this.minecraft.getProfiler().pop();
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
