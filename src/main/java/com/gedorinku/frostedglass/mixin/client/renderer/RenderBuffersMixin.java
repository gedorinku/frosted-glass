package com.gedorinku.frostedglass.mixin.client.renderer;

import com.gedorinku.frostedglass.client.renderer.FrostedGlassBlockRenderer;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.SortedMap;

@Mixin(RenderBuffers.class)
public class RenderBuffersMixin {
    @Shadow
    @Final
    private SortedMap<RenderType, BufferBuilder> fixedBuffers;

    @Shadow
    @Final
    private ChunkBufferBuilderPack fixedBufferPack;

    @Shadow
    protected MultiBufferSource.BufferSource bufferSource;

    @Inject(method = "<init>()V", at = @At("RETURN"))
    private void constructor(CallbackInfo ci) {
        this.fixedBuffers.put(
                FrostedGlassBlockRenderer.RENDER_TYPE_ITEM_ENTITY,
                this.fixedBufferPack.builder(FrostedGlassBlockRenderer.RENDER_TYPE)
        );
        this.bufferSource = MultiBufferSource.immediateWithBuffers(this.fixedBuffers, new BufferBuilder(256));
    }
}
