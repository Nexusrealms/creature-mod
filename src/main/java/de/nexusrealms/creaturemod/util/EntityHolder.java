package de.nexusrealms.creaturemod.util;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public interface EntityHolder {
    @Nullable
    default Entity getEntity(ServerWorld world){
        if(getStoredEntity() != null){
            return getStoredEntity();
        } else if (getOptionalUUID().isPresent()){
            Entity entity = world.getEntity(getOptionalUUID().get());
            if(entity != null){
                return entity;
            }
        }
        return null;
    }
    @Nullable
    default Optional<Entity> getEntityOptional(ServerWorld world){
        return Optional.ofNullable(getEntity(world));
    }
    boolean has();
    void set(UUID uuid);
    void set(Entity entity);
    Optional<UUID> getOptionalUUID();
    @Nullable
    Entity getStoredEntity();
}
