package de.nexusrealms.creaturemod.entities.brain.task;

import com.mojang.datafixers.util.Pair;
import de.nexusrealms.creaturemod.entities.brain.memory.ModMemories;
import de.nexusrealms.creaturemod.entities.natural.BearEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class EatFromBeehive extends ExtendedBehaviour<BearEntity> {
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(1).hasMemory(ModMemories.TARGET_BEEHIVE).usesMemory(ModMemories.FULL);

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryModuleState>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean doStartCheck(ServerWorld level, BearEntity entity, long gameTime) {
        return super.doStartCheck(level, entity, gameTime) && entity.isInEatingRange(BrainUtils.getMemory(entity, ModMemories.TARGET_BEEHIVE));
    }

    @Override
    protected void start(BearEntity entity) {
        BlockPos target = BrainUtils.getMemory(entity, ModMemories.TARGET_BEEHIVE);
        entity.eatHoneyAtPos(target);
        BrainUtils.setForgettableMemory(entity, ModMemories.FULL, Unit.INSTANCE, 1000);
        super.start(entity);
    }


}
