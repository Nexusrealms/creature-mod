package de.nexusrealms.creaturemod.magic.element;

import de.nexusrealms.creaturemod.CreatureMod;
import de.nexusrealms.creaturemod.ModRegistries;
import net.minecraft.registry.Registry;

public class Elements {
    public static final Element EARTH = create("earth", new Element(1, 0x00dc0a));
    public static final Element WATER = create("water", new Element(2, 0x1dc2d1));
    public static final Element AIR = create("air", new Element(3, 0x7a82c4));
    public static final Element FIRE = create("fire", new Element(4, 0xff6100));
    public static final Element VOID = create("void", new Element(8, 0x000000));
    private static Element create(String name, Element element){
        return Registry.register(ModRegistries.ELEMENTS, CreatureMod.id(name), element);
    }
    public static void init(){}
}
