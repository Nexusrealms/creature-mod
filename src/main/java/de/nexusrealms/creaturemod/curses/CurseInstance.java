package de.nexusrealms.creaturemod.curses;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;

public class CurseInstance {
    public static final Codec<CurseInstance> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                    Curse.ENTRY_CODEC.fieldOf("id").forGetter(CurseInstance::getType),
                    Codec.BOOL.fieldOf("hidden").forGetter(CurseInstance::isHidden),
                    Codec.BOOL.fieldOf("shouldTick").forGetter(CurseInstance::shouldPossiblyTick))
                    .apply(instance, CurseInstance::new));
    private final RegistryEntry<Curse> type;
    private final boolean hidden;
    private final boolean shouldTick;
    public CurseInstance(RegistryEntry<Curse> type, boolean hidden, boolean shouldTick){
        this.type = type;
        this.hidden = hidden;
        this.shouldTick = shouldTick;
    }
    public RegistryEntry<Curse> getType(){
        return type;
    }
    public boolean isHidden(){
        return hidden;
    }
    private boolean shouldPossiblyTick(){
        return shouldTick;
    }
    public boolean shouldActuallyTick(){
        return shouldPossiblyTick() && type.value().shouldTick();
    }
    public void tick(LivingEntity holder){
        type.value().tick(holder, this);
    }
}
