package de.nexusrealms.creaturemod.curses;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.Component;

import java.util.Optional;
import java.util.UUID;

public class TherianthropyComponent implements Component {
    private Optional<UUID> uuid = Optional.empty();
    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        if(nbtCompound.contains("uuid")){
            uuid = Optional.of(nbtCompound.getUuid("uuid"));
        }
    }
    public Optional<UUID> getUuid(){
        return uuid;
    }
    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        uuid.ifPresent(value -> nbtCompound.putUuid("uuid", value));
    }
    public void setEntity(@Nullable Entity entity){
        if(entity == null){
            uuid = Optional.empty();
        } else {
            uuid = Optional.of(entity.getUuid());
        }
    }
}
