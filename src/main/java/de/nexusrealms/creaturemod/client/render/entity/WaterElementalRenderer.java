package de.nexusrealms.creaturemod.client.render.entity;

import de.nexusrealms.creaturemod.client.SingleVariantGeoEntityModel;
import de.nexusrealms.creaturemod.entities.elemental.WaterElementalEntity;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.Color;

public class WaterElementalRenderer extends GeoEntityRenderer<WaterElementalEntity> {
    private final Outer outer;
    public WaterElementalRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new SingleVariantGeoEntityModel<>("water_elemental"));
        outer = new Outer(renderManager);
    }

    @Override
    public void render(WaterElementalEntity entity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        if(entity.shouldRenderOuter()) outer.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    static class Outer extends GeoEntityRenderer<WaterElementalEntity> {

        public Outer(EntityRendererFactory.Context renderManager) {
            super(renderManager, new SingleVariantGeoEntityModel<>("water_elemental_outer"));
        }
        @Override
        public Identifier getTexture(WaterElementalEntity animatable) {
            return Identifier.ofVanilla("textures/block/water_still.png");
        }

        @Override
        public Color getRenderColor(WaterElementalEntity animatable, float partialTick, int packedLight) {
            return Color.ofOpaque(BiomeColors.getWaterColor(animatable.getWorld(), animatable.getBlockPos()));
        }
        @Override
        public @Nullable RenderLayer getRenderType(WaterElementalEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
            return RenderLayer.getEntityTranslucent(texture);
        }
    }
}
