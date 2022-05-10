package com.gedorinku.frostedglass.block.entity;

import com.gedorinku.frostedglass.FrostedGlassMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class FrostedGlassBlockEntity extends BlockEntity {
    public FrostedGlassBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(FrostedGlassMod.FROSTED_GLASS_BLOCK_ENTITY.get(), p_155229_, p_155230_);
    }

    public boolean shouldRenderFace(Direction p_59959_) {
        return Block.shouldRenderFace(this.getBlockState(), this.level, this.getBlockPos(), p_59959_, this.getBlockPos().relative(p_59959_));
    }
}
