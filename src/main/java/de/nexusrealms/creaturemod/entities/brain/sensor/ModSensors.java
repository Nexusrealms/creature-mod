package de.nexusrealms.creaturemod.entities.brain.sensor;

import de.nexusrealms.creaturemod.CreatureMod;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModSensors {
    public static final SensorType<EntitiesAroundGuardedPositionSensor<?>> ENTITES_AROUND_GUARDED_POSITION = create("entities_around_guarded_position", new SensorType<>(EntitiesAroundGuardedPositionSensor::new));
    private static <T extends Sensor<?>> SensorType<T> create(String name, SensorType<T> type){
        return Registry.register(Registries.SENSOR_TYPE, CreatureMod.id(name), type);
    }
    public static void init(){}
}
