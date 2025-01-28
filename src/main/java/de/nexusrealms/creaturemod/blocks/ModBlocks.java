package de.nexusrealms.creaturemod.blocks;

import de.nexusrealms.creaturemod.CreatureMod;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModBlocks {
    public static final CropBlock GARLIC = create("garlic", new GarlicBlock(AbstractBlock.Settings.copy(Blocks.WHEAT)));
    private static <T extends Block> T createWithItem(String name, T block, RegistryKey<ItemGroup> group){
        return createWithItem(name, block, group, b -> new BlockItem(b, new Item.Settings()), FabricItemGroupEntries::add);
    }
    private static <T extends Block> T createWithItem(String name, T block, RegistryKey<ItemGroup> group,
                                                      Function<T, Item> itemFactory,
                                                      BiConsumer<FabricItemGroupEntries, Item>itemGrouper){
        Item item = itemFactory.apply(block);
        create(name, block);
        ItemGroupEvents.modifyEntriesEvent(group).register(fabricItemGroupEntries -> itemGrouper.accept(fabricItemGroupEntries, item));
        Registry.register(Registries.ITEM, CreatureMod.id(name), item);
        return block;
    }
    private static <T extends Block> T create(String name, T block){
        return Registry.register(Registries.BLOCK, CreatureMod.id(name), block);
    }
    public static void init(){}
}
