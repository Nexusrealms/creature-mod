package de.nexusrealms.creaturemod.entities.brain;

import de.nexusrealms.creaturemod.CreatureMod;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;

import java.util.function.UnaryOperator;

public class ModActivities {
    public static final Activity EAT_HONEY = create("eat_honey");
    private static Activity create(String name) {
        Identifier id = CreatureMod.id(name);
        return Registry.register(Registries.ACTIVITY, id, new Activity(id.toString()));
    }
    public static void init(){}

    public static <T extends LivingEntity & SmartBrainOwner<T>> BrainActivityGroup<T> createActivityGroup(Activity activity, UnaryOperator<BrainActivityGroup<T>> updater, MultiTickTask... behaviours) {
        return updater.apply((new BrainActivityGroup(activity)).behaviours(behaviours));
    }
}
