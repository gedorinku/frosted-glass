package com.gedorinku.frostedglass.mixin.client.renderer;

import com.gedorinku.frostedglass.FrostedGlassMod;
import com.gedorinku.frostedglass.client.renderer.FrostedGlassBlockRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Shadow
    public abstract void render(ItemStack p_115144_, ItemTransforms.TransformType p_115145_, boolean p_115146_, PoseStack p_115147_, MultiBufferSource p_115148_, int p_115149_, int p_115150_, BakedModel p_115151_);

    @Shadow
    public abstract BakedModel getModel(ItemStack p_174265_, @org.jetbrains.annotations.Nullable Level p_174266_, @org.jetbrains.annotations.Nullable LivingEntity p_174267_, int p_174268_);

    @Inject(
            method = "renderStatic(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;III)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void renderStatic(
            @Nullable LivingEntity livingEntity,
            ItemStack itemStack,
            ItemTransforms.TransformType transformType,
            boolean p_174246_,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            Level level,
            int p_174250_,
            int p_174251_,
            int p_174252_,
            CallbackInfo ci
    ) {
        RenderType renderType = ItemBlockRenderTypes.getRenderType(itemStack, false);
        if (renderType != FrostedGlassBlockRenderer.RENDER_TYPE_ITEM_ENTITY) {
            return;
        }

        var bakedModel = getModel(itemStack, level, livingEntity, p_174252_);
        var shaderInstance = FrostedGlassMod.RENDER_TYPE_ITEM_ENTITY_FROSTED_GLASS_SHADER;
        FrostedGlassBlockRenderer.setWindowSize(shaderInstance);

        for (var dir : new FrostedGlassBlockRenderer.BlurDirection[]{FrostedGlassBlockRenderer.BlurDirection.VERTICAL, FrostedGlassBlockRenderer.BlurDirection.HORIZONTAL}) {
            FrostedGlassBlockRenderer.setBlurDirection(shaderInstance, dir);
            render(itemStack, transformType, p_174246_, poseStack, multiBufferSource, p_174250_, p_174251_, bakedModel);

            if (multiBufferSource instanceof MultiBufferSource.BufferSource) {
                ((MultiBufferSource.BufferSource) multiBufferSource).endBatch(FrostedGlassBlockRenderer.RENDER_TYPE_ITEM_ENTITY);
            }
        }

        FrostedGlassBlockRenderer.setBlurDirection(shaderInstance, FrostedGlassBlockRenderer.BlurDirection.VERTICAL_AND_HORIZONTAL);

        ci.cancel();
    }
}
