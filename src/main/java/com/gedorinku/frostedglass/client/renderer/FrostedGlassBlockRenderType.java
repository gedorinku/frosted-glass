package com.gedorinku.frostedglass.client.renderer;

import com.gedorinku.frostedglass.FrostedGlassMod;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlUtil;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL12.GL_BGRA;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_INT_8_8_8_8_REV;

public class FrostedGlassBlockRenderType {
    private static int textureID;

    public static final RenderType RENDER_TYPE = RenderType
            .create(FrostedGlassMod.ID + ":frosted_glass", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 2097152, true, true, RenderType.CompositeState.builder()
                    .setLightmapState(new RenderStateShard.LightmapStateShard(true))
                    .setShaderState(new RenderStateShard.ShaderStateShard(() -> FrostedGlassMod.FROSTED_GLASS_BLOCK_ENTITY_SHADER))
                    .setTextureState(new RenderStateShard.EmptyTextureStateShard(() -> {
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
                    }, () -> {
                        TextureUtil.releaseTextureId(textureID);
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
}
