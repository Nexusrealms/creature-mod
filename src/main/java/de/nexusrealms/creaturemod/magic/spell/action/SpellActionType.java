package de.nexusrealms.creaturemod.magic.spell.action;

import com.mojang.serialization.MapCodec;
import de.nexusrealms.creaturemod.CreatureMod;
import de.nexusrealms.creaturemod.ModRegistries;
import net.minecraft.registry.Registry;

public record SpellActionType<T extends SpellAction>(MapCodec<T> codec) {
    public static final SpellActionType<GiveItemAction> GIVE_ITEM = create("give_item", GiveItemAction.CODEC);
    private static <T extends SpellAction> SpellActionType<T> create(String name, MapCodec<T> codec){
        return Registry.register(ModRegistries.SPELL_ACTION_TYPES, CreatureMod.id(name), new SpellActionType<>(codec));
    }
    public static void init(){}

}
