package de.nexusrealms.creaturemod.magic.flow;

import com.mojang.serialization.JsonOps;
import de.nexusrealms.creaturemod.magic.MagicChecks;
import de.nexusrealms.creaturemod.magic.element.Element;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;
import org.ladysnake.cca.api.v3.entity.RespawnableComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Flow;

public class PlayerFlowStorageComponent implements AutoSyncedComponent, ServerTickingComponent, RespawnableComponent<PlayerFlowStorageComponent> {
    private final PlayerEntity player;
    private Map<RegistryEntry<Element>, FlowUnit.Mutable> map = new HashMap<>();

    public PlayerFlowStorageComponent(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        map = FlowUnit.MAP_STORAGE_CODEC.parse(NbtOps.INSTANCE, nbtCompound.get("map")).getOrThrow();
    }
    private FlowUnit.Mutable getFlow(RegistryEntry<Element> element){
        return map.computeIfAbsent(element, entry -> new FlowUnit.Mutable(entry, 0));
    }
    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        nbtCompound.put("map", FlowUnit.MAP_STORAGE_CODEC.encodeStart(NbtOps.INSTANCE, map).getOrThrow());
    }
    public FlowUnit getFlowAmount(RegistryEntry<Element> element){
        return getFlow(element).toImmutable();
    }
    public void addFlow(FlowUnit flow){
        FlowUnit.Mutable mutable = getFlow(flow.getElement());
        mutable.add(flow.getValue());
    }
    @Override
    public void serverTick() {
        int factor = MagicChecks.getComponentDissipationFactor(player);
        if(factor < 0){
            FlowUnit.Mutable.updateAllValues(map, mutable -> mutable.subtract(mutable.getElement().value().baseDissipation() + factor));
        }
    }
    public String dumpFlow(){
        return FlowUnit.MAP_STORAGE_CODEC.encodeStart(JsonOps.INSTANCE, map).getOrThrow().toString();
    }
}
