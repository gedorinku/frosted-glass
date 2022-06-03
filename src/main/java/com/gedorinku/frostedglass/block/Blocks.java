package com.gedorinku.frostedglass.block;

import com.gedorinku.frostedglass.FrostedGlassMod;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Blocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, FrostedGlassMod.ID);
    public static final RegistryObject<Block> FROSTED_GLASS_BLOCK = BLOCKS.register("frosted_glass", () -> new FrostedGlassBlock(BlockBehaviour.Properties.of(Material.GLASS).strength(50.0F, 2000.0F).noOcclusion().noDrops()));
    public static final RegistryObject<Block> FROSTED_GLASS_PANE_BLOCK = BLOCKS.register("frosted_glass_pane", () -> new FrostedGlassPaneBlock(BlockBehaviour.Properties.of(Material.GLASS).strength(50.0F, 2000.0F).noOcclusion().noDrops()));
}
