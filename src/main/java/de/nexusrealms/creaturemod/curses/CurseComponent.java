package de.nexusrealms.creaturemod.curses;

import com.mojang.serialization.DataResult;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CurseComponent implements AutoSyncedComponent, ServerTickingComponent {
    private final LivingEntity holder;
    private List<CurseInstance> instances = new ArrayList<>();
    public CurseComponent(LivingEntity living){
        holder = living;
    }
    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        NbtList list = nbtCompound.getList("curses", NbtElement.LIST_TYPE);
        instances =  list.stream().map(nbtElement -> CurseInstance.CODEC.parse(NbtOps.INSTANCE, nbtElement)).filter(DataResult::isSuccess).map(DataResult::getOrThrow).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        NbtList list = new NbtList();
        instances.stream().map(instance -> CurseInstance.CODEC.encodeStart(NbtOps.INSTANCE, instance)).filter(DataResult::isSuccess).map(DataResult::getOrThrow).forEach(list::add);
        nbtCompound.put("curses", list);
    }

    @Override
    public void serverTick() {
        instances.stream().filter(CurseInstance::shouldActuallyTick).forEach(instance -> instance.tick(holder));
    }
    public void addCurse(CurseInstance instance){
        instances.add(instance);
    }
    public void removeCurses(Predicate<CurseInstance> instance){
        instances.removeIf(instance);
    }
    public List<CurseInstance> getCurses(){
        return instances;
    }
}
