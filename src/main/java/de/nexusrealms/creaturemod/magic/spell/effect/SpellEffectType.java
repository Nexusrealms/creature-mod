package de.nexusrealms.creaturemod.magic.spell.effect;

import com.mojang.serialization.MapCodec;
import de.nexusrealms.creaturemod.CreatureMod;
import de.nexusrealms.creaturemod.ModRegistries;
import de.nexusrealms.creaturemod.magic.spell.effect.block.LightningStrikeEffect;
import de.nexusrealms.creaturemod.magic.spell.effect.block.OffsetInDirection;
import de.nexusrealms.creaturemod.magic.spell.effect.block.SetOnFire;
import de.nexusrealms.creaturemod.magic.spell.effect.entity.*;
import de.nexusrealms.creaturemod.magic.spell.effect.world.ChangeDaytimeEffect;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registry;
import net.minecraft.world.World;

public record SpellEffectType<T extends SpellEffect<?>>(MapCodec<T> codec) {
    public static final SpellEffectType<GiveItemEffect> GIVE_ITEM = createEntity("give_item", GiveItemEffect.CODEC);
    public static final SpellEffectType<ApplyToWorldEffect> APPLY_TO_WORLD = createEntity("apply_to_world", ApplyToWorldEffect.CODEC);
    public static final SpellEffectType<ForwardBeamEffect> FORWARD_BEAM = createEntity("forward_beam", ForwardBeamEffect.CODEC);
    public static final SpellEffectType<ApplyToClickTargetEffect> APPLY_TO_CLICK_TARGET = createEntity("apply_to_click_target", ApplyToClickTargetEffect.CODEC);
    public static final SpellEffectType<ApplyToBlockEffect> APPLY_TO_BLOCK = createEntity("apply_to_block", ApplyToBlockEffect.CODEC);
    public static final SpellEffectType<LaunchSpellEffect> LAUNCH_SPELL = createEntity("launch_spell", LaunchSpellEffect.CODEC);
    public static final SpellEffectType<SetFireTicksEffect> SET_FIRE_TICKS = createEntity("set_fire_ticks", SetFireTicksEffect.CODEC);

    public static final SpellEffectType<ChangeDaytimeEffect> CHANGE_TIME = createWorld("change_time", ChangeDaytimeEffect.CODEC);

    public static final SpellEffectType<SetOnFire> SET_ON_FIRE = createBlock("set_on_fire", MapCodec.unit(new SetOnFire()));
    public static final SpellEffectType<OffsetInDirection> OFFSET_IN_DIRECTION = createBlock("offset_in_direction", OffsetInDirection.CODEC);
    public static final SpellEffectType<LightningStrikeEffect> LIGHTNING_STRIKE = createBlock("lightning_strike", MapCodec.unit(new LightningStrikeEffect()));


    private static <T extends SpellEffect<Entity>> SpellEffectType<T> createEntity(String name, MapCodec<T> codec){
        return Registry.register(ModRegistries.ENTITY_SPELL_EFFECTS, CreatureMod.id(name), new SpellEffectType<>(codec));
    }
    private static <T extends SpellEffect<World>> SpellEffectType<T> createWorld(String name, MapCodec<T> codec){
        return Registry.register(ModRegistries.WORLD_SPELL_EFFECTS, CreatureMod.id(name), new SpellEffectType<>(codec));
    }
    private static <T extends SpellEffect<CachedBlockPosition>> SpellEffectType<T> createBlock(String name, MapCodec<T> codec){
        return Registry.register(ModRegistries.BLOCK_SPELL_EFFECTS, CreatureMod.id(name), new SpellEffectType<>(codec));
    }
    public static void init(){}

}
