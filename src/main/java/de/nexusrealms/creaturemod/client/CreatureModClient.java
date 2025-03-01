package de.nexusrealms.creaturemod.client;

import de.nexusrealms.creaturemod.CreatureMod;
import de.nexusrealms.creaturemod.client.render.entity.WaterElementalRenderer;
import de.nexusrealms.creaturemod.entities.ModEntities;
import de.nexusrealms.creaturemod.entities.elemental.WaterElementalEntity;
import de.nexusrealms.creaturemod.items.ModItemComponents;
import de.nexusrealms.creaturemod.items.ModItems;
import de.nexusrealms.creaturemod.magic.element.Element;
import de.nexusrealms.creaturemod.magic.flow.FlowUnit;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EmptyEntityRenderer;
import net.minecraft.data.client.ModelIds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;

public class CreatureModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        initEntityRenderers();
        initItemColors();
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
        registerSimpleEntityRenderer(ModEntities.WEREBAT);

    }
    private void initItemColors(){
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            if(tintIndex != 1) return 0xFFFFFFFF;
            if(stack.contains(ModItemComponents.STORED_FLOW)){
                FlowUnit flowUnit = stack.get(ModItemComponents.STORED_FLOW);
                int color = flowUnit.getElement().value().color();
                float alpha = (float) flowUnit.getValue() / (float) stack.get(ModItemComponents.FLOW_CAPACITY);
                //Flat circle function
                alpha = (float) (0.75 * MathHelper.sqrt(2 * alpha - alpha * alpha));
                return ColorHelper.Argb.withAlpha((int) (alpha * 255), color);
            }
            return 0x000000;
        }, ModItems.ELEMENT_FLASK);
    }
    private <T extends Entity & GeoEntity> void registerSimpleEntityRenderer(EntityType<T> entityType){
        EntityRendererRegistry.register(entityType, ctx -> new SimpleGeoEntityRenderer<>(ctx, () -> Registries.ENTITY_TYPE.getId(entityType).getPath(), SingleVariantGeoEntityModel::new));
    }
    private <T extends Entity> void registerEmptyEntityRenderer(EntityType<T> entityType){
        EntityRendererRegistry.register(entityType, EmptyEntityRenderer::new);
    }
}
