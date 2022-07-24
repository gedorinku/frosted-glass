package com.gedorinku.frostedglass.client.renderer;

import com.gedorinku.frostedglass.FrostedGlassMod;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;

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
        RenderSystem.setShaderTexture(5, textureID);

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
            .create(FrostedGlassMod.ID + ":item_entity_frosted_glass", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, false,
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

    public static void setBlurDirection(ShaderInstance shaderInstance, BlurDirection blurDirection) {
        var blurDirectionUniform = shaderInstance.getUniform(BLUR_DIRECTION);
        if (blurDirectionUniform != null && blurDirection != null) {
            blurDirectionUniform.set(blurDirection.shaderEnum);
        }
    }

    public static void setWindowSize(ShaderInstance shaderInstance) {
        var windowSizeUniform = shaderInstance.getUniform(WINDOW_SIZE);
        if (windowSizeUniform != null) {
            var width = Minecraft.getInstance().getWindow().getWidth();
            var height = Minecraft.getInstance().getWindow().getHeight();
            windowSizeUniform.set(width, height);
        }
    }
}
