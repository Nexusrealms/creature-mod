package de.nexusrealms.creaturemod.magic.spell.action;

import com.mojang.serialization.Codec;
import de.nexusrealms.creaturemod.ModRegistries;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public interface SpellAction {
    Codec<SpellAction> CODEC = ModRegistries.SPELL_ACTION_TYPES.getCodec()
            .dispatch("type", SpellAction::getType, SpellActionType::codec);

    SpellActionType<?> getType();
    boolean cast(ServerPlayerEntity caster, @Nullable ItemStack castingItem, @Nullable Entity clickTarget);

}
