package de.nexusrealms.creaturemod.entities.brain.task;

import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class SetEntityLookTarget <E extends LivingEntity> extends ExtendedBehaviour<E> {
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2).hasMemory(MemoryModuleType.VISIBLE_MOBS).noMemory(MemoryModuleType.LOOK_TARGET);
    protected BiPredicate<E, LivingEntity> lookPredicate = this::defaultPredicate;
    protected LivingEntity target = null;

    public SetEntityLookTarget() {
    }


    public SetEntityLookTarget<E> lookPredicate(BiPredicate<E, LivingEntity> predicate) {
        this.lookPredicate = predicate;
        return this;
    }


    protected boolean shouldRun(ServerWorld level, E entity) {
        Optional<LivingEntity> var3 = BrainUtils.getMemory(entity, MemoryModuleType.VISIBLE_MOBS).findFirst(entity1 -> lookPredicate.test(entity, entity1));

        var3.ifPresent(entity1 -> target =  entity1);
        return var3.isPresent();
    }

    protected boolean defaultPredicate(E entity, LivingEntity player) {
        if (entity.hasPassenger(player)) {
            return false;
        } else {
            if (entity instanceof MobEntity) {
                MobEntity mob = (MobEntity)entity;
                if (!mob.getVisibilityCache().canSee(player)) {
                    return false;
                }
            } else if (!entity.canSee(player)) {
                return false;
            }

            double visibleDistance = Math.max(player.getAttackDistanceScalingFactor(entity) * 16.0, 2.0);
            return entity.squaredDistanceTo(player) <= visibleDistance * visibleDistance;
        }
    }

    protected void start(E entity) {
        BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new EntityLookTarget(this.target, true));
    }

    protected void stop(E entity) {
        this.target = null;
    }
    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryModuleState>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;

    }
}
