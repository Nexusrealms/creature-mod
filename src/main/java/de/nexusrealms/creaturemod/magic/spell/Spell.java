package de.nexusrealms.creaturemod.magic.spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.nexusrealms.creaturemod.magic.MagicUtils;
import de.nexusrealms.creaturemod.magic.flow.FlowCost;
import de.nexusrealms.creaturemod.magic.flow.FlowStorage;
import de.nexusrealms.creaturemod.magic.flow.FlowUnit;
import de.nexusrealms.creaturemod.magic.spell.action.SpellAction;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record Spell(FlowCost flowCost, SpellAction action, Text description, Optional<SoundEvent> soundEvent, Incantation incantation) {

    public static final Codec<Spell> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FlowCost.CODEC.fieldOf("flowCost").forGetter(Spell::flowCost),
            SpellAction.CODEC.fieldOf("action").forGetter(Spell::action),
            TextCodecs.CODEC.fieldOf("description").forGetter(Spell::description),
            SoundEvent.CODEC.optionalFieldOf("castingSound").forGetter(Spell::soundEvent),
            Incantation.CODEC.fieldOf("incantation").forGetter(Spell::incantation)
    ).apply(instance, Spell::new));

    public boolean cast(ServerPlayerEntity caster, @Nullable ItemStack castingItem, @Nullable Entity clickTarget){
        Optional<FlowStorage> flowStorage = FlowStorage.getPreferredFlowStorage(caster);
        if(flowStorage.isPresent()){
            if(flowCost.drain(flowStorage.get())){
                soundEvent.ifPresent(sound -> caster.getWorld().playSound(null, caster.getBlockPos(), sound, SoundCategory.PLAYERS, 1f, 1f));
                return action.cast(caster, castingItem, clickTarget);
            }
        }
        return false;
    }
}
