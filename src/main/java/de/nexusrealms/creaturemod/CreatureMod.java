package de.nexusrealms.creaturemod;

import de.nexusrealms.creaturemod.blocks.ModBlocks;
import de.nexusrealms.creaturemod.curses.Curses;
import de.nexusrealms.creaturemod.entities.ModEntities;
import de.nexusrealms.creaturemod.entities.brain.ModActivities;
import de.nexusrealms.creaturemod.entities.brain.memory.ModMemories;
import de.nexusrealms.creaturemod.entities.brain.sensor.ModSensors;
import de.nexusrealms.creaturemod.items.ModItemComponents;
import de.nexusrealms.creaturemod.items.ModItemGroups;
import de.nexusrealms.creaturemod.items.ModItems;
import de.nexusrealms.creaturemod.magic.element.Elements;
import de.nexusrealms.creaturemod.magic.flow.FlowCostType;
import de.nexusrealms.creaturemod.magic.spell.Spell;
import de.nexusrealms.creaturemod.magic.spell.effect.SpellEffectType;
import de.nexusrealms.creaturemod.network.ModPackets;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreatureMod implements ModInitializer {
	public static final String MOD_ID = "creature-mod";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static Identifier id(String name){
		return Identifier.of(MOD_ID, name);
	}
	public static Identifier geoModelId(String path){ return id("geo/" + path + ".geo.json");}
	public static Identifier geoAnimId(String path){ return id("animations/" + path + ".animation.json");}
	public static Identifier entityTextureId(String path){ return id("textures/entity/" + path + ".png");}


	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		//Keep this one on the top
		ModRegistries.init();

		ModItemGroups.init();
		ModCommands.init();
		ModPackets.init();
		ModBlocks.init();
		ModItems.init();
		ModActivities.init();
		ModMemories.init();
		ModSensors.init();
		ModEntities.init();
		Curses.init();
		Elements.init();
		FlowCostType.init();
		SpellEffectType.init();

		UseItemCallback.EVENT.register((playerEntity, world, hand) -> {
			ItemStack stack = playerEntity.getStackInHand(hand);
			if(playerEntity instanceof ServerPlayerEntity player){
				if(stack.isIn(ModTags.USE_BINDABLE)){
					if(stack.contains(ModItemComponents.USE_BIND)){
						RegistryEntry<Spell> spellRegistryEntry = stack.get(ModItemComponents.USE_BIND).spell();
						return Spell.castUse(player, spellRegistryEntry, stack, null);
					}
				}
			}
			return TypedActionResult.pass(stack);
		});
		UseEntityCallback.EVENT.register((playerEntity, world, hand, entity, entityHitResult) -> {
			ItemStack stack = playerEntity.getStackInHand(hand);
			if(playerEntity instanceof ServerPlayerEntity player){
				if(stack.isIn(ModTags.USE_BINDABLE)){
					if(stack.contains(ModItemComponents.USE_BIND)){
						RegistryEntry<Spell> spellRegistryEntry = stack.get(ModItemComponents.USE_BIND).spell();
						return Spell.castUse(player, spellRegistryEntry, stack, entity).getResult();
					}
				}
			}
			return TypedActionResult.pass(stack).getResult();
		});
		LOGGER.info("Hello Fabric world!");
	}
}