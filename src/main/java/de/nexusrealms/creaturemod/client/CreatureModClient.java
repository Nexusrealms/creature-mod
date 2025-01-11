package de.nexusrealms.creaturemod.client;

import de.nexusrealms.creaturemod.client.render.entity.BearEntityRenderer;
import de.nexusrealms.creaturemod.entities.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class CreatureModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        initEntityRenderers();
    }
    private void initEntityRenderers(){
        EntityRendererRegistry.register(ModEntities.BEAR, BearEntityRenderer::new);
    }
}
