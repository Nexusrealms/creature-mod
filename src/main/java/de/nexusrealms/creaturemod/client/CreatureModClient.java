package de.nexusrealms.creaturemod.client;

import de.nexusrealms.creaturemod.CreatureMod;
import de.nexusrealms.creaturemod.client.render.entity.WaterElementalRenderer;
import de.nexusrealms.creaturemod.entities.ModEntities;
import de.nexusrealms.creaturemod.entities.elemental.WaterElementalEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EmptyEntityRenderer;
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
        registerEmptyEntityRenderer(ModEntities.AIR_ELEMENTAL);
        EntityRendererRegistry.register(ModEntities.WATER_ELEMENTAL,
                ctx -> new WaterElementalRenderer<>(ctx, new SingleVariantGeoEntityModel<>("water_elemental")));
        registerSimpleEntityRenderer(ModEntities.WERECAT);

    }
    private <T extends Entity & GeoEntity> void registerSimpleEntityRenderer(EntityType<T> entityType){
        EntityRendererRegistry.register(entityType, ctx -> new SimpleGeoEntityRenderer<>(ctx, () -> Registries.ENTITY_TYPE.getId(entityType).getPath(), SingleVariantGeoEntityModel::new));
    }
    private <T extends Entity> void registerEmptyEntityRenderer(EntityType<T> entityType){
        EntityRendererRegistry.register(entityType, EmptyEntityRenderer::new);
    }
}
