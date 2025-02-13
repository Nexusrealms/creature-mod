package de.nexusrealms.creaturemod.datagen;

import de.nexusrealms.creaturemod.CreatureMod;
import de.nexusrealms.creaturemod.items.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class RecipeProvider extends FabricRecipeProvider {
    public RecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter recipeExporter) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.GARLIC_CLOVE, 8)
                .input(ModItems.GARLIC_BULB)
                .criterion(FabricRecipeProvider.hasItem(ModItems.GARLIC_CLOVE), FabricRecipeProvider.conditionsFromItem(ModItems.GARLIC_CLOVE))
                .offerTo(recipeExporter);
    }
}
