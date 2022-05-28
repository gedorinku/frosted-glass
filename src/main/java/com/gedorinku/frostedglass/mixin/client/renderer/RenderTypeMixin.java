package com.gedorinku.frostedglass.mixin.client.renderer;

import com.gedorinku.frostedglass.client.renderer.FrostedGlassBlockRenderer;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(RenderType.class)
public abstract class RenderTypeMixin {

    @Inject(method = "chunkBufferLayers", at = @At("RETURN"), cancellable = true)
    private static void chunkBufferLayers(CallbackInfoReturnable<List<RenderType>> cir) {
        var original =  new ArrayList<>(cir.getReturnValue());
        original.add(FrostedGlassBlockRenderer.RENDER_TYPE);
        cir.setReturnValue(ImmutableList.copyOf(original));
    }
}
