package de.nexusrealms.creaturemod.entities.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class RetaliateOrTargetOthers<E extends MobEntity> extends TargetOrRetaliate<E> {
    protected Predicate<LivingEntity> retaliationPredicate = canAttackPredicate;
    //SBL does not allow different predicates for retaliation and targeting, so I have to do it myself
    public TargetOrRetaliate<E> retaliationPredicate(Predicate<LivingEntity> predicate) {
        this.retaliationPredicate = predicate;
        return this;
    }
    @Override
    protected @Nullable LivingEntity getTarget(E owner, ServerWorld level, @Nullable LivingEntity existingTarget) {
        Brain<?> brain = owner.getBrain();
        LivingEntity newTarget = BrainUtils.getMemory(brain, this.priorityTargetMemory);
        if (newTarget == null) {
            newTarget = BrainUtils.getMemory(brain, MemoryModuleType.HURT_BY_ENTITY);
            if (newTarget == null) {
                LivingTargetCache nearbyEntities = BrainUtils.getMemory(brain, MemoryModuleType.VISIBLE_MOBS);
                if (nearbyEntities != null) {
                    newTarget = nearbyEntities.findFirst(this.canAttackPredicate).orElse(null);
                }

                if (newTarget == null) {
                    return null;
                }
            } else {
                if (newTarget == existingTarget) {
                    return null;
                } else {
                    return this.retaliationPredicate.test(newTarget) ? newTarget : null;
                }
            }
        }

        if (newTarget == existingTarget) {
            return null;
        } else {
            return this.canAttackPredicate.test(newTarget) ? newTarget : null;
        }
    }
}
