package de.nexusrealms.creaturemod.magic.spell.effect.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.nexusrealms.creaturemod.magic.spell.effect.SpellEffect;
import de.nexusrealms.creaturemod.magic.spell.effect.SpellEffectType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public record KnockAwayFromOrigin(int strength) implements SpellEffect<Entity> {
    public static final MapCodec<KnockAwayFromOrigin> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("strength").forGetter(KnockAwayFromOrigin::strength)
    ).apply(instance, KnockAwayFromOrigin::new));
    @Override
    public SpellEffectType<? extends SpellEffect<Entity>> getType() {
        return null;
    }

    @Override
    public boolean apply(PlayerEntity origin, Entity target, @Nullable ItemStack castingItem, @Nullable Entity clickTarget) {
        if(target instanceof LivingEntity living){
            Vec3d vec3d = origin.getPos().subtract(target.getPos()).normalize();
            living.takeKnockback(strength, vec3d.getX(), vec3d.getZ());
            return true;
        }
        return false;
    }
}
