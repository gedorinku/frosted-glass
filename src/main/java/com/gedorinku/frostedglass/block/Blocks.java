package com.gedorinku.frostedglass.block;

import com.gedorinku.frostedglass.FrostedGlassMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Blocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, FrostedGlassMod.ID);
    public static final RegistryObject<Block> FROSTED_GLASS_BLOCK = BLOCKS.register("frosted_glass", () -> new FrostedGlassBlock(BlockBehaviour.Properties.of(Material.GLASS).strength(0.3F).sound(SoundType.GLASS).noOcclusion().isValidSpawn(Blocks::never).isRedstoneConductor(Blocks::never).isSuffocating(Blocks::never).isViewBlocking(Blocks::never)));

    public static final RegistryObject<Block> FROSTED_GLASS_PANE_BLOCK = BLOCKS.register("frosted_glass_pane", () -> new FrostedGlassPaneBlock(BlockBehaviour.Properties.of(Material.GLASS).strength(0.3F).sound(SoundType.GLASS).noOcclusion()));

    private static boolean never(BlockState state, BlockGetter blockGetter, BlockPos pos, EntityType<?> entityType) {
        return false;
    }

    private static boolean never(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return false;
    }
}
