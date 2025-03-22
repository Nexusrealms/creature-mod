package de.nexusrealms.creaturemod;

import de.nexusrealms.creaturemod.curses.CurseComponent;
import de.nexusrealms.creaturemod.curses.TherianthropyComponent;
import net.minecraft.entity.LivingEntity;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistryV3;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;

public class ModEntityComponents implements EntityComponentInitializer {
    public static final ComponentKey<CurseComponent> CURSES = ComponentRegistryV3.INSTANCE.getOrCreate(CreatureMod.id("curses"), CurseComponent.class);
    public static final ComponentKey<TherianthropyComponent> THERIANTHROPY = ComponentRegistryV3.INSTANCE.getOrCreate(CreatureMod.id("therianthropy"), TherianthropyComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry entityComponentFactoryRegistry) {
        entityComponentFactoryRegistry.registerFor(LivingEntity.class, CURSES, CurseComponent::new);
        entityComponentFactoryRegistry.registerFor(LivingEntity.class, THERIANTHROPY, living -> new TherianthropyComponent());

    }
}
