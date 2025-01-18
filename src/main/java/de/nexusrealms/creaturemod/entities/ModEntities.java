package de.nexusrealms.creaturemod.entities;

import de.nexusrealms.creaturemod.CreatureMod;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.function.Supplier;

public class ModEntities {
    public static final EntityType<BearEntity> BEAR = createWithSpawnEgg("bear", EntityType.Builder.create(BearEntity::new, SpawnGroup.CREATURE)
            .dimensions(1.2f, 1f).build(), 0xb77635, 0x63401c, BearEntity::getDefaultAttributes);
    public static final EntityType<WerewolfEntity> WEREWOLF = createWithSpawnEgg("werewolf", EntityType.Builder.create(WerewolfEntity::new, SpawnGroup.CREATURE)
            .dimensions(1.2f, 1f).build(), 0xFFFFFF, 0x000000, WerewolfEntity::getDefaultAttributes);
    public static final EntityType<WerebearEntity> WEREBEAR = createWithSpawnEgg("werebear", EntityType.Builder.create(WerebearEntity::new, SpawnGroup.CREATURE)
            .dimensions(1.2f, 1f).build(), 0xb77635, 0x000000, WerebearEntity::getDefaultAttributes);
    private static <T extends MobEntity> EntityType<T> createWithSpawnEgg(String name,
                                                                         EntityType<T> entityType,
                                                                         int primaryColor,
                                                                         int secondaryColor,
                                                                         Supplier<DefaultAttributeContainer> containerSupplier){
        return createWithSpawnEgg(name, entityType, new SpawnEggItem(entityType, primaryColor, secondaryColor,
                new Item.Settings()), containerSupplier);
    }
    private static <T extends MobEntity> EntityType<T> createWithSpawnEgg(String name,
                                                                         EntityType<T> entityType,
                                                                         SpawnEggItem eggItemSupplier,
                                                                         Supplier<DefaultAttributeContainer> containerSupplier){

        Registry.register(Registries.ITEM, CreatureMod.id(name + "_spawn_egg"), eggItemSupplier);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(fabricItemGroupEntries -> fabricItemGroupEntries.add(eggItemSupplier));
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
    public static <T extends EntityType<E>, E extends Entity> RegistryEntry.Reference<T> entryOf(T value){
        RegistryEntry.Reference<T> reference = (RegistryEntry.Reference<T>) Registries.ENTITY_TYPE.createEntry(value);
        return reference;
    }
}
