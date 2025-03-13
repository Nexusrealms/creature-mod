package de.nexusrealms.creaturemod.magic.spell.effect.block;

import de.nexusrealms.creaturemod.magic.spell.effect.SpellEffect;
import de.nexusrealms.creaturemod.magic.spell.effect.SpellEffectType;
import de.nexusrealms.creaturemod.util.DirectedBlockPosition;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class SetOnFire implements SpellEffect<CachedBlockPosition> {
    @Override
    public SpellEffectType<? extends SpellEffect<CachedBlockPosition>> getType() {
        return null;
    }

    @Override
    public boolean apply(PlayerEntity origin, CachedBlockPosition target, @Nullable ItemStack castingItem, @Nullable Entity clickTarget) {
        if(target.getWorld() instanceof ServerWorld world && target instanceof DirectedBlockPosition directedBlockPosition){
            BlockPos blockPos = target.getBlockPos();
            BlockState blockState = world.getBlockState(blockPos);
            if (!CampfireBlock.canBeLit(blockState) && !CandleBlock.canBeLit(blockState) && !CandleCakeBlock.canBeLit(blockState)) {
                if (AbstractFireBlock.canPlaceAt(world, blockPos, directedBlockPosition.getDirection())) {
                    BlockState blockState2 = AbstractFireBlock.getState(world, blockPos);
                    world.setBlockState(blockPos, blockState2, 11);
                    world.emitGameEvent(origin, GameEvent.BLOCK_PLACE, blockPos);
                    return true;
                } else {
                    return false;
                }
            } else {
                world.setBlockState(blockPos, blockState.with(Properties.LIT, true), 11);
                world.emitGameEvent(origin, GameEvent.BLOCK_CHANGE, blockPos);

                return true;
            }
        }
        return false;
    }
}
