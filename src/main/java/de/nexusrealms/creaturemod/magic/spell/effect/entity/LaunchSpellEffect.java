package de.nexusrealms.creaturemod.magic.spell.effect.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.nexusrealms.creaturemod.entities.ModEntities;
import de.nexusrealms.creaturemod.magic.spell.effect.SpellEffect;
import de.nexusrealms.creaturemod.magic.spell.effect.SpellEffectType;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record LaunchSpellEffect(Optional<SpellEffect<Entity>> entityHitEffect, Optional<SpellEffect<CachedBlockPosition>> blockHitEffect, Identifier emitter) implements SpellEffect<Entity>{
    public static final MapCodec<LaunchSpellEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SpellEffect.ENTITY_CODEC.optionalFieldOf("entityHitEffect").forGetter(LaunchSpellEffect::entityHitEffect),
            SpellEffect.BLOCK_CODEC.optionalFieldOf("blockHitEffect").forGetter(LaunchSpellEffect::blockHitEffect),
            Identifier.CODEC.fieldOf("emitter").forGetter(LaunchSpellEffect::emitter)
    ).apply(instance, LaunchSpellEffect::new));
    public static final Codec<LaunchSpellEffect> STORE_CODEC = CODEC.codec();
    @Override
    public SpellEffectType<? extends SpellEffect<Entity>> getType() {
        return SpellEffectType.LAUNCH_SPELL;
    }

    @Override
    public boolean apply(PlayerEntity origin, Entity target, @Nullable ItemStack castingItem, @Nullable Entity clickTarget) {
        if(target.getWorld() instanceof ServerWorld world){
            return world.spawnEntity(ModEntities.LAUNCHED_SPELL.create(world, l -> {l.setData(target, emitter, this, castingItem); l.setOwner(origin);}, target.getBlockPos(), SpawnReason.TRIGGERED, false, false));
        }
        return false;
    }
}
