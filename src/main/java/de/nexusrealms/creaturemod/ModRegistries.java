package de.nexusrealms.creaturemod;

import de.nexusrealms.creaturemod.curses.Curse;
import de.nexusrealms.creaturemod.magic.element.Element;
import de.nexusrealms.creaturemod.magic.flow.FlowCostType;
import de.nexusrealms.creaturemod.magic.spell.Spell;
import de.nexusrealms.creaturemod.magic.spell.effect.SpellEffect;
import de.nexusrealms.creaturemod.magic.spell.effect.SpellEffectType;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

public class ModRegistries {
    public static final Registry<Curse> CURSES = createSimple(Keys.CURSES);
    public static final Registry<Element> ELEMENTS = createSimple(Keys.ELEMENTS);
    public static final Registry<SpellEffectType<? extends SpellEffect<Entity>>> ENTITY_SPELL_EFFECTS = createSimple(Keys.ENTITY_SPELL_EFFECTS);
    public static final Registry<SpellEffectType<? extends SpellEffect<World>>> WORLD_SPELL_EFFECTS = createSimple(Keys.WORLD_SPELL_EFFECTS);
    public static final Registry<FlowCostType<?>> FLOW_COST_TYPES = createSimple(Keys.FLOW_COST_TYPES);


    private static <T> Registry<T> createSimple(RegistryKey<Registry<T>> registryKey){
        return FabricRegistryBuilder.createSimple(registryKey).buildAndRegister();
    }
    public static void init(){
        DynamicRegistries.registerSynced(Keys.SPELLS, Spell.CODEC);
    }
    public static class Keys{
        public static final RegistryKey<Registry<Curse>> CURSES = create("curse");
        public static final RegistryKey<Registry<Element>> ELEMENTS = create("element");
        public static final RegistryKey<Registry<SpellEffectType<? extends SpellEffect<Entity>>>> ENTITY_SPELL_EFFECTS = create("entity_spell_effects");
        public static final RegistryKey<Registry<SpellEffectType<? extends SpellEffect<World>>>> WORLD_SPELL_EFFECTS = create("world_spell_effects");
        public static final RegistryKey<Registry<FlowCostType<?>>> FLOW_COST_TYPES = create("flow_cost_type");

        public static final RegistryKey<Registry<Spell>> SPELLS = create("spell");

        private static <T> RegistryKey<Registry<T>> create(String name){
            return RegistryKey.ofRegistry(CreatureMod.id(name));
        }
    }
}
