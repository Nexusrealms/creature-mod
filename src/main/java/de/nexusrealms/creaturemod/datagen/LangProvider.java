package de.nexusrealms.creaturemod.datagen;

import de.nexusrealms.creaturemod.blocks.ModBlocks;
import de.nexusrealms.creaturemod.entities.ModEntities;
import de.nexusrealms.creaturemod.items.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class LangProvider extends FabricLanguageProvider {
    protected LangProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        // Specifying en_us is optional, as it's the default language code
        super(dataOutput, "en_us", registryLookup);
    }
    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        generateEntityTranslations(wrapperLookup, translationBuilder);
        generateItemTranslations(wrapperLookup, translationBuilder);
        generateMessageTranslations(wrapperLookup, translationBuilder);
        generateSpellTranslations(wrapperLookup, translationBuilder);
        generateItemGroupTranslations(wrapperLookup, translationBuilder);
    }
    private void generateBlockTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder){
        translationBuilder.add(ModBlocks.GARLIC, "Garlic Plant");
    }
    private void generateItemTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder){
        translationBuilder.add(ModItems.GARLIC_CLOVE, "Garlic Clove");
        translationBuilder.add(ModItems.GARLIC_BULB, "Garlic Bulb");
        translationBuilder.add(ModItems.ELEMENT_FLASK, "Element Flask");
    }
    private void generateEntityTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder){
        generateEntityTranslationWithSpawnEgg(wrapperLookup, translationBuilder, ModEntities.WEREWOLF, "Werewolf");
        generateEntityTranslationWithSpawnEgg(wrapperLookup, translationBuilder, ModEntities.WEREBEAR, "Werebear");
        generateEntityTranslationWithSpawnEgg(wrapperLookup, translationBuilder, ModEntities.WERECAT, "Werecat");
        generateEntityTranslationWithSpawnEgg(wrapperLookup, translationBuilder, ModEntities.WEREBAT, "Werebat");
        generateEntityTranslationWithSpawnEgg(wrapperLookup, translationBuilder, ModEntities.WRAITH, "Wraith");
        generateEntityTranslationWithSpawnEgg(wrapperLookup, translationBuilder, ModEntities.BEAR, "Bear");
        generateEntityTranslationWithSpawnEgg(wrapperLookup, translationBuilder, ModEntities.AIR_ELEMENTAL, "Air Elemental");
    }
    private void generateEntityTranslationWithSpawnEgg(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder, EntityType<?> entityType, String name){
        translationBuilder.add(entityType, name);
        if(SpawnEggItem.forEntity(entityType) instanceof SpawnEggItem item){
            translationBuilder.add(item, name + " Spawn Egg");
        }
    }
    private void generateMessageTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder){
        translationBuilder.add("message.creature-mod.curse.remove", "The curse %s has been removed from ");
        translationBuilder.add("message.creature-mod.curse.nonliving", "Curses can't be applied to non-living entities!");
        translationBuilder.add("message.creature-mod.curse.none", "%s has no curses!");
        translationBuilder.add("message.creature-mod.curse.apply", "The curse %s has been applied to %s!");
        translationBuilder.add("message.creature-mod.flow.add", "Flow of element %s of value %s has been added to %s players");
        translationBuilder.add("message.creature-mod.flow.add.item", "Flow of element %s of value %s has been added to ");
        translationBuilder.add("message.creature-mod.spell.cast", "Cast spell %s!");
        translationBuilder.add("message.creature-mod.spell.notfound", "Could not find any spell for incantation %s!");
    }
    private void generateSpellTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder){
        translationBuilder.add("spell.creature-mod.test", "Test spell");

    }
    private void generateItemGroupTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder){
        translationBuilder.add("itemgroup.creature-mod.magical", "Magical items");

    }
}
