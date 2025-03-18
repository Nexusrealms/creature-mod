package de.nexusrealms.creaturemod.magic.spell;

import com.mojang.serialization.JsonOps;
import de.nexusrealms.creaturemod.ModEntityComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.entity.RespawnableComponent;

import java.util.ArrayList;
import java.util.List;

public class UnlockedSpellsComponents implements AutoSyncedComponent, RespawnableComponent<UnlockedSpellsComponents> {
    private List<RegistryEntry<Spell>> unlocked = new ArrayList<>();
    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        unlocked = Spell.UNLOCKING_CODEC.parse(wrapperLookup.getOps(NbtOps.INSTANCE), nbtCompound.get("unlocked")).getOrThrow();
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        nbtCompound.put("unlocked", Spell.UNLOCKING_CODEC.encodeStart(wrapperLookup.getOps(NbtOps.INSTANCE), unlocked).getOrThrow());
    }
    public boolean isUnlocked(RegistryEntry<Spell> spell){
        return unlocked.stream().anyMatch(spellRegistryEntry -> spellRegistryEntry.matches(spell));
    }
    public String listUnlocked(RegistryWrapper.WrapperLookup wrapperLookup){
        return Spell.UNLOCKING_CODEC.encodeStart(wrapperLookup.getOps(JsonOps.INSTANCE), unlocked).getOrThrow().toString();
    }
    public boolean unlock(RegistryEntry<Spell> registryEntry){
        return unlocked.add(registryEntry);
    }
    public static boolean canUse(RegistryEntry<Spell> spell, PlayerEntity player){
        return !needsSpellsUnlocked(player) || player.getComponent(ModEntityComponents.UNLOCKED_SPELLS_COMPONENT).isUnlocked(spell);
    }
    public static boolean needsSpellsUnlocked(PlayerEntity player){
        return !player.isCreative();
    }
}
