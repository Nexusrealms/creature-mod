package de.nexusrealms.creaturemod.magic.spell.effect.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.nexusrealms.creaturemod.magic.spell.effect.SpellEffect;
import de.nexusrealms.creaturemod.magic.spell.effect.SpellEffectType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

public record SetFireTicksEffect(int fireTicks, boolean fromLava) implements SpellEffect<Entity> {
    public static final MapCodec<SetFireTicksEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("fireTicks").forGetter(SetFireTicksEffect::fireTicks),
            Codec.BOOL.fieldOf("fromLava").forGetter(SetFireTicksEffect::fromLava)
    ).apply(instance, SetFireTicksEffect::new));
    @Override
    public SpellEffectType<? extends SpellEffect<Entity>> getType() {
        return SpellEffectType.SET_FIRE_TICKS;
    }

    @Override
    public boolean apply(PlayerEntity origin, Entity target, @Nullable ItemStack castingItem, @Nullable Entity clickTarget) {
        if(target.getWorld() instanceof ServerWorld){
            if(fromLava) target.setOnFireFromLava();
            target.setFireTicks(fireTicks);
            return true;
        }
        return false;
    }
}
