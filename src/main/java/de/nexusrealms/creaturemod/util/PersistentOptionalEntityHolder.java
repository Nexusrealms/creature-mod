package de.nexusrealms.creaturemod.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.Uuids;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class PersistentOptionalEntityHolder implements EntityHolder{
    public static final Codec<PersistentOptionalEntityHolder> PERSISTENT_OPTIONAL_CODEC = Uuids.CODEC.optionalFieldOf("holderId").xmap(PersistentOptionalEntityHolder::new, PersistentOptionalEntityHolder::getOptionalUUID).codec();
    public PersistentOptionalEntityHolder(){

    }
    public PersistentOptionalEntityHolder(Optional<UUID> uuid){
        this.uuid = uuid;
    }
    public PersistentOptionalEntityHolder(UUID uuid){
        this.uuid = Optional.of(uuid);
    }
    public PersistentOptionalEntityHolder(Entity entity){
        this.storedEntity = entity;
        this.uuid = Optional.of(entity.getUuid());
    }
    private Optional<UUID> uuid = Optional.empty();
    @Nullable
    private Entity storedEntity;

    @Override
    public boolean has() {
        return uuid.isPresent();
    }

    @Override
    public void set(UUID uuid) {
        this.uuid = Optional.of(uuid);
    }

    @Override
    public void set(Entity entity) {
        set(entity.getUuid());
        storedEntity = entity;
    }

    @Override
    public Optional<UUID> getOptionalUUID() {
        return uuid;
    }

    @Override
    public @Nullable Entity getStoredEntity() {
        return storedEntity;
    }
}
