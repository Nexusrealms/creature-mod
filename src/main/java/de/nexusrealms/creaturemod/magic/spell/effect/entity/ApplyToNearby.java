package de.nexusrealms.creaturemod.magic.spell.effect.entity;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.nexusrealms.creaturemod.magic.spell.effect.SpellEffect;
import de.nexusrealms.creaturemod.magic.spell.effect.SpellEffectType;
import it.unimi.dsi.fastutil.booleans.Boolean2BooleanFunction;
import it.unimi.dsi.fastutil.booleans.BooleanBinaryOperator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record ApplyToNearby(int radius, EntityPredicate predicate, boolean exceptTarget, boolean exceptCaster,
                            SpellEffect<Entity> effect) implements SpellEffect<Entity> {
    public static final MapCodec<ApplyToNearby> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("radius").forGetter(ApplyToNearby::radius),
            EntityPredicate.CODEC.optionalFieldOf("predicate", EntityPredicate.Builder.create().build()).forGetter(ApplyToNearby::predicate),
            Codec.BOOL.optionalFieldOf("exceptCaster", false).forGetter(ApplyToNearby::exceptCaster),
            Codec.BOOL.optionalFieldOf("exceptTarget", true).forGetter(ApplyToNearby::exceptTarget),
            SpellEffect.ENTITY_CODEC.fieldOf("effect").forGetter(ApplyToNearby::effect)
    ).apply(instance, ApplyToNearby::new));

    @Override
    public SpellEffectType<? extends SpellEffect<Entity>> getType() {
        return SpellEffectType.APPLY_TO_NEARBY;
    }

    @Override
    public boolean apply(PlayerEntity origin, Entity target, @Nullable ItemStack castingItem, @Nullable Entity clickTarget) {
        if (target.getWorld() instanceof ServerWorld world) {
            List<Entity> entities = world.getOtherEntities(null, Box.from(target.getPos()).expand(radius), entity -> {
                        if (exceptCaster && entity == origin) return false;
                        if (exceptTarget && entity == target) return false;
                        return predicate.test(world, null, entity);
                    }
            );
            return entities.stream().map(entity -> effect.apply(origin, entity, castingItem, clickTarget)).reduce(false, (b1, b2) -> b1 || b2);
        }
        return false;
    }
}
