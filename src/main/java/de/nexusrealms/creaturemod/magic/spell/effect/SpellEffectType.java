package de.nexusrealms.creaturemod.magic.spell.effect;

import com.mojang.serialization.MapCodec;
import de.nexusrealms.creaturemod.CreatureMod;
import de.nexusrealms.creaturemod.ModRegistries;
import de.nexusrealms.creaturemod.magic.spell.effect.entity.ApplyToWorldEffect;
import de.nexusrealms.creaturemod.magic.spell.effect.entity.ForwardBeamEffect;
import de.nexusrealms.creaturemod.magic.spell.effect.entity.GiveItemEffect;
import de.nexusrealms.creaturemod.magic.spell.effect.world.ChangeDaytimeEffect;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registry;
import net.minecraft.world.World;

public record SpellEffectType<T extends SpellEffect<?>>(MapCodec<T> codec) {
    public static final SpellEffectType<GiveItemEffect> GIVE_ITEM = createEntity("give_item", GiveItemEffect.CODEC);
    public static final SpellEffectType<ApplyToWorldEffect> APPLY_TO_WORLD = createEntity("apply_to_world", ApplyToWorldEffect.CODEC);
    public static final SpellEffectType<ForwardBeamEffect> FORWARD_BEAM = createEntity("forward_beam", ForwardBeamEffect.CODEC);

    public static final SpellEffectType<ChangeDaytimeEffect> CHANGE_TIME = createWorld("change_time", ChangeDaytimeEffect.CODEC);

    private static <T extends SpellEffect<Entity>> SpellEffectType<T> createEntity(String name, MapCodec<T> codec){
        return Registry.register(ModRegistries.ENTITY_SPELL_EFFECTS, CreatureMod.id(name), new SpellEffectType<>(codec));
    }
    private static <T extends SpellEffect<World>> SpellEffectType<T> createWorld(String name, MapCodec<T> codec){
        return Registry.register(ModRegistries.WORLD_SPELL_EFFECTS, CreatureMod.id(name), new SpellEffectType<>(codec));
    }
    public static void init(){}

}
