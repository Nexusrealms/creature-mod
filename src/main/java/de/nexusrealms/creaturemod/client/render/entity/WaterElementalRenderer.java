package de.nexusrealms.creaturemod.client.render.entity;

import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.Color;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class WaterElementalRenderer <T extends Entity & GeoEntity> extends GeoEntityRenderer<T> {

    public WaterElementalRenderer(EntityRendererFactory.Context renderManager, GeoModel<T> model) {
        super(renderManager, model);
    }

    @Override
    public Identifier getTexture(T animatable) {
        return Identifier.ofVanilla("textures/block/water_still.png");
    }

    @Override
    public Color getRenderColor(T animatable, float partialTick, int packedLight) {
        return Color.ofOpaque(BiomeColors.getWaterColor(animatable.getWorld(), animatable.getBlockPos()));
    }

    @Override
    public @Nullable RenderLayer getRenderType(T animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return RenderLayer.getEntityTranslucent(texture);
    }
}
