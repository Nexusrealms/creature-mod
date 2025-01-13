package de.nexusrealms.creaturemod.client;

import de.nexusrealms.creaturemod.CreatureMod;
import de.nexusrealms.creaturemod.entities.WerewolfEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.model.GeoModel;

public class SingleVariantGeoEntityModel<T extends Entity & GeoEntity> extends GeoModel<T> {
    private final String name;

    public SingleVariantGeoEntityModel(String name) {
        this.name = name;
    }
    @Override
    public Identifier getModelResource(T entity) {
        return CreatureMod.geoModelId(name);
    }

    @Override
    public Identifier getTextureResource(T entity) {
        return CreatureMod.entityTextureId(name);
    }

    @Override
    public Identifier getAnimationResource(T entity) {
        return CreatureMod.geoAnimId(name);
    }
}
