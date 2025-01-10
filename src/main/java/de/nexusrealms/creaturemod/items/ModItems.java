package de.nexusrealms.creaturemod.items;

import de.nexusrealms.creaturemod.CreatureMod;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

import java.util.function.BiConsumer;

public class ModItems {

    private static <T extends Item> T create(String name, T item, RegistryKey<ItemGroup> group){
        return create(name, item, group, FabricItemGroupEntries::add);
    }
    private static <T extends Item> T create(String name, T item,RegistryKey<ItemGroup> group, BiConsumer<FabricItemGroupEntries, T> itemGrouper){
        ItemGroupEvents.modifyEntriesEvent(group).register(fabricItemGroupEntries -> itemGrouper.accept(fabricItemGroupEntries, item));
        return Registry.register(Registries.ITEM, CreatureMod.id(name), item);
    }
    public static void init(){}
}
