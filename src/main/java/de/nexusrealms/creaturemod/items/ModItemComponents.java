package de.nexusrealms.creaturemod.items;

import com.mojang.serialization.Codec;
import de.nexusrealms.creaturemod.CreatureMod;
import de.nexusrealms.creaturemod.magic.flow.FlowUnit;
import net.minecraft.component.ComponentType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModItemComponents {
    public static final ComponentType<FlowUnit> STORED_FLOW = create("stored_flow", FlowUnit.CODEC, FlowUnit.PACKET_CODEC);
    public static final ComponentType<Integer> FLOW_CAPACITY = create("flow_capacity", Codec.INT, PacketCodecs.INTEGER);
    public static <T> ComponentType<T> create(String name, Codec<T> codec, PacketCodec<? super RegistryByteBuf, T> packetCodec){
        return Registry.register(Registries.DATA_COMPONENT_TYPE, CreatureMod.id(name), ComponentType.<T>builder().codec(codec).packetCodec(packetCodec).build());
    }
}
