package com.gedorinku.frostedglass;

import com.gedorinku.frostedglass.block.BlockItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class FrostedGlassCreativeModeTab {
    public static final CreativeModeTab TAB = new CreativeModeTab(FrostedGlassMod.ID) {
        @Override
        public ItemStack makeIcon() {
            return BlockItems.FROSTED_GLASS_BLOCK_ITEM.get().getDefaultInstance();
        }
    };
}
