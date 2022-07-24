package com.gedorinku.frostedglass;

import com.gedorinku.frostedglass.block.BlockItems;
import com.gedorinku.frostedglass.block.Blocks;
import com.gedorinku.frostedglass.client.renderer.FrostedGlassBlockRenderer;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.IOException;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(FrostedGlassMod.ID)
public class FrostedGlassMod {
    public static final String ID = "frostedglass";

    public static ShaderInstance RENDER_TYPE_FROSTED_GLASS_SHADER;
    public static ShaderInstance RENDER_TYPE_ITEM_ENTITY_FROSTED_GLASS_SHADER;

    public FrostedGlassMod() {
        Blocks.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BlockItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = ID)
    public static class ClientSetup {
        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {
            ItemBlockRenderTypes.setRenderLayer(Blocks.FROSTED_GLASS_BLOCK.get(), FrostedGlassBlockRenderer.RENDER_TYPE);
            ItemBlockRenderTypes.setRenderLayer(Blocks.FROSTED_GLASS_PANE_BLOCK.get(), FrostedGlassBlockRenderer.RENDER_TYPE);
        }

        @SubscribeEvent
        public static void shaderRegistry(RegisterShadersEvent event) throws IOException {
            event.registerShader(new ShaderInstance(event.getResourceManager(), new ResourceLocation(ID, "frosted_glass/frosted_glass"), DefaultVertexFormat.BLOCK), shaderInstance -> {
                RENDER_TYPE_FROSTED_GLASS_SHADER = shaderInstance;
            });
            event.registerShader(new ShaderInstance(event.getResourceManager(), new ResourceLocation(ID, "frosted_glass/item_entity_frosted_glass"), DefaultVertexFormat.NEW_ENTITY), shaderInstance -> {
                RENDER_TYPE_ITEM_ENTITY_FROSTED_GLASS_SHADER = shaderInstance;
            });
        }
    }
}
