package de.nexusrealms.creaturemod.magic.spell;

import com.mojang.serialization.Codec;
import de.nexusrealms.creaturemod.ModRegistries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.List;
import java.util.Optional;

public record Incantation(String words) {
    public static final Codec<Incantation> CODEC = Codec.STRING.xmap(Incantation::new, Incantation::words);
    public boolean matches(String words){
        return words.equalsIgnoreCase(this.words);
    }

    public static Optional<RegistryEntry<Spell>> lookup(RegistryWrapper.WrapperLookup wrapperLookup, String words){
        List<RegistryEntry.Reference<Spell>> list = wrapperLookup.getWrapperOrThrow(ModRegistries.Keys.SPELLS).streamEntries().filter(spellReference -> spellReference.value().incantation().matches(words)).toList();
        if(list.isEmpty()){
            return Optional.empty();
        }
        else if(list.size() > 1){
            throw new IllegalStateException("There is more than one spell for incantation " + words);
        } else {
            return Optional.of(list.getFirst());
        }
    }
}
