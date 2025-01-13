package de.nexusrealms.creaturemod;

import de.nexusrealms.creaturemod.curses.CurseComponent;
import de.nexusrealms.creaturemod.curses.TherianthropyComponent;
import de.nexusrealms.creaturemod.curses.TherianthropyCurse;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistryV3;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;

public class ModEntityComponents implements EntityComponentInitializer {
    public static final ComponentKey<CurseComponent> CURSES = ComponentRegistryV3.INSTANCE.getOrCreate(CreatureMod.id("curses"), CurseComponent.class);
    public static final ComponentKey<TherianthropyComponent> THERIANTHROPY = ComponentRegistryV3.INSTANCE.getOrCreate(CreatureMod.id("therianthropy"), TherianthropyComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry entityComponentFactoryRegistry) {
        entityComponentFactoryRegistry.registerFor(LivingEntity.class, CURSES, CurseComponent::new);
        entityComponentFactoryRegistry.registerForPlayers(THERIANTHROPY, player -> new TherianthropyComponent(), RespawnCopyStrategy.NEVER_COPY);

    }
}
