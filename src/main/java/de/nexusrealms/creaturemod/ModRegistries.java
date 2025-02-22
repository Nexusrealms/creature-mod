package de.nexusrealms.creaturemod;

import de.nexusrealms.creaturemod.curses.Curse;
import de.nexusrealms.creaturemod.magic.element.Element;
import de.nexusrealms.creaturemod.magic.flow.FlowCostType;
import de.nexusrealms.creaturemod.magic.spell.action.SpellActionType;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class ModRegistries {
    public static final Registry<Curse> CURSES = createSimple(Keys.CURSES);
    public static final Registry<Element> ELEMENTS = createSimple(Keys.ELEMENTS);
    public static final Registry<SpellActionType<?>> SPELL_ACTION_TYPES = createSimple(Keys.SPELL_ACTION_TYPES);
    public static final Registry<FlowCostType<?>> FLOW_COST_TYPES = createSimple(Keys.FLOW_COST_TYPES);


    private static <T> Registry<T> createSimple(RegistryKey<Registry<T>> registryKey){
        return FabricRegistryBuilder.createSimple(registryKey).buildAndRegister();
    }
    public static void init(){}
    public static class Keys{
        public static final RegistryKey<Registry<Curse>> CURSES = create("curse");
        public static final RegistryKey<Registry<Element>> ELEMENTS = create("element");
        public static final RegistryKey<Registry<SpellActionType<?>>> SPELL_ACTION_TYPES = create("spell_action_type");
        public static final RegistryKey<Registry<FlowCostType<?>>> FLOW_COST_TYPES = create("flow_cost_type");


        private static <T> RegistryKey<Registry<T>> create(String name){
            return RegistryKey.ofRegistry(CreatureMod.id(name));
        }
    }
}
