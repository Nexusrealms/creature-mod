package de.nexusrealms.creaturemod.magic.spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.nexusrealms.creaturemod.CreatureMod;
import de.nexusrealms.creaturemod.ModEntityComponents;
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
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record Spell(FlowCost flowCost, SpellEffect<Entity> rootEffect, Text description,
                    Optional<SoundEvent> soundEvent, Incantation incantation, boolean castOnClient,
                    BindData useBindData, BindData attackBindData, int delay) {

    public static final Codec<Spell> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FlowCost.CODEC.fieldOf("flowCost").forGetter(Spell::flowCost),
            SpellEffect.ENTITY_CODEC.fieldOf("rootEffect").forGetter(Spell::rootEffect),
            TextCodecs.CODEC.optionalFieldOf("description", Text.literal("A spell")).forGetter(Spell::description),
            SoundEvent.CODEC.optionalFieldOf("castingSound").forGetter(Spell::soundEvent),
            Incantation.CODEC.fieldOf("incantation").forGetter(Spell::incantation),
            Codec.BOOL.optionalFieldOf("castOnClient", false).forGetter(Spell::castOnClient),
            BindData.CODEC.optionalFieldOf("useBindData", new BindData(false, Optional.empty(), Optional.empty(), "", ItemPredicate.Builder.create().build())).forGetter(Spell::useBindData),
            BindData.CODEC.optionalFieldOf("attackBindData", new BindData(false, Optional.empty(), Optional.empty(), "", ItemPredicate.Builder.create().build())).forGetter(Spell::useBindData),
            Codec.INT.optionalFieldOf("delay", 0).forGetter(Spell::delay)
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
    public static boolean castDirect(ServerPlayerEntity caster, RegistryEntry<Spell> spellRegistryEntry){
        CastDelayComponent component = caster.getComponent(ModEntityComponents.CAST_DELAY_COMPONENT);
        if(!spellRegistryEntry.hasKeyAndValue()) {
            CreatureMod.LOGGER.error("Invalid spell cast!");
            return false;
        }
        if(component.isReady(spellRegistryEntry.getKey().get())){
            if(spellRegistryEntry.value().castServer(caster, null, null)){
                int delay = spellRegistryEntry.value().delay();
                if(delay > 0){
                    component.addDelay(spellRegistryEntry.getKey().get(), delay);
                }
                return true;
            }
        }
        return false;
    }
    public void castClient(PlayerEntity caster, @Nullable ItemStack castingItem, @Nullable Entity clickTarget){
        rootEffect.apply(caster, caster, castingItem, clickTarget);
    }
    public record BindData(boolean bindable, Optional<Integer> delayOverride, Optional<String> invocationOverride, String invocationPrefix, ItemPredicate bindablePredicate){
        public static final Codec<BindData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.optionalFieldOf("bindable", true).forGetter(BindData::bindable),
                Codec.INT.optionalFieldOf("delayOverride").forGetter(BindData::delayOverride),
                Codec.STRING.optionalFieldOf("invocationOverride").forGetter(BindData::invocationOverride),
                Codec.STRING.optionalFieldOf("invocationPrefix", "Vai").forGetter(BindData::invocationPrefix),
                ItemPredicate.CODEC.optionalFieldOf("predicate", ItemPredicate.Builder.create().build()).forGetter(BindData::bindablePredicate)
        ).apply(instance, BindData::new));
    }
}
