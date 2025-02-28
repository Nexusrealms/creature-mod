package de.nexusrealms.creaturemod.items;

import de.nexusrealms.creaturemod.CreatureMod;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;

public class ModItemGroups {
    private static final ItemGroup MAGICAL_GROUP = Registry.register(Registries.ITEM_GROUP,
            CreatureMod.id("magical"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.creature-mod.magical"))
                    .icon(() -> new ItemStack(ModItems.ELEMENT_FLASK)).build());
    public static final RegistryKey<ItemGroup> MAGICAL = RegistryKey.of(RegistryKeys.ITEM_GROUP, CreatureMod.id("magical"));
    public static void init() {
    }
}
