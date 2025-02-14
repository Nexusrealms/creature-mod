package de.nexusrealms.creaturemod.entities.brain.task;

import com.mojang.datafixers.util.Pair;
import de.nexusrealms.creaturemod.entities.brain.memory.ModMemories;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.registry.SBLMemoryTypes;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class FindHoney <E extends MobEntity> extends ExtendedBehaviour<E> {
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2).hasMemory(SBLMemoryTypes.NEARBY_BLOCKS.get()).usesMemory(ModMemories.TARGET_BEEHIVE);
    protected boolean doStartCheck(ServerWorld level, E entity, long gameTime) {
        return (!BrainUtils.hasMemory(entity, MemoryModuleType.ATTACK_TARGET) && super.doStartCheck(level, entity, gameTime)) && checkExtraStartConditions(level, entity);
    }
    protected BlockPos toTarget = null;

    protected boolean checkExtraStartConditions(ServerWorld level, E owner) {
        this.toTarget = this.getTarget(owner, level);
        return this.toTarget != null;
    }


    protected @Nullable BlockPos getTarget(E owner, ServerWorld level) {
        List<Pair<BlockPos, BlockState>> positions = BrainUtils.getMemory(owner, SBLMemoryTypes.NEARBY_BLOCKS.get());
        return positions.stream().findAny().map(Pair::getFirst).orElse(null);
    }

    protected void start(E entity) {
        BrainUtils.setMemory(entity, ModMemories.TARGET_BEEHIVE, this.toTarget);
        BrainUtils.clearMemory(entity, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        this.toTarget = null;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryModuleState>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }
}
