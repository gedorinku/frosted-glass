package com.gedorinku.frostedglass.client.renderer.blockentity;

import com.gedorinku.frostedglass.FrostedGlassMod;
import com.gedorinku.frostedglass.block.entity.FrostedGlassBlockEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlUtil;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.lwjgl.opengl.GL12.GL_BGRA;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_INT_8_8_8_8_REV;

public class FrostedGlassBlockEntityRenderer implements BlockEntityRenderer<FrostedGlassBlockEntity> {
    private static int textureID;
    private static final RenderType RENDER_TYPE = RenderType
            .create(FrostedGlassMod.ID + ":frosted_glass", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 2097152, true, true, RenderType.CompositeState.builder()
                    .setLightmapState(new RenderStateShard.LightmapStateShard(true))
                    .setShaderState(new RenderStateShard.ShaderStateShard(() -> FrostedGlassMod.FROSTED_GLASS_BLOCK_ENTITY_SHADER))
//                    .setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation( "textures/block/glass.png"), false, true))
                    .setTextureState(new RenderStateShard.EmptyTextureStateShard(() -> {
                        RenderSystem.enableTexture();

                        var height = Minecraft.getInstance().getWindow().getHeight();
                        var width = Minecraft.getInstance().getWindow().getWidth();
                        ByteBuffer bytebuffer = GlUtil.allocateMemory(width * height * 4 * 4);
                        RenderSystem.readPixels(0, 0, width, height, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, bytebuffer);
                        textureID = GlStateManager._genTexture(); // TODO: 毎回 allocate しない
                        RenderSystem.bindTextureForSetup(textureID);
                        TextureUtil.initTexture(bytebuffer.asIntBuffer(), width, height);
                        RenderSystem.setShaderTexture(0, textureID);
                        GlUtil.freeMemory(bytebuffer);
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

    private final BlockRenderDispatcher blockRenderer;

    public FrostedGlassBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(FrostedGlassBlockEntity blockEntity, float partialTicks, PoseStack ps, MultiBufferSource buffer, int light, int overlay) {
        Level level = blockEntity.getLevel();
        if (level == null) {
            return;
        }

        VertexConsumer vc = buffer.getBuffer(this.renderType());
        long seed = blockEntity.getBlockState().getSeed(blockEntity.getBlockPos());
        var bakedModel = new SimpleBakedBlockModelWithoutAtlas((SimpleBakedModel) this.blockRenderer.getBlockModel(blockEntity.getBlockState()));
        blockRenderer.getModelRenderer().tesselateBlock(level, bakedModel, blockEntity.getBlockState(), blockEntity.getBlockPos(), ps, vc, true, new Random(), seed, OverlayTexture.NO_OVERLAY, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
    }

    protected RenderType renderType() {
        return RENDER_TYPE;
    }

    // TODO: int[] vertices を上書きするのは最悪っぽいので、良い感じに UV マッピングを読みこんで SimpleBakedModel を作るようにしたい
    static class SimpleBakedBlockModelWithoutAtlas implements BakedModel {

        private final SimpleBakedModel original;
        private final Map<@Nullable Direction, List<BakedQuad>> quadsCache = new HashMap<>();

        public SimpleBakedBlockModelWithoutAtlas(SimpleBakedModel original) {
            this.original = original;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, Random random) {
            // net.minecraft.client.resources.model.SimpleBakedModel#getQuads のロジックと整合性が取られている必要がある。
            if (quadsCache.get(direction) != null) {
                return quadsCache.get(direction);
            }

            var quads = original.getQuads(blockState, direction, random).stream().map(bakedQuad -> (BakedQuad) new BlockBakedQuad(bakedQuad)).toList();
            quadsCache.put(direction, quads);

            return quads;
        }

        @Override
        public boolean useAmbientOcclusion() {
            return original.useAmbientOcclusion();
        }

        @Override
        public boolean isGui3d() {
            return original.isGui3d();
        }

        @Override
        public boolean usesBlockLight() {
            return original.usesBlockLight();
        }

        @Override
        public boolean isCustomRenderer() {
            return original.isCustomRenderer();
        }

        @Override
        public TextureAtlasSprite getParticleIcon() {
            return original.getParticleIcon();
        }

        @Override
        public ItemOverrides getOverrides() {
            return original.getOverrides();
        }
    }

    static class BlockBakedQuad extends BakedQuad {
        private final BakedQuad original;
        private final int[] vertices;

        public BlockBakedQuad(BakedQuad original) {
            super(null, 0, null, null, false);

            this.original = original;

            final int VERTEX_COUNT = 4;
            var byteBuffer = ByteBuffer.allocate(original.getVertices().length * Integer.BYTES);
            var intBuffer = byteBuffer.asIntBuffer();
            intBuffer.put(original.getVertices());

            final int U_LOCATION = 16;
            final int V_LOCATION = 20;
            final var U = new float[]{0.0f, 0.0f, 1.0f, 1.0f};
            final var V = new float[]{1.0f, 0.0f, 0.0f, 1.0f};
            for (int vertexIndex = 0; vertexIndex < 4; vertexIndex++) {
                final int offset = vertexIndex * byteBuffer.capacity() / VERTEX_COUNT;
                byteBuffer.putFloat(offset + U_LOCATION, U[vertexIndex]);
                byteBuffer.putFloat(offset + V_LOCATION, V[vertexIndex]);
            }

            vertices = new int[original.getVertices().length];
            intBuffer.get(0, vertices);
        }

        @Override
        public TextureAtlasSprite getSprite() {
            return original.getSprite();
        }

        @Override
        public int[] getVertices() {
            return vertices;
        }

        @Override
        public boolean isTinted() {
            return original.isTinted();
        }

        @Override
        public int getTintIndex() {
            return original.getTintIndex();
        }

        @Override
        public Direction getDirection() {
            return original.getDirection();
        }

        @Override
        public void pipe(IVertexConsumer consumer) {
            original.pipe(consumer);
        }

        @Override
        public boolean isShade() {
            return original.isShade();
        }
    }
}
