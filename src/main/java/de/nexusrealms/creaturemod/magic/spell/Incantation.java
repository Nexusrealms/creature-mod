package de.nexusrealms.creaturemod.magic.spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.nexusrealms.creaturemod.ModRegistries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;
import java.util.Optional;

public record Incantation(String words) {
    public static final Codec<Incantation> CODEC = Codec.STRING.xmap(Incantation::new, Incantation::words);
    public boolean matches(String words){
        return words.equalsIgnoreCase(this.words);
    }
    public boolean matches(String words, WordsAddition wordsAddition){
        return words.equalsIgnoreCase(wordsAddition.prefix() + this.words + wordsAddition.suffix());
    }
    public static Optional<RegistryEntry<Spell>> lookupCast(RegistryWrapper.WrapperLookup wrapperLookup, String words){
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
    public static Optional<RegistryEntry<Spell>> lookupUseBind(RegistryWrapper.WrapperLookup wrapperLookup, String words){
        List<RegistryEntry.Reference<Spell>> list = wrapperLookup.getWrapperOrThrow(ModRegistries.Keys.SPELLS).streamEntries()
                .filter(spellReference -> spellReference.value().useBindData().bindable())
                .filter(spellReference -> spellReference.value().matchesUseBind(words))
                .toList();
        if(list.isEmpty()){
            return Optional.empty();
        }
        else if(list.size() > 1){
            throw new IllegalStateException("There is more than one spell for incantation " + words);
        } else {
            return Optional.of(list.getFirst());
        }
    }
    public static Optional<RegistryEntry<Spell>> lookupAttackBind(RegistryWrapper.WrapperLookup wrapperLookup, String words){
        List<RegistryEntry.Reference<Spell>> list = wrapperLookup.getWrapperOrThrow(ModRegistries.Keys.SPELLS).streamEntries()
                .filter(spellReference -> spellReference.value().attackBindData().bindable())
                .filter(spellReference -> spellReference.value().matchesAttackBind(words))
                .toList();
        if(list.isEmpty()){
            return Optional.empty();
        }
        else if(list.size() > 1){
            throw new IllegalStateException("There is more than one spell for incantation " + words);
        } else {
            return Optional.of(list.getFirst());
        }
    }
    public static boolean process(ServerPlayerEntity player, RegistryWrapper.WrapperLookup wrapperLookup, String words){
        Optional<RegistryEntry<Spell>> castOptional = lookupCast(wrapperLookup, words);
        if(castOptional.isPresent()){
            Spell.castDirect(player, castOptional.get());
            return true;
        }
        Optional<RegistryEntry<Spell>> useBindOptional = lookupUseBind(wrapperLookup, words);
        if(useBindOptional.isPresent()){
            return Spell.bind(player, useBindOptional.get(), false);
        }
        Optional<RegistryEntry<Spell>> attackBindOptional = lookupAttackBind(wrapperLookup, words);
        return attackBindOptional.filter(spellRegistryEntry -> Spell.bind(player, spellRegistryEntry, true)).isPresent();
    }
    public record WordsAddition(String prefix, String suffix){
        public static final Codec<WordsAddition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.optionalFieldOf("prefix", "").forGetter(WordsAddition::prefix),
                Codec.STRING.optionalFieldOf("suffix", "").forGetter(WordsAddition::suffix)
        ).apply(instance, WordsAddition::new));
    }
}
