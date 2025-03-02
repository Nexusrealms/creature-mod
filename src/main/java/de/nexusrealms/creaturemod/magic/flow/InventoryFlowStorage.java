package de.nexusrealms.creaturemod.magic.flow;

import com.mojang.serialization.JsonOps;
import de.nexusrealms.creaturemod.CreatureMod;
import de.nexusrealms.creaturemod.items.ModItemComponents;
import de.nexusrealms.creaturemod.magic.element.Element;
import de.nexusrealms.creaturemod.mixin.PlayerInventoryAccessor;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.Collections;
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
        List<ItemStack> list = getAllStacks();
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
            cacheFlowUnits();
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
        cacheFlowUnits();
        if(cachedFlowUnits.isEmpty()){
            return "No flow in inventory";
        }
        return FlowUnit.LIST_CODEC.encodeStart(JsonOps.INSTANCE, cachedFlowUnits).getOrThrow().toString();
    }

    @Override
    public boolean drainFlow(FlowUnit flowUnit) {
        if(canDrain(flowUnit)){
            if(drainFlowFromItem(playerInventory.getMainHandStack(), flowUnit)){
                cacheFlowUnits();
                return true;
            } else if (drainFlowFromItem(playerInventory.offHand.getFirst(), flowUnit)){
                cacheFlowUnits();
                return true;
            } else {
                List<ItemStack> list = getAllStacks();
                List<ItemStack> filtered = list.stream()
                        .filter(stack -> stack.contains(ModItemComponents.STORED_FLOW))
                        .filter(stack -> stack.get(ModItemComponents.STORED_FLOW).elementEquals(flowUnit))
                        .toList();
                int flowToDrain = flowUnit.getValue();
                for (ItemStack stack : filtered){
                    int storedFlow = stack.get(ModItemComponents.STORED_FLOW).getValue();
                    if(flowToDrain >= storedFlow){
                        stack.remove(ModItemComponents.STORED_FLOW);
                        flowToDrain -= storedFlow;
                    } else {
                        stack.set(ModItemComponents.STORED_FLOW, new FlowUnit.Immutable(flowUnit.getElement(), storedFlow - flowToDrain));
                        cacheFlowUnits();
                        return true;
                    }
                }
                CreatureMod.LOGGER.error("Inventory did not have enough flow even though canDrain passed.\n" +
                        "Should be unreachable. We offer no refunds for lost flow.");
            }
        }
        return false;
    }
    private List<ItemStack> getAllStacks(){
        return ((PlayerInventoryAccessor) playerInventory).getCombinedInventory().stream().flatMap(List::stream).toList();
    }
    private boolean drainFlowFromItem(ItemStack stack, FlowUnit flowUnit){
        if(stack == null) return false;
        FlowUnit stored = stack.get(ModItemComponents.STORED_FLOW);
        if(stored != null){
            if(stored.elementEquals(flowUnit)){
                if(stored.getValue() >= flowUnit.getValue()){
                    stack.set(ModItemComponents.STORED_FLOW, new FlowUnit.Immutable(stored.getElement(), stored.getValue() - flowUnit.getValue()));
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public boolean canDrain(FlowUnit flowUnit) {
        List<ItemStack> list = getAllStacks();
        int storedFlow = list.stream().filter(stack -> stack.contains(ModItemComponents.STORED_FLOW))
                .map(stack -> stack.get(ModItemComponents.STORED_FLOW))
                .filter(flowUnit1 -> flowUnit1.elementEquals(flowUnit))
                .mapToInt(FlowUnit::getValue)
                .sum();
        return storedFlow >= flowUnit.getValue();
    }
}
