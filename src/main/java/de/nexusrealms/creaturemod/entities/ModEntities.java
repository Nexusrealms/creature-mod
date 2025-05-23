package de.nexusrealms.creaturemod.entities;

import de.nexusrealms.creaturemod.CreatureMod;
import de.nexusrealms.creaturemod.entities.elemental.AirElementalEntity;
import de.nexusrealms.creaturemod.entities.elemental.WaterElementalEntity;
import de.nexusrealms.creaturemod.entities.natural.BearEntity;
import de.nexusrealms.creaturemod.entities.spectral.WraithEntity;
import de.nexusrealms.creaturemod.entities.therianthrope.WerebatEntity;
import de.nexusrealms.creaturemod.entities.therianthrope.WerebearEntity;
import de.nexusrealms.creaturemod.entities.therianthrope.WerecatEntity;
import de.nexusrealms.creaturemod.entities.therianthrope.WerewolfEntity;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
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
import net.minecraft.registry.tag.BiomeTags;

import java.util.function.Supplier;

public class ModEntities {
    public static final EntityType<BearEntity> BEAR = createWithSpawnEgg("bear", EntityType.Builder.create(BearEntity::new, SpawnGroup.CREATURE)
            .dimensions(1.2f, 1f).build(), 0xb77635, 0x63401c, BearEntity::getDefaultAttributes);
    public static final EntityType<WerewolfEntity> WEREWOLF = createWithSpawnEgg("werewolf", EntityType.Builder.create(WerewolfEntity::new, SpawnGroup.MONSTER)
            .dimensions(0.9f, 1.8f).build(), 0x643821, 0xa59381, WerewolfEntity::getDefaultAttributes);
    public static final EntityType<WerebearEntity> WEREBEAR = createWithSpawnEgg("werebear", EntityType.Builder.create(WerebearEntity::new, SpawnGroup.MONSTER)
            .dimensions(1.1f, 2f).build(), 0xb77635, 0x000000, WerebearEntity::getDefaultAttributes);
    public static final EntityType<WerecatEntity> WERECAT = createWithSpawnEgg("werecat", EntityType.Builder.create(WerecatEntity::new, SpawnGroup.MONSTER)
            .dimensions(0.8f, 1.9f).build(), 0xddb475, 0xddb475, WerecatEntity::getDefaultAttributes);
    public static final EntityType<WerebatEntity> WEREBAT = createWithSpawnEgg("werebat", EntityType.Builder.create(WerebatEntity::new, SpawnGroup.MONSTER)
            .dimensions(0.8f, 1.7f).build(), 0x26211f, 0x825f7f, WerebatEntity::getDefaultAttributes);

    public static final EntityType<WraithEntity> WRAITH = createWithSpawnEgg("wraith", EntityType.Builder.create(WraithEntity::new, SpawnGroup.MONSTER)
            .dimensions(0.8f, 1.8f).build(), 0x000000, 0xFF0000, WraithEntity::getDefaultAttributes);
    public static final EntityType<AirElementalEntity> AIR_ELEMENTAL = createWithSpawnEgg("air_elemental", EntityType.Builder.create(AirElementalEntity::new, SpawnGroup.MONSTER)
            .dimensions(1.2f, 1.2f).build(), 0xb4d9e7, 0x7a86c5, AirElementalEntity::getDefaultAttributes);
    public static final EntityType<WaterElementalEntity> WATER_ELEMENTAL = createWithSpawnEgg("water_elemental", EntityType.Builder.create(WaterElementalEntity::new, SpawnGroup.MONSTER)
            .dimensions(0.8f, 0.8f).build(), 0x0cc3ca, 0xf89520, WaterElementalEntity::getDefaultAttributes);

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
    public static void init(){
        BiomeModifications.addSpawn(ctx -> ctx.hasTag(BiomeTags.IS_TAIGA), SpawnGroup.MONSTER, WEREBEAR, 1, 1, 1);
        BiomeModifications.addSpawn(ctx -> ctx.hasTag(BiomeTags.IS_FOREST), SpawnGroup.MONSTER, WEREWOLF, 2, 1, 1);
        BiomeModifications.addSpawn(ctx -> ctx.hasTag(BiomeTags.IS_FOREST), SpawnGroup.CREATURE, BEAR, 6, 1, 4);
        BiomeModifications.addSpawn(ctx -> ctx.hasTag(BiomeTags.IS_JUNGLE), SpawnGroup.MONSTER, WERECAT, 2, 1, 1);
        BiomeModifications.addSpawn(ctx -> ctx.hasTag(BiomeTags.MINESHAFT_HAS_STRUCTURE), SpawnGroup.MONSTER, WEREBAT, 1, 1, 1);
        BiomeModifications.addSpawn(ctx -> ctx.hasTag(BiomeTags.WOODLAND_MANSION_HAS_STRUCTURE), SpawnGroup.MONSTER, WRAITH, 2, 1, 1);


    }
}
