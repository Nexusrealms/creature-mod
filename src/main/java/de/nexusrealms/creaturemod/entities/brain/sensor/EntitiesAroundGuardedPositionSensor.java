package de.nexusrealms.creaturemod.entities.brain.sensor;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.object.FixedNearestVisibleLivingEntities;
import net.tslat.smartbrainlib.object.SquareRadius;
import net.tslat.smartbrainlib.registry.SBLSensors;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class EntitiesAroundGuardedPositionSensor<T extends LivingEntity> extends NearbyLivingEntitySensor<T> {
    public SensorType<? extends ExtendedSensor<?>> type() {
        return ModSensors.ENTITES_AROUND_GUARDED_POSITION;
    }

    @Override
    protected void sense(ServerWorld level, T entity) {
        BrainUtils.withMemory(entity, MemoryModuleType.HOME, globalPos -> {
            SquareRadius radius = this.radius;
            if (radius == null) {
                double dist = entity.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
                radius = new SquareRadius(dist, dist);
            }

            List<LivingEntity> entities = EntityRetrievalUtil.getEntities(entity.getWorld(), Box.from(globalPos.pos().toCenterPos()).expand(radius.xzRadius(), radius.yRadius(), radius.xzRadius()), LivingEntity.class, (target -> target != entity && predicate().test(target, entity)));
            Objects.requireNonNull(entity);
            entities.sort(Comparator.comparingDouble(entity::squaredDistanceTo));
            BrainUtils.setMemory(entity, MemoryModuleType.MOBS, entities);
            BrainUtils.setMemory(entity, MemoryModuleType.VISIBLE_MOBS, new FixedNearestVisibleLivingEntities(entity, entities));
        });

    }
}
