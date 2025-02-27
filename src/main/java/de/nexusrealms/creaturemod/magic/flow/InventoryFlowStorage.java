package de.nexusrealms.creaturemod.magic.flow;

import com.mojang.serialization.JsonOps;
import de.nexusrealms.creaturemod.items.ModItemComponents;
import de.nexusrealms.creaturemod.magic.element.Element;
import de.nexusrealms.creaturemod.mixin.PlayerInventoryAccessor;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InventoryFlowStorage implements FlowStorage{
    private final PlayerInventory playerInventory;
    private final List<FlowUnit> cachedFlowUnits = new ArrayList<>();
    public InventoryFlowStorage(PlayerInventory player){
        this.playerInventory = player;
        cacheFlowUnits();
    }
    private void cacheFlowUnits(){
        cachedFlowUnits.clear();
        ((PlayerInventoryAccessor) playerInventory).getCombinedInventory().forEach(lists -> lists.forEach(stack -> {
            if(stack.contains(ModItemComponents.STORED_FLOW)){
                cachedFlowUnits.add(stack.get(ModItemComponents.STORED_FLOW));
            }
        }));
    }
    @Override
    public boolean addFlow(FlowUnit flow) {
        List<ItemStack> list = new ArrayList<>();
        ((PlayerInventoryAccessor) playerInventory).getCombinedInventory().forEach(list::addAll);
        Optional<ItemStack> itemStack = list.stream().filter(stack -> stack.contains(ModItemComponents.FLOW_CAPACITY)).filter(stack -> {
            if(stack.contains(ModItemComponents.STORED_FLOW)){
                FlowUnit stored = stack.get(ModItemComponents.STORED_FLOW);
                if(stored.elementEquals(flow)){
                    return false;
                } else {
                    return stored.getValue() + flow.getValue() <= stack.get(ModItemComponents.FLOW_CAPACITY);
                }
            }
            return true;
        }).findFirst();
        if(itemStack.isPresent()){
            itemStack.get().apply(ModItemComponents.STORED_FLOW, new FlowUnit.Immutable(flow.getElement(), 0),flow, FlowUnit::combine);
            return true;
        }
        return false;
    }

    @Override
    public FlowUnit getFlow(RegistryEntry<Element> elementRegistryEntry) {
        return cachedFlowUnits.stream().filter(flowUnit -> flowUnit.elementEquals(elementRegistryEntry)).reduce(new FlowUnit.Immutable(elementRegistryEntry, 0), FlowUnit::combine);
    }

    @Override
    public String dumpFlow() {
        return FlowUnit.LIST_CODEC.encodeStart(JsonOps.INSTANCE, cachedFlowUnits).getOrThrow().getAsString();
    }

    @Override
    public boolean drainFlow(FlowUnit flowUnit) {
        //TODO add this im too tired to think
        return false;
    }

    @Override
    public boolean canDrain(FlowUnit flowUnit) {
        //TODO add this im too tired to think
        return false;
    }
}
