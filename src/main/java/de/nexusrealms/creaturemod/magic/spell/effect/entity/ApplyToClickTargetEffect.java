package de.nexusrealms.creaturemod.magic.spell.effect.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.nexusrealms.creaturemod.magic.spell.effect.SpellEffect;
import de.nexusrealms.creaturemod.magic.spell.effect.SpellEffectType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record ApplyToClickTargetEffect(SpellEffect<Entity> effect) implements SpellEffect<Entity>{
    public static final MapCodec<ApplyToClickTargetEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SpellEffect.ENTITY_CODEC.fieldOf("effect").forGetter(ApplyToClickTargetEffect::effect)
    ).apply(instance, ApplyToClickTargetEffect::new));
    @Override
    public SpellEffectType<? extends SpellEffect<Entity>> getType() {
        return SpellEffectType.APPLY_TO_CLICK_TARGET;
    }

    @Override
    public boolean apply(PlayerEntity origin, Entity target, @Nullable ItemStack castingItem, @Nullable Entity clickTarget) {
        if(clickTarget != null){
            return effect.apply(origin, clickTarget, castingItem, clickTarget);
        }
        return false;
    }
}
