package de.nexusrealms.creaturemod.magic.spell.effect.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.nexusrealms.creaturemod.magic.spell.effect.SpellEffect;
import de.nexusrealms.creaturemod.magic.spell.effect.SpellEffectType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record GiveItemEffect(ItemStack stack) implements SpellEffect<Entity> {
    public static final MapCodec<GiveItemEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStack.CODEC.fieldOf("itemStack").forGetter(GiveItemEffect::stack)
    ).apply(instance, GiveItemEffect::new));


    @Override
    public SpellEffectType<? extends SpellEffect<Entity>> getType() {
        return SpellEffectType.GIVE_ITEM;
    }

    @Override
    public boolean apply(PlayerEntity caster, Entity entity, @Nullable ItemStack castingItem, @Nullable Entity clickTarget) {
        caster.giveItemStack(stack.copy());
        return true;
    }
}
