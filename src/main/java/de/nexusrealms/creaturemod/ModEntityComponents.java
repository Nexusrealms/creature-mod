package de.nexusrealms.creaturemod;

import de.nexusrealms.creaturemod.curses.CurseComponent;
import de.nexusrealms.creaturemod.curses.TherianthropyComponent;
import de.nexusrealms.creaturemod.magic.flow.PlayerFlowStorageComponent;
import de.nexusrealms.creaturemod.magic.spell.CastDelayComponent;
import de.nexusrealms.creaturemod.magic.spell.UnlockedSpellsComponents;
import net.minecraft.entity.LivingEntity;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistryV3;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;

public class ModEntityComponents implements EntityComponentInitializer {
    public static final ComponentKey<CurseComponent> CURSES = ComponentRegistryV3.INSTANCE.getOrCreate(CreatureMod.id("curses"), CurseComponent.class);
    public static final ComponentKey<TherianthropyComponent> THERIANTHROPY = ComponentRegistryV3.INSTANCE.getOrCreate(CreatureMod.id("therianthropy"), TherianthropyComponent.class);
    public static final ComponentKey<PlayerFlowStorageComponent> PLAYER_FLOW_STORAGE = ComponentRegistryV3.INSTANCE.getOrCreate(CreatureMod.id("player_flow_storage"), PlayerFlowStorageComponent.class);
    public static final ComponentKey<CastDelayComponent> CAST_DELAY_COMPONENT = ComponentRegistryV3.INSTANCE.getOrCreate(CreatureMod.id("cast_delay"), CastDelayComponent.class);
    public static final ComponentKey<UnlockedSpellsComponents> UNLOCKED_SPELLS_COMPONENT = ComponentRegistryV3.INSTANCE.getOrCreate(CreatureMod.id("unlocked_spells"), UnlockedSpellsComponents.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry entityComponentFactoryRegistry) {
        entityComponentFactoryRegistry.registerFor(LivingEntity.class, CURSES, CurseComponent::new);
        entityComponentFactoryRegistry.registerFor(LivingEntity.class, THERIANTHROPY, living -> new TherianthropyComponent());
        entityComponentFactoryRegistry.registerForPlayers(PLAYER_FLOW_STORAGE, PlayerFlowStorageComponent::new);
        entityComponentFactoryRegistry.registerForPlayers(CAST_DELAY_COMPONENT, player -> new CastDelayComponent());
        entityComponentFactoryRegistry.registerForPlayers(UNLOCKED_SPELLS_COMPONENT, player -> new UnlockedSpellsComponents());

    }
}
