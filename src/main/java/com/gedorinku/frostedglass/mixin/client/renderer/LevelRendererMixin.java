package com.gedorinku.frostedglass.mixin.client.renderer;

import com.gedorinku.frostedglass.client.renderer.FrostedGlassBlockRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Shadow protected abstract void renderChunkLayer(RenderType p_172994_, PoseStack p_172995_, double p_172996_, double p_172997_, double p_172998_, Matrix4f p_172999_);

    @Inject(method = "renderChunkLayer", at = @At("RETURN"))
    private void renderChunkLayer(RenderType renderType, PoseStack p_172995_, double cameraX, double cameraY, double cameraZ, Matrix4f p_172999_, CallbackInfo ci) {
        if (renderType != RenderType.translucent()) {
            return;
        }

        renderChunkLayer(FrostedGlassBlockRenderType.RENDER_TYPE, p_172995_, cameraX, cameraY, cameraZ, p_172999_);
    }
}
