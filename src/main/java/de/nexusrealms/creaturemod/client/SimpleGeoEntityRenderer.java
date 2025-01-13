package de.nexusrealms.creaturemod.client;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKey;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.function.Function;
import java.util.function.Supplier;

public class SimpleGeoEntityRenderer<T extends Entity & GeoEntity> extends GeoEntityRenderer<T> {
    public SimpleGeoEntityRenderer(EntityRendererFactory.Context renderManager, Supplier<String> nameSupplier, Function<String, SingleVariantGeoEntityModel<T>> modelFactory) {
        super(renderManager, modelFactory.apply(nameSupplier.get()));
    }
}
