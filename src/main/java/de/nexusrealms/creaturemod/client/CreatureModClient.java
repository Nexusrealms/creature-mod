package de.nexusrealms.creaturemod.client;

import de.nexusrealms.creaturemod.CreatureMod;
import de.nexusrealms.creaturemod.entities.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;

public class CreatureModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        initEntityRenderers();
    }
    private void initEntityRenderers(){
        registerSimpleEntityRenderer(ModEntities.BEAR);
        registerSimpleEntityRenderer(ModEntities.WEREWOLF);
        registerSimpleEntityRenderer(ModEntities.WEREBEAR);
        registerSimpleEntityRenderer(ModEntities.WRAITH);
    }
    private <T extends Entity & GeoEntity> void registerSimpleEntityRenderer(EntityType<T> entityType){
        EntityRendererRegistry.register(entityType, ctx -> new SimpleGeoEntityRenderer<>(ctx, () -> Registries.ENTITY_TYPE.getId(entityType).getPath(), SingleVariantGeoEntityModel::new));
    }

}
