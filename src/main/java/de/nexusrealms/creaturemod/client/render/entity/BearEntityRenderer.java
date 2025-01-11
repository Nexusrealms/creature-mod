package de.nexusrealms.creaturemod.client.render.entity;

import de.nexusrealms.creaturemod.entities.BearEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BearEntityRenderer extends GeoEntityRenderer<BearEntity> {
    public BearEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new BearEntityModel());
    }
}
