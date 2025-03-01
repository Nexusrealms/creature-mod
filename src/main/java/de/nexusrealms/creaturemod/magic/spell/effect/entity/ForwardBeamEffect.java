package de.nexusrealms.creaturemod.magic.spell.effect.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import de.nexusrealms.creaturemod.CreatureMod;
import de.nexusrealms.creaturemod.magic.spell.effect.SpellEffect;
import de.nexusrealms.creaturemod.magic.spell.effect.SpellEffectType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record ForwardBeamEffect(int color) implements SpellEffect<Entity> {
    public static final MapCodec<ForwardBeamEffect> CODEC = Codec.INT.fieldOf("color").xmap(ForwardBeamEffect::new, ForwardBeamEffect::color);
    @Override
    public SpellEffectType<? extends SpellEffect<Entity>> getType() {
        return SpellEffectType.FORWARD_BEAM;
    }

    @Override
    public boolean apply(PlayerEntity origin, Entity target, @Nullable ItemStack castingItem, @Nullable Entity clickTarget) {
        CreatureMod.LOGGER.info("Yea yea this logs on the client too");
        return true;
    }
}
