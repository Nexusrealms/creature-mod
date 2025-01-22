package de.nexusrealms.creaturemod.entities.brain.memory;

import com.mojang.serialization.Codec;
import de.nexusrealms.creaturemod.CreatureMod;
import de.nexusrealms.creaturemod.entities.brain.sensor.EntitiesAroundGuardedPositionSensor;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Unit;
import net.minecraft.util.dynamic.Codecs;

import java.util.Optional;

public class ModMemories {
    public static final MemoryModuleType<Unit> IS_HOME_THREATHENED = create("is_home_threathened", Unit.CODEC);

    private static <T> MemoryModuleType<T> create(String name, Codec<T> codec){
        return Registry.register(Registries.MEMORY_MODULE_TYPE, CreatureMod.id(name), new MemoryModuleType<>(Optional.of(codec)));
    }
    public static void init(){}
}
