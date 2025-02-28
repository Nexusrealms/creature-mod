package de.nexusrealms.creaturemod.magic.element;

import com.mojang.serialization.Codec;
import de.nexusrealms.creaturemod.ModRegistries;
import net.minecraft.registry.entry.RegistryEntry;

public record Element(int baseDissipation, int color) {
    public static final Codec<Element> CODEC = ModRegistries.ELEMENTS.getCodec();
    public static final Codec<RegistryEntry<Element>> ENTRY_CODEC = ModRegistries.ELEMENTS.getEntryCodec();

}
