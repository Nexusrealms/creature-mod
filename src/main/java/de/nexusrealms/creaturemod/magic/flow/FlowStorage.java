package de.nexusrealms.creaturemod.magic.flow;

import de.nexusrealms.creaturemod.ModEntityComponents;
import de.nexusrealms.creaturemod.magic.MagicUtils;
import de.nexusrealms.creaturemod.magic.element.Element;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.Optional;

public interface FlowStorage {
    void addFlow(FlowUnit flow);
    FlowUnit getFlow(RegistryEntry<Element> elementRegistryEntry);
    String dumpFlow();
    boolean drainFlow(FlowUnit flowUnit);
    boolean canDrain(FlowUnit flowUnit);

    static Optional<FlowStorage> getPreferredFlowStorage(PlayerEntity player){
        if(MagicUtils.shouldDoSorcery(player)) return Optional.of(player.getComponent(ModEntityComponents.PLAYER_FLOW_STORAGE));
        return Optional.empty();
    }
}
