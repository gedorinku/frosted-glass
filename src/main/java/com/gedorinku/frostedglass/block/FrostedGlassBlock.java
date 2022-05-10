package com.gedorinku.frostedglass.block;

import com.gedorinku.frostedglass.block.entity.FrostedGlassBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class FrostedGlassBlock extends BaseEntityBlock  {
    public FrostedGlassBlock(Properties p_49224_) {
        super(p_49224_);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new FrostedGlassBlockEntity(p_153215_, p_153216_);
    }
}
