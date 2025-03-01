package de.nexusrealms.creaturemod.network;

import de.nexusrealms.creaturemod.CreatureMod;
import de.nexusrealms.creaturemod.ModRegistries;
import de.nexusrealms.creaturemod.magic.spell.Spell;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record CastSpellPacket(UUID caster, RegistryEntry<Spell> spell) implements ReceiverPacket<ClientPlayNetworking.Context> {
    public static final CustomPayload.Id<CastSpellPacket> PACKET_ID = new CustomPayload.Id<>(CreatureMod.id("cast_spell_packet"));
    public static final PacketCodec<RegistryByteBuf, CastSpellPacket> PACKET_CODEC = PacketCodec.tuple(
            Uuids.PACKET_CODEC, CastSpellPacket::caster,
            PacketCodecs.registryEntry(ModRegistries.Keys.SPELLS), CastSpellPacket::spell,
            CastSpellPacket::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

    @Override
    public void onReceive(ClientPlayNetworking.Context context){
        PlayerEntity player = context.player().getWorld().getPlayerByUuid(caster);
        //TODO Add other parameters if it is useful
        spell.value().castClient(player, null, null);
    }
}
