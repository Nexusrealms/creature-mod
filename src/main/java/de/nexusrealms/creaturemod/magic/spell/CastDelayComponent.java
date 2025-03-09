package de.nexusrealms.creaturemod.magic.spell;

import com.mojang.serialization.Codec;
import de.nexusrealms.creaturemod.ModRegistries;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;
import org.ladysnake.cca.api.v3.entity.RespawnableComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CastDelayComponent implements AutoSyncedComponent, ServerTickingComponent, RespawnableComponent<CastDelayComponent> {
    public static final Codec<Map<RegistryKey<Spell>, Integer>> DELAY_STORAGE_CODEC = Codec.unboundedMap(RegistryKey.createCodec(ModRegistries.Keys.SPELLS), Codec.INT).xmap(HashMap::new, Function.identity());
    private Map<RegistryKey<Spell>, Integer> map = new HashMap<>();
    @Override
    public void serverTick() {
        map.replaceAll((spellRegistryKey, integer) -> integer-1);
        map.values().removeIf(integer -> integer==0);
    }
    public void addDelay(RegistryKey<Spell> spellRegistryKey, int delay){
        map.put(spellRegistryKey, delay);
    }
    public boolean isReady(RegistryKey<Spell> spellRegistryKey){
        return !map.containsKey(spellRegistryKey);
    }
    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        map = DELAY_STORAGE_CODEC.parse(wrapperLookup.getOps(NbtOps.INSTANCE), nbtCompound.getCompound("map")).getOrThrow();
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        nbtCompound.put("map", DELAY_STORAGE_CODEC.encodeStart(wrapperLookup.getOps(NbtOps.INSTANCE), map).getOrThrow());
    }
}
