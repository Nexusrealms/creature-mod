package de.nexusrealms.creaturemod.magic.spell.effect;

import com.mojang.serialization.Codec;
import de.nexusrealms.creaturemod.ModRegistries;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface SpellEffect<T> {
    Codec<SpellEffect<Entity>> ENTITY_CODEC = ModRegistries.ENTITY_SPELL_EFFECTS.getCodec()
            .dispatch("type", SpellEffect::getType, SpellEffectType::codec);
    Codec<SpellEffect<World>> WORLD_CODEC = ModRegistries.WORLD_SPELL_EFFECTS.getCodec()
            .dispatch("type", SpellEffect::getType, SpellEffectType::codec);
    Codec<SpellEffect<CachedBlockPosition>> BLOCK_CODEC = ModRegistries.BLOCK_SPELL_EFFECTS.getCodec()
            .dispatch("type", SpellEffect::getType, SpellEffectType::codec);
    SpellEffectType<? extends SpellEffect<T>> getType();
    boolean apply(PlayerEntity origin, T target, @Nullable ItemStack castingItem, @Nullable Entity clickTarget);

}
