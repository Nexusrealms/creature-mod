package de.nexusrealms.creaturemod.entities.brain.task;

import com.mojang.datafixers.util.Pair;
import de.nexusrealms.creaturemod.entities.brain.memory.ModMemories;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.TeleportTarget;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class TeleportHome<E extends MobEntity> extends ExtendedBehaviour<E> {
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(3).usesMemories(MemoryModuleType.HOME, ModMemories.IS_HOME_THREATHENED);
    protected boolean doStartCheck(ServerWorld level, E entity, long gameTime) {
        return (BrainUtils.hasMemory(entity, MemoryModuleType.HOME)
                && BrainUtils.hasMemory(entity, ModMemories.IS_HOME_THREATHENED)
                && !BrainUtils.getMemory(entity, MemoryModuleType.HOME).pos().isWithinDistance(entity.getPos(), entity.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE))
                && super.doStartCheck(level, entity, gameTime));
    }
    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryModuleState>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected void start(E entity) {
        BrainUtils.clearMemory(entity, ModMemories.IS_HOME_THREATHENED);
        GlobalPos pos = BrainUtils.getMemory(entity, MemoryModuleType.HOME);
        if(pos.dimension().equals(entity.getWorld().getRegistryKey())){
            entity.teleport(pos.pos().getX(), pos.pos().getY(), pos.pos().getZ(), true);
        } else {
            entity.teleportTo(new TeleportTarget(entity.getServer().getWorld(pos.dimension()),
                    pos.pos().toCenterPos(), entity.getVelocity(), entity.getYaw(), entity.getPitch(), TeleportTarget.NO_OP));
        }
    }
}
