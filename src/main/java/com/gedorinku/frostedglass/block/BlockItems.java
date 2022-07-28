package com.gedorinku.frostedglass.block;

import com.gedorinku.frostedglass.FrostedGlassCreativeModeTab;
import com.gedorinku.frostedglass.FrostedGlassMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, FrostedGlassMod.ID);
    public static final RegistryObject<Item> FROSTED_GLASS_BLOCK_ITEM = ITEMS.register("frosted_glass", () -> new BlockItem(Blocks.FROSTED_GLASS_BLOCK.get(), new Item.Properties().tab(FrostedGlassCreativeModeTab.TAB)));
    public static final RegistryObject<Item> FROSTED_GLASS_PANE_BLOCK_ITEM = ITEMS.register("frosted_glass_pane", () -> new BlockItem(Blocks.FROSTED_GLASS_PANE_BLOCK.get(), new Item.Properties().tab(FrostedGlassCreativeModeTab.TAB)));
}
