package de.nexusrealms.creaturemod.magic.spell.effect.block;

import com.mojang.serialization.Codec;
import de.nexusrealms.creaturemod.magic.spell.effect.SpellEffect;
import de.nexusrealms.creaturemod.magic.spell.effect.SpellEffectType;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

public class LightningStrikeEffect implements SpellEffect<CachedBlockPosition> {
    @Override
    public SpellEffectType<? extends SpellEffect<CachedBlockPosition>> getType() {
        return SpellEffectType.LIGHTNING_STRIKE;
    }

    @Override
    public boolean apply(PlayerEntity origin, CachedBlockPosition target, @Nullable ItemStack castingItem, @Nullable Entity clickTarget) {
        if(target.getWorld() instanceof ServerWorld world){
            world.spawnEntity(EntityType.LIGHTNING_BOLT.create(world, null, target.getBlockPos(), SpawnReason.TRIGGERED, true, false));
            return true;
        }
        return false;
    }
}
