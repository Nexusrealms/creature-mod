package de.nexusrealms.creaturemod.entities.brain.task;

import com.mojang.datafixers.util.Pair;
import de.nexusrealms.creaturemod.entities.brain.memory.ModMemories;
import de.nexusrealms.creaturemod.entities.natural.BearEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.util.math.BlockPos;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.function.BiPredicate;

public class InvalidateTargetBeehive <E extends BearEntity> extends ExtendedBehaviour<E> {
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2).hasMemory(ModMemories.TARGET_BEEHIVE).usesMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    protected long pathfindingAttentionSpan = 200L;
    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryModuleState>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    public InvalidateTargetBeehive<E> stopTryingToPathAfter(long ticks) {
        this.pathfindingAttentionSpan = ticks;
        return this;
    }

    protected void start(E entity) {
        BlockPos targetBeehive = BrainUtils.getMemory(entity, ModMemories.TARGET_BEEHIVE);
        BlockState state = entity.getWorld().getBlockState(targetBeehive);
        if (this.isTiredOfPathing(entity) || !BearEntity.BEEHIVE_PREDICATE.test(state, entity)) {
            BrainUtils.clearMemory(entity, ModMemories.TARGET_BEEHIVE);
        }
    }

    protected boolean isTiredOfPathing(E entity) {
        if (this.pathfindingAttentionSpan <= 0L) {
            return false;
        } else {
            Long time = BrainUtils.getMemory(entity, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
            return time != null && entity.getWorld().getTime() - time > this.pathfindingAttentionSpan;
        }
    }
}
