package com.gedorinku.frostedglass;

import com.gedorinku.frostedglass.block.FrostedGlassBlock;
import com.gedorinku.frostedglass.block.FrostedGlassPaneBlock;
import com.gedorinku.frostedglass.client.renderer.FrostedGlassBlockRenderType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(FrostedGlassMod.ID)
public class FrostedGlassMod {
    public static final String ID = "frostedglass";

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ID);
    public static final RegistryObject<Block> FROSTED_GLASS_BLOCK = BLOCKS.register("frosted_glass", () -> new FrostedGlassBlock(BlockBehaviour.Properties.of(Material.GLASS).strength(50.0F, 2000.0F).noOcclusion().noDrops()));
    public static final RegistryObject<Block> FROSTED_GLASS_PANE_BLOCK = BLOCKS.register("frosted_glass_pane", () -> new FrostedGlassPaneBlock(BlockBehaviour.Properties.of(Material.GLASS).strength(50.0F, 2000.0F).noOcclusion().noDrops()));


    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ID);
    public static final RegistryObject<Item> FROSTED_GLASS_BLOCK_ITEM = ITEMS.register("frosted_glass", () -> new BlockItem(FROSTED_GLASS_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> FROSTED_GLASS_PANE_BLOCK_ITEM = ITEMS.register("frosted_glass_pane", () -> new BlockItem(FROSTED_GLASS_PANE_BLOCK.get(), new Item.Properties()));
    public static ShaderInstance RENDER_TYPE_FROSTED_GLASS_SHADER;

    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public FrostedGlassMod() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // Some example code to dispatch IMC to another mod
        InterModComms.sendTo("examplemod", "helloworld", () -> {
            LOGGER.info("Hello world from the MDK");
            return "Hello world";
        });
    }

    private void processIMC(final InterModProcessEvent event) {
        // Some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m -> m.messageSupplier().get()).
                collect(Collectors.toList()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // Register a new block here
            LOGGER.info("HELLO from Register Block");
        }
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = ID)
    public static class ClientSetup {
        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {
            ItemBlockRenderTypes.setRenderLayer(FROSTED_GLASS_BLOCK.get(), FrostedGlassBlockRenderType.RENDER_TYPE);
            ItemBlockRenderTypes.setRenderLayer(FROSTED_GLASS_PANE_BLOCK.get(), FrostedGlassBlockRenderType.RENDER_TYPE);
        }

        @SubscribeEvent
        public static void shaderRegistry(RegisterShadersEvent event) throws IOException {
            event.registerShader(new ShaderInstance(event.getResourceManager(), new ResourceLocation(ID, "frosted_glass/frosted_glass"), DefaultVertexFormat.NEW_ENTITY), shaderInstance -> {
                RENDER_TYPE_FROSTED_GLASS_SHADER = shaderInstance;
            });
        }
    }
}
