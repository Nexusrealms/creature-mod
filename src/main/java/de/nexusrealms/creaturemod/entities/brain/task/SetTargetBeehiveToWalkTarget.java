package de.nexusrealms.creaturemod.entities.brain.task;

import com.mojang.datafixers.util.Pair;
import de.nexusrealms.creaturemod.entities.brain.ModActivities;
import de.nexusrealms.creaturemod.entities.brain.memory.ModMemories;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.*;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.ToIntBiFunction;

public class SetTargetBeehiveToWalkTarget <E extends MobEntity> extends ExtendedBehaviour<E> {
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(3).hasMemory(ModMemories.TARGET_BEEHIVE).usesMemories(MemoryModuleType.WALK_TARGET, MemoryModuleType.LOOK_TARGET);

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryModuleState>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }
    protected BiFunction<E, BlockPos, Float> speedMod = (owner, target) -> 1.0F;
    protected ToIntBiFunction<E, BlockPos> closeEnoughWhen = (owner, target) -> 2;


    public SetTargetBeehiveToWalkTarget<E> speedMod(BiFunction<E, BlockPos, Float> speedModifier) {
        this.speedMod = speedModifier;
        return this;
    }

    public SetTargetBeehiveToWalkTarget<E> closeEnoughDist(ToIntBiFunction<E, BlockPos> closeEnoughMod) {
        this.closeEnoughWhen = closeEnoughMod;
        return this;
    }

    protected void start(E entity) {
        Brain<?> brain = entity.getBrain();
        BlockPos target = BrainUtils.getMemory(entity, ModMemories.TARGET_BEEHIVE);
        if (entity.getPos().distanceTo(target.toCenterPos()) < closeEnoughWhen.applyAsInt(entity, target)) {
            BrainUtils.clearMemory(brain, MemoryModuleType.WALK_TARGET);
        } else {
            BrainUtils.setMemory(brain, MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(target));
            BrainUtils.setMemory(brain, MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosLookTarget(target), (Float)this.speedMod.apply(entity, target), this.closeEnoughWhen.applyAsInt(entity, target)));
        }

    }
}
