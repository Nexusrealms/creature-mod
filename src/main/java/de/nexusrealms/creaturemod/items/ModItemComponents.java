package de.nexusrealms.creaturemod.items;

import com.mojang.serialization.Codec;
import de.nexusrealms.creaturemod.CreatureMod;
import net.minecraft.component.ComponentType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModItemComponents {

    public static <T> ComponentType<T> create(String name, Codec<T> codec, PacketCodec<? super RegistryByteBuf, T> packetCodec){
        return Registry.register(Registries.DATA_COMPONENT_TYPE, CreatureMod.id(name), ComponentType.<T>builder().codec(codec).packetCodec(packetCodec).build());
    }
}
