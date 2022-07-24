package com.gedorinku.frostedglass.mixin.client.renderer;

import com.gedorinku.frostedglass.client.renderer.FrostedGlassBlockRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemBlockRenderTypes.class)
public abstract class ItemBlockRenderTypesMixin {
    @Inject(
            method = "getRenderType(Lnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/client/renderer/RenderType;",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void getRenderType(BlockState blockState, boolean flag, CallbackInfoReturnable<RenderType> cir) {
        if (!ItemBlockRenderTypes.canRenderInLayer(blockState, FrostedGlassBlockRenderer.RENDER_TYPE)) {
            return;
        }

        cir.setReturnValue(FrostedGlassBlockRenderer.RENDER_TYPE_ITEM_ENTITY);
        cir.cancel();
    }
}
