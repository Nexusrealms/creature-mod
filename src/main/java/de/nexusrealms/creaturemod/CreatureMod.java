package de.nexusrealms.creaturemod;

import de.nexusrealms.creaturemod.blocks.ModBlocks;
import de.nexusrealms.creaturemod.curses.Curses;
import de.nexusrealms.creaturemod.entities.ModEntities;
import de.nexusrealms.creaturemod.entities.brain.ModActivities;
import de.nexusrealms.creaturemod.entities.brain.memory.ModMemories;
import de.nexusrealms.creaturemod.entities.brain.sensor.ModSensors;
import de.nexusrealms.creaturemod.items.ModItemGroups;
import de.nexusrealms.creaturemod.items.ModItems;
import de.nexusrealms.creaturemod.magic.element.Elements;
import de.nexusrealms.creaturemod.magic.flow.FlowCostType;
import de.nexusrealms.creaturemod.magic.spell.action.SpellEffectType;
import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;
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
		LOGGER.info("Hello Fabric world!");
	}
}