package de.nexusrealms.creaturemod.entities.brain.sensor;

import de.nexusrealms.creaturemod.CreatureMod;
import de.nexusrealms.creaturemod.entities.brain.memory.ModMemories;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;
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
    private static final List<MemoryModuleType<?>> MEMORIES = ObjectArrayList.of(ModMemories.IS_HOME_THREATHENED);

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return MEMORIES;
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
            if(!entities.isEmpty()){
                BrainUtils.setMemory(entity, ModMemories.IS_HOME_THREATHENED, Unit.INSTANCE);
            }
        });

    }
}
