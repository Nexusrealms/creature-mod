package de.nexusrealms.creaturemod.magic.spell.effect.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.nexusrealms.creaturemod.magic.spell.effect.SpellEffect;
import de.nexusrealms.creaturemod.magic.spell.effect.SpellEffectType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public record ChangeDaytimeEffect(long daytime) implements SpellEffect<World> {
    public static final MapCodec<ChangeDaytimeEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.LONG.fieldOf("daytime").forGetter(ChangeDaytimeEffect::daytime)
    ).apply(instance, ChangeDaytimeEffect::new));
    @Override
    public SpellEffectType<? extends SpellEffect<World>> getType() {
        return SpellEffectType.CHANGE_TIME;
    }

    @Override
    public boolean apply(PlayerEntity origin, World target, @Nullable ItemStack castingItem, @Nullable Entity clickTarget) {
        if(!target.isClient()){
            ((ServerWorld) target).setTimeOfDay(daytime);
        }
        return true;
    }
}
