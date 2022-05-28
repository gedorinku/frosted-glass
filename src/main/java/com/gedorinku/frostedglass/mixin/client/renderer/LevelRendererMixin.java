package com.gedorinku.frostedglass.mixin.client.renderer;

import com.gedorinku.frostedglass.client.renderer.FrostedGlassBlockRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Shadow
    @Final
    private ObjectArrayList<LevelRenderer.RenderChunkInfo> renderChunksInFrustum;

    private final FrostedGlassBlockRenderer frostedGlassBlockRenderer = new FrostedGlassBlockRenderer();

    @Inject(method = "renderChunkLayer", at = @At("RETURN"))
    private void renderChunkLayer(RenderType p_172994_, PoseStack p_172995_, double p_172996_, double p_172997_, double p_172998_, Matrix4f p_172999_, CallbackInfo ci) {
        if (p_172994_ != RenderType.translucent()) {
            return;
        }

        frostedGlassBlockRenderer.renderFrostedGlassChunkLayer(this.renderChunksInFrustum, p_172995_, p_172996_, p_172997_, p_172998_, p_172999_);
    }
}
