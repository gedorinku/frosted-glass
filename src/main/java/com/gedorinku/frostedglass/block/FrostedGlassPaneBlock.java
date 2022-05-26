package com.gedorinku.frostedglass.block;

import net.minecraft.world.level.block.IronBarsBlock;

public class FrostedGlassPaneBlock extends IronBarsBlock {
    public FrostedGlassPaneBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(NORTH, false)
            .setValue(EAST, false)
            .setValue(SOUTH, false)
            .setValue(WEST, false)
            .setValue(WATERLOGGED, false));
    }
}
