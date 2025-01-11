package de.nexusrealms.creaturemod.curses;

import com.mojang.serialization.Codec;
import de.nexusrealms.creaturemod.ModRegistries;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.entry.RegistryEntry;

public class Curse {
    public static final Codec<RegistryEntry<Curse>> ENTRY_CODEC = ModRegistries.CURSES.getEntryCodec();
    public boolean shouldTick(){
        return false;
    }
    public void tick(LivingEntity holder, CurseInstance instance){

    }
}
