package de.nexusrealms.creaturemod.magic.spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.nexusrealms.creaturemod.CreatureMod;
import de.nexusrealms.creaturemod.ModEntityComponents;
import de.nexusrealms.creaturemod.ModRegistries;
import de.nexusrealms.creaturemod.ModTags;
import de.nexusrealms.creaturemod.items.ModItemComponents;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
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
            BindData.CODEC.optionalFieldOf("useBindData", new BindData(false, -1, Optional.empty(), new Incantation.WordsAddition("", ""), ItemPredicate.Builder.create().build(), 0, new FlowCost.None())).forGetter(Spell::useBindData),
            BindData.CODEC.optionalFieldOf("attackBindData", new BindData(false, -1, Optional.empty(), new Incantation.WordsAddition("", ""), ItemPredicate.Builder.create().build(), 0, new FlowCost.None())).forGetter(Spell::useBindData),
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
    public static TypedActionResult<ItemStack> castUse(ServerPlayerEntity caster, RegistryEntry<Spell> spellRegistryEntry, ItemStack stack, @Nullable Entity clickTarget){
        BindData data = spellRegistryEntry.value().useBindData();
        return castBound(spellRegistryEntry, data, caster, stack, clickTarget);
    }
    public static TypedActionResult<ItemStack> castAttack(ServerPlayerEntity caster, RegistryEntry<Spell> spellRegistryEntry, ItemStack stack, Entity clickTarget){
        BindData data = spellRegistryEntry.value().attackBindData();
        return castBound(spellRegistryEntry, data, caster, stack, clickTarget);
    }
    private static TypedActionResult<ItemStack> castBound(RegistryEntry<Spell> spellRegistryEntry, BindData bindData, ServerPlayerEntity caster, ItemStack stack, @Nullable Entity clickTarget){
        if(bindData.bindable()){
            CastDelayComponent component = caster.getComponent(ModEntityComponents.CAST_DELAY_COMPONENT);
            if(!spellRegistryEntry.hasKeyAndValue()) {
                CreatureMod.LOGGER.error("Invalid spell cast using item during attack!");
                return TypedActionResult.fail(stack);
            }
            Spell spell = spellRegistryEntry.value();
            if(component.isReady(spellRegistryEntry.getKey().get())){
                if(spell.castServer(caster, stack, clickTarget)){
                    int delay = bindData.delayOverride() >= 0 ? bindData.delayOverride() : spellRegistryEntry.value().delay();
                    if(delay > 0){
                        component.addDelay(spellRegistryEntry.getKey().get(), delay);
                    }
                    int itemDelay = bindData.itemCooldown();
                    if(itemDelay > 0){
                        caster.getItemCooldownManager().set(stack.getItem(), itemDelay);
                    }
                    return TypedActionResult.success(stack);
                }
            }
        }
        return TypedActionResult.fail(stack);
    }
    public void castClient(PlayerEntity caster, @Nullable ItemStack castingItem, @Nullable Entity clickTarget){
        rootEffect.apply(caster, caster, castingItem, clickTarget);
    }
    public boolean matchesUseBind(String words){
        return incantation.matches(words, useBindData.addition);
    }
    public boolean matchesAttackBind(String words){
        return incantation.matches(words, attackBindData.addition);
    }
    public static boolean bind(ServerPlayerEntity player, RegistryEntry<Spell> spellRegistryEntry, boolean isAttackBind){
        TagKey<Item> tag = isAttackBind ? ModTags.ATTACK_BINDABLE : ModTags.USE_BINDABLE;
        ItemStack stack = player.getStackInHand(Hand.MAIN_HAND).isEmpty() ? player.getStackInHand(Hand.OFF_HAND) : player.getStackInHand(Hand.MAIN_HAND);
        if(stack.isIn(tag)){
            BindData bindData = isAttackBind ? spellRegistryEntry.value().attackBindData() : spellRegistryEntry.value().useBindData();
            if (bindData.bindCost().drain(FlowStorage.getFlowStorage(player))){
                Bind bind = new Bind(spellRegistryEntry);
                stack.set(isAttackBind ? ModItemComponents.ATTACK_BIND : ModItemComponents.USE_BIND, bind);
                return true;
            }
        }
        return false;
    }
    public record BindData(boolean bindable, int delayOverride, Optional<String> invocationOverride, Incantation.WordsAddition addition, ItemPredicate bindablePredicate, int itemCooldown, FlowCost bindCost){
        public static final Codec<BindData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.optionalFieldOf("bindable", true).forGetter(BindData::bindable),
                Codec.INT.optionalFieldOf("delayOverride", -1).forGetter(BindData::delayOverride),
                Codec.STRING.optionalFieldOf("invocationOverride").forGetter(BindData::invocationOverride),
                Incantation.WordsAddition.CODEC.optionalFieldOf("wordsAddition", new Incantation.WordsAddition("In ", " nilir")).forGetter(BindData::addition),
                ItemPredicate.CODEC.optionalFieldOf("predicate", ItemPredicate.Builder.create().build()).forGetter(BindData::bindablePredicate),
                Codec.INT.optionalFieldOf("itemCooldown", 0).forGetter(BindData::itemCooldown),
                FlowCost.CODEC.optionalFieldOf("bindCost", new FlowCost.None()).forGetter(BindData::bindCost)
        ).apply(instance, BindData::new));
    }
    public record Bind(RegistryEntry<Spell> spell){
        public static final Codec<Bind> CODEC = RegistryFixedCodec.of(ModRegistries.Keys.SPELLS).xmap(Bind::new, Bind::spell);
        public static final PacketCodec<RegistryByteBuf, Bind> PACKET_CODEC = PacketCodecs.registryEntry(ModRegistries.Keys.SPELLS).xmap(Bind::new, Bind::spell);
    }
}
