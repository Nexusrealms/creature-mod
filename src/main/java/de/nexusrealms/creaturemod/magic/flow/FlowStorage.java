package de.nexusrealms.creaturemod.magic.flow;

import de.nexusrealms.creaturemod.magic.element.Element;
import net.minecraft.registry.entry.RegistryEntry;

public interface FlowStorage {
    void addFlow(FlowUnit flow);
    FlowUnit getFlow(RegistryEntry<Element> elementRegistryEntry);
    String dumpFlow();
    boolean drainFlow(FlowUnit flowUnit);
    boolean canDrain(FlowUnit flowUnit);
}
