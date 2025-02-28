package de.nexusrealms.creaturemod.magic.flow;

import de.nexusrealms.creaturemod.ModEntityComponents;
import de.nexusrealms.creaturemod.magic.MagicUtils;
import de.nexusrealms.creaturemod.magic.element.Element;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.Optional;

public interface FlowStorage {
    boolean addFlow(FlowUnit flow);
    FlowUnit getFlow(RegistryEntry<Element> elementRegistryEntry);
    String dumpFlow();
    boolean drainFlow(FlowUnit flowUnit);
    boolean canDrain(FlowUnit flowUnit);

    static Optional<FlowStorage> getPreferredFlowStorage(PlayerEntity player){
        if(MagicUtils.shouldDoSorcery(player)) return Optional.of(player.getComponent(ModEntityComponents.PLAYER_FLOW_STORAGE));
        return Optional.empty();
    }
    static FlowStorage getFlowStorage(PlayerEntity player){
        if(MagicUtils.shouldDoSorcery(player)){
            return new Dual(player.getComponent(ModEntityComponents.PLAYER_FLOW_STORAGE), ((InventoryFlowStorageProvider) player).getInventoryFlowStorage());
        } else {
            return ((InventoryFlowStorageProvider) player).getInventoryFlowStorage();
        }
    }
    record Dual(FlowStorage primary, FlowStorage secondary) implements FlowStorage{

        @Override
        public boolean addFlow(FlowUnit flow) {
            if(primary.addFlow(flow)){
                return true;
            } else {
                return secondary.addFlow(flow);
            }
        }

        @Override
        public FlowUnit getFlow(RegistryEntry<Element> elementRegistryEntry) {
            FlowUnit primaryFlow = primary.getFlow(elementRegistryEntry);
            FlowUnit secondaryFlow = secondary.getFlow(elementRegistryEntry);
            return primaryFlow.combine(secondaryFlow);
        }

        @Override
        public String dumpFlow() {
            return "First storage: " + primary.dumpFlow() +"\nSecond storage: " + secondary.dumpFlow();
        }

        @Override
        public boolean drainFlow(FlowUnit flowUnit) {
            if(primary.drainFlow(flowUnit)){
                return true;
            } else {
                return secondary.drainFlow(flowUnit);
            }
        }

        @Override
        public boolean canDrain(FlowUnit flowUnit) {
            if(primary.canDrain(flowUnit)){
                return true;
            } else {
                return secondary.canDrain(flowUnit);
            }
        }
    }
}
