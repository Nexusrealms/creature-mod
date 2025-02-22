package de.nexusrealms.creaturemod.magic.spell.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public record GiveItemAction(ItemStack stack) implements SpellAction {
    public static final MapCodec<GiveItemAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStack.CODEC.fieldOf("itemStack").forGetter(GiveItemAction::stack)
    ).apply(instance, GiveItemAction::new));
    @Override
    public SpellActionType<?> getType() {
        return SpellActionType.GIVE_ITEM;
    }

    @Override
    public boolean cast(ServerPlayerEntity caster, @Nullable ItemStack castingItem, @Nullable Entity clickTarget) {
        caster.giveItemStack(stack.copy());
        return true;
    }
}
