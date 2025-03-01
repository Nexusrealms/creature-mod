package de.nexusrealms.creaturemod.magic.spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.nexusrealms.creaturemod.ModRegistries;
import de.nexusrealms.creaturemod.magic.flow.FlowCost;
import de.nexusrealms.creaturemod.magic.flow.FlowStorage;
import de.nexusrealms.creaturemod.magic.spell.effect.SpellEffect;
import de.nexusrealms.creaturemod.network.CastSpellPacket;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record Spell(FlowCost flowCost, SpellEffect<Entity> rootEffect, Text description,
                    Optional<SoundEvent> soundEvent, Incantation incantation, boolean castOnClient) {

    public static final Codec<Spell> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FlowCost.CODEC.fieldOf("flowCost").forGetter(Spell::flowCost),
            SpellEffect.ENTITY_CODEC.fieldOf("rootEffect").forGetter(Spell::rootEffect),
            TextCodecs.CODEC.optionalFieldOf("description", Text.literal("A spell")).forGetter(Spell::description),
            SoundEvent.CODEC.optionalFieldOf("castingSound").forGetter(Spell::soundEvent),
            Incantation.CODEC.fieldOf("incantation").forGetter(Spell::incantation),
            Codec.BOOL.optionalFieldOf("castOnClient", false).forGetter(Spell::castOnClient)
    ).apply(instance, Spell::new));

    public boolean castServer(ServerPlayerEntity caster, @Nullable ItemStack castingItem, @Nullable Entity clickTarget) {
        FlowStorage flowStorage = FlowStorage.getFlowStorage(caster);
        if (flowCost.drain(flowStorage)) {
            soundEvent.ifPresent(sound -> caster.getWorld().playSound(null, caster.getBlockPos(), sound, SoundCategory.PLAYERS, 1f, 1f));
            if(castOnClient){
                RegistryEntry<Spell> entryOfThis = caster.getWorld().getRegistryManager().get(ModRegistries.Keys.SPELLS).getEntry(this);
                PlayerLookup.all(caster.server).forEach(player -> ServerPlayNetworking.send(player, new CastSpellPacket(caster.getUuid(), entryOfThis)));
            }
            return rootEffect.apply(caster, caster, castingItem, clickTarget);
        }
        return false;
    }
    public void castClient(PlayerEntity caster, @Nullable ItemStack castingItem, @Nullable Entity clickTarget){
        rootEffect.apply(caster, caster, castingItem, clickTarget);
    }
}
