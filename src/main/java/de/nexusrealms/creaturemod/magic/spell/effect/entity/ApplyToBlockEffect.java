package de.nexusrealms.creaturemod.magic.spell.effect.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.nexusrealms.creaturemod.magic.spell.effect.SpellEffect;
import de.nexusrealms.creaturemod.magic.spell.effect.SpellEffectType;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record ApplyToBlockEffect(SpellEffect<CachedBlockPosition> effect) implements SpellEffect<Entity> {
    public static final MapCodec<ApplyToBlockEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SpellEffect.BLOCK_CODEC.fieldOf("effect").forGetter(ApplyToBlockEffect::effect)
    ).apply(instance, ApplyToBlockEffect::new));
    @Override
    public SpellEffectType<? extends SpellEffect<Entity>> getType() {
        return SpellEffectType.APPLY_TO_BLOCK;
    }

    @Override
    public boolean apply(PlayerEntity origin, Entity target, @Nullable ItemStack castingItem, @Nullable Entity clickTarget) {
        return effect.apply(origin, new CachedBlockPosition(target.getWorld(), target.getBlockPos(), false), castingItem, clickTarget);
    }
}
