package de.nexusrealms.creaturemod.magic.spell.effect.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.nexusrealms.creaturemod.magic.spell.effect.SpellEffect;
import de.nexusrealms.creaturemod.magic.spell.effect.SpellEffectType;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public record ApplyToWorldEffect(SpellEffect<World> effect) implements SpellEffect<Entity> {
    public static final MapCodec<ApplyToWorldEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SpellEffect.WORLD_CODEC.fieldOf("effect").forGetter(ApplyToWorldEffect::effect)
    ).apply(instance, ApplyToWorldEffect::new));
    @Override
    public SpellEffectType<? extends SpellEffect<Entity>> getType() {
        return SpellEffectType.APPLY_TO_WORLD;
    }

    @Override
    public boolean apply(ServerPlayerEntity origin, Entity target, @Nullable ItemStack castingItem, @Nullable Entity clickTarget) {
        return effect.apply(origin, target.getWorld(), castingItem, clickTarget);
    }
}
