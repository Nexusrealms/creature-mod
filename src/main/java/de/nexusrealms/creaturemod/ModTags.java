package de.nexusrealms.creaturemod;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import javax.swing.text.html.HTML;

public class ModTags {
    public static final TagKey<Item> USE_BINDABLE = createItem("use_bindable");
    public static final TagKey<Item> ATTACK_BINDABLE = createItem("attack_bindable");

    private static TagKey<Item> createItem(String name){
        return TagKey.of(RegistryKeys.ITEM, CreatureMod.id(name));
    }
}
