package de.nexusrealms.creaturemod;

import de.nexusrealms.creaturemod.curses.Curse;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class ModRegistries {
    public static final Registry<Curse> CURSES = createSimple(Keys.CURSES);
    private static <T> Registry<T> createSimple(RegistryKey<Registry<T>> registryKey){
        return FabricRegistryBuilder.createSimple(registryKey).buildAndRegister();
    }
    public static void init(){}
    public static class Keys{
        public static final RegistryKey<Registry<Curse>> CURSES = create("curse");
        private static <T> RegistryKey<Registry<T>> create(String name){
            return RegistryKey.ofRegistry(CreatureMod.id(name));
        }
    }
}
