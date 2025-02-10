package de.nexusrealms.creaturemod.datagen;

import de.nexusrealms.creaturemod.blocks.ModBlocks;
import de.nexusrealms.creaturemod.entities.ModEntities;
import de.nexusrealms.creaturemod.items.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.CropBlock;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.Models;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class ModelProvider extends FabricModelProvider {
    private static final Model SPAWN_EGG_MODEL = new Model(Optional.of(Identifier.ofVanilla("item/template_spawn_egg")), Optional.empty());
    protected ModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerCrop(ModBlocks.GARLIC, CropBlock.AGE, 0, 1, 2, 3, 4, 5, 6, 7);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        generateSpawnEggModels(itemModelGenerator);
        itemModelGenerator.register(ModItems.GARLIC_CLOVE, Models.GENERATED);
    }
    private void generateSpawnEggModels(ItemModelGenerator itemModelGenerator){
        makeSpawnEgg(itemModelGenerator, ModEntities.BEAR);
        makeSpawnEgg(itemModelGenerator, ModEntities.WRAITH);
        makeSpawnEgg(itemModelGenerator, ModEntities.WEREBEAR);
        makeSpawnEgg(itemModelGenerator, ModEntities.WEREWOLF);
        makeSpawnEgg(itemModelGenerator, ModEntities.AIR_ELEMENTAL);
    }
    private void makeSpawnEgg(ItemModelGenerator generator, EntityType<?> entityType){
        if(SpawnEggItem.forEntity(entityType) instanceof SpawnEggItem item){
            generator.register(item, SPAWN_EGG_MODEL);
        }
    }
}
