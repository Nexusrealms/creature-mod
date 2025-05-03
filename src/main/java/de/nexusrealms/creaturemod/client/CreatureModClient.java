package de.nexusrealms.creaturemod.client;

import de.nexusrealms.creaturemod.client.render.entity.WaterElementalRenderer;
import de.nexusrealms.creaturemod.entities.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.EmptyEntityRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import org.lwjgl.glfw.GLFW;
import software.bernie.geckolib.animatable.GeoEntity;

public class CreatureModClient implements ClientModInitializer {
    private static KeyBinding openIncantation;
    @Override
    public void onInitializeClient() {
        initEntityRenderers();
        initItemColors();
        initKeyBinds();
    }
    private void initKeyBinds(){
        openIncantation = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.creature.incantation", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_I, // The keycode of the key
                "category.creature-mod.magic" // The translation key of the keybinding's category.
        ));
    }
    private void initEntityRenderers(){
        registerSimpleEntityRenderer(ModEntities.BEAR);
        registerSimpleEntityRenderer(ModEntities.WEREWOLF);
        registerSimpleEntityRenderer(ModEntities.WEREBEAR);
        registerSimpleEntityRenderer(ModEntities.WRAITH);
        registerSimpleEntityRenderer(ModEntities.AIR_ELEMENTAL);
        EntityRendererRegistry.register(ModEntities.WATER_ELEMENTAL, WaterElementalRenderer::new);
        registerSimpleEntityRenderer(ModEntities.WERECAT);
        registerSimpleEntityRenderer(ModEntities.WEREBAT);

    }
    private void initItemColors(){

    }
    private <T extends Entity & GeoEntity> void registerSimpleEntityRenderer(EntityType<T> entityType){
        EntityRendererRegistry.register(entityType, ctx -> new SimpleGeoEntityRenderer<>(ctx, () -> Registries.ENTITY_TYPE.getId(entityType).getPath(), SingleVariantGeoEntityModel::new));
    }
    private <T extends Entity> void registerEmptyEntityRenderer(EntityType<T> entityType){
        EntityRendererRegistry.register(entityType, EmptyEntityRenderer::new);
    }
}
