package de.nexusrealms.creaturemod.client.render.entity;

import de.nexusrealms.creaturemod.CreatureMod;
import de.nexusrealms.creaturemod.entities.BearEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class BearEntityModel extends GeoModel<BearEntity> {
    @Override
    public Identifier getModelResource(BearEntity bear) {
        return CreatureMod.geoModelId("bear");
    }

    @Override
    public Identifier getTextureResource(BearEntity bear) {
        return CreatureMod.entityTextureId("bear");
    }

    @Override
    public Identifier getAnimationResource(BearEntity bear) {
        return CreatureMod.geoAnimId("bear");
    }
}
