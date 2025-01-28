package de.nexusrealms.creaturemod.items;

import de.nexusrealms.creaturemod.CreatureMod;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

import java.util.function.BiConsumer;

public class ModItems {
    public static final Item GARLIC_CLOVE = create("garlic_clove", new Item(new Item.Settings().food(FoodComponents.GARLIC_CLOVE)), ItemGroups.FOOD_AND_DRINK,
            (fabricItemGroupEntries, item) -> fabricItemGroupEntries.addAfter(Items.BEETROOT, item));
    public static final Item GARLIC_BULB = create("garlic_bulb", new Item(new Item.Settings()), ItemGroups.NATURAL);
    private static <T extends Item> T create(String name, T item, RegistryKey<ItemGroup> group){
        return create(name, item, group, FabricItemGroupEntries::add);
    }
    private static <T extends Item> T create(String name, T item,RegistryKey<ItemGroup> group, BiConsumer<FabricItemGroupEntries, T> itemGrouper){
        ItemGroupEvents.modifyEntriesEvent(group).register(fabricItemGroupEntries -> itemGrouper.accept(fabricItemGroupEntries, item));
        return Registry.register(Registries.ITEM, CreatureMod.id(name), item);
    }
    public static void init(){}
    public static class FoodComponents {
        public static final FoodComponent GARLIC_CLOVE = new FoodComponent.Builder().nutrition(3).snack().build();
    }
}
