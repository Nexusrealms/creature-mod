package de.nexusrealms.creaturemod.magic.spell.effect.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.nexusrealms.creaturemod.magic.spell.effect.SpellEffect;
import de.nexusrealms.creaturemod.magic.spell.effect.SpellEffectType;
import de.nexusrealms.creaturemod.util.DirectedBlockPosition;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record OffsetInDirection(int offset, SpellEffect<CachedBlockPosition> effect) implements SpellEffect<CachedBlockPosition> {
    public static final MapCodec<OffsetInDirection> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("offset", 1).forGetter(OffsetInDirection::offset),
            SpellEffect.BLOCK_CODEC.fieldOf("effect").forGetter(OffsetInDirection::effect)
    ).apply(instance, OffsetInDirection::new));
    @Override
    public SpellEffectType<? extends SpellEffect<CachedBlockPosition>> getType() {
        return SpellEffectType.OFFSET_IN_DIRECTION;
    }

    @Override
    public boolean apply(PlayerEntity origin, CachedBlockPosition target, @Nullable ItemStack castingItem, @Nullable Entity clickTarget) {
        if(target instanceof DirectedBlockPosition directed){
            return effect.apply(origin, directed.offset(offset), castingItem, clickTarget);
        }
        return false;
    }
}
