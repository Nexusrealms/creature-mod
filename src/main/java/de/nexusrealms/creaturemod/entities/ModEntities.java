package de.nexusrealms.creaturemod.entities;

import de.nexusrealms.creaturemod.CreatureMod;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.function.Supplier;

public class ModEntities {
    public static final EntityType<BearEntity> BEAR = createWithSpawnEgg("bear", EntityType.Builder.create(BearEntity::new, SpawnGroup.CREATURE)
            .dimensions(2f, 1.5f).build(), 0xFFFFFF, 0x000000, BearEntity::getDefaultAttributes);
    private static <T extends MobEntity> EntityType<T> createWithSpawnEgg(String name,
                                                                         EntityType<T> entityType,
                                                                         int primaryColor,
                                                                         int secondaryColor,
                                                                         Supplier<DefaultAttributeContainer> containerSupplier){
        return createWithSpawnEgg(name, entityType, () -> new SpawnEggItem(entityType, primaryColor, secondaryColor,
                new Item.Settings()), containerSupplier);
    }
    private static <T extends MobEntity> EntityType<T> createWithSpawnEgg(String name,
                                                                         EntityType<T> entityType,
                                                                         Supplier<SpawnEggItem> eggItemSupplier,
                                                                         Supplier<DefaultAttributeContainer> containerSupplier){
        Registry.register(Registries.ITEM, CreatureMod.id(name + "spawn_egg"), eggItemSupplier.get());
        return create(name, entityType, containerSupplier);
    }
    private static <T extends LivingEntity> EntityType<T> create(String name,
                                                                EntityType<T> entityType,
                                                                Supplier<DefaultAttributeContainer> containerSupplier){
        FabricDefaultAttributeRegistry.register(entityType, containerSupplier.get());
        return create(name, entityType);
    }
    private static <T extends Entity> EntityType<T> create(String name, EntityType<T> entityType){
        return Registry.register(Registries.ENTITY_TYPE, CreatureMod.id(name), entityType);
    }
    public static void init(){}
}
