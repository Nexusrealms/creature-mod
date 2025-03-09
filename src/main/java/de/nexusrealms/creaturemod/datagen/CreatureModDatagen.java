package de.nexusrealms.creaturemod.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class CreatureModDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(ItemTagProvider::new);
        pack.addProvider(LangProvider::new);
        pack.addProvider(ModelProvider::new);
        pack.addProvider(BlockLootTableProvider::new);
        pack.addProvider(RecipeProvider::new);
    }
}
