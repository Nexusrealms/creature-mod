package de.nexusrealms.creaturemod.magic.flow;

public interface InventoryFlowStorageProvider {
    default InventoryFlowStorage getInventoryFlowStorage() {
        return null;
    }
}
