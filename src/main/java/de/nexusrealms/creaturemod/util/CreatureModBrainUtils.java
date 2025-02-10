package de.nexusrealms.creaturemod.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.function.Predicate;

public class CreatureModBrainUtils {
    public static <T> boolean hasMemoryConditional(LivingEntity entity, MemoryModuleType<T> memory, Predicate<T> predicate) {
        return hasMemoryConditional(entity.getBrain(), memory, predicate);
    }

    public static <T> boolean hasMemoryConditional(Brain<?> brain, MemoryModuleType<T> memory, Predicate<T> predicate) {
        if(BrainUtils.hasMemory(brain, memory)){
            return predicate.test(BrainUtils.getMemory(brain, memory));
        }
        return false;
    }
}
