package de.nexusrealms.creaturemod.network;

import de.nexusrealms.creaturemod.CreatureMod;
import de.nexusrealms.creaturemod.magic.spell.Incantation;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;

public record IncantationPacket(String words) implements ReceiverPacket<ServerPlayNetworking.Context>{
    public static final PacketCodec<ByteBuf, IncantationPacket> PACKET_CODEC = PacketCodecs.STRING.xmap(IncantationPacket::new, IncantationPacket::words);
    public static final CustomPayload.Id<IncantationPacket> PACKET_ID = new CustomPayload.Id<>(CreatureMod.id("incantation_packet"));
    @Override
    public void onReceive(ServerPlayNetworking.Context context) {
        Incantation.process(context.player(), context.player().getRegistryManager(), words);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
