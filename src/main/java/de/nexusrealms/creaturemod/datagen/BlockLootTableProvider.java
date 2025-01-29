package de.nexusrealms.creaturemod.datagen;

import de.nexusrealms.creaturemod.blocks.ModBlocks;
import de.nexusrealms.creaturemod.items.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.AllOfLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class BlockLootTableProvider extends FabricBlockLootTableProvider {
    protected BlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        //TODO Add actual drops
        addDrop(ModBlocks.GARLIC);
    }
}
