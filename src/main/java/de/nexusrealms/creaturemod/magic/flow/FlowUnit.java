package de.nexusrealms.creaturemod.magic.flow;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.nexusrealms.creaturemod.ModRegistries;
import de.nexusrealms.creaturemod.magic.element.Element;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Flow;
import java.util.function.Consumer;

public interface FlowUnit{
    Codec<FlowUnit> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ModRegistries.ELEMENTS.getEntryCodec().fieldOf("element").forGetter(FlowUnit::getElement),
            Codec.INT.fieldOf("value").forGetter(FlowUnit::getValue)
    ).apply(instance, Immutable::new));
    Codec<List<FlowUnit>> LIST_CODEC = CODEC.listOf();
    PacketCodec<RegistryByteBuf, Immutable> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.registryEntry(ModRegistries.Keys.ELEMENTS), Immutable::element, PacketCodecs.VAR_INT, Immutable::value, Immutable::new);
    Codec<Map<RegistryEntry<Element>, Mutable>> MAP_STORAGE_CODEC = Codec.unboundedMap(ModRegistries.ELEMENTS.getEntryCodec(), Codec.INT).xmap(map -> {
        HashMap<RegistryEntry<Element>, Mutable> newmap = new HashMap<>();
        map.forEach((elementRegistryEntry, integer) -> newmap.put(elementRegistryEntry, new Mutable(elementRegistryEntry, integer)));
        return newmap;
    }, registryEntryMutableMap -> {
        Map<RegistryEntry<Element>, Integer> newmap = new HashMap<>();
        registryEntryMutableMap.forEach((elementRegistryEntry, mutable) -> newmap.put(elementRegistryEntry, mutable.getValue()));
        return newmap;
    });
    default FlowUnit combine(FlowUnit other){
        if(elementEquals(other)){
            return new Immutable(getElement(), getValue() + other.getValue());
        }
        throw new IllegalArgumentException("Tried to combine flow of different elements");
    }
    default boolean elementEquals(FlowUnit other){
        return getElement().getKey().equals(other.getElement().getKey());
    }
    default boolean elementEquals(RegistryEntry<Element> entry){
        return getElement().getKey().equals(entry.getKey());
    }
    Mutable toMutable();
    RegistryEntry<Element> getElement();
    int getValue();
    default boolean isEnoughFor(FlowUnit other){
        return getValue() >= other.getValue();
    }
    static FlowUnit of(RegistryEntry<Element> element, int value){
        return new Immutable(element, value);
    }
    record Immutable(RegistryEntry<Element> element, int value) implements FlowUnit {


        @Override
        public RegistryEntry<Element> getElement() {
            return element();
        }

        @Override
        public int getValue() {
            return value();
        }
        public Mutable toMutable(){
            return new Mutable(element, value);
        }
    }
    class Mutable implements FlowUnit{
        public static final Codec<Mutable> CODEC = FlowUnit.CODEC.xmap(FlowUnit::toMutable, Mutable::toImmutable);

        private final RegistryEntry<Element> element;
        private int value;

        public Mutable(RegistryEntry<Element> element, int value) {
            this.element = element;
            this.value = value;
        }
        public void add(int amount){
            value += amount;
        }
        public void subtract(int amount){
            add(-amount);
        }
        @Override
        public RegistryEntry<Element> getElement() {
            return element;
        }

        @Override
        public int getValue() {
            return value;
        }
        public Immutable toImmutable(){
            return new Immutable(element, value);
        }

        @Override
        public Mutable toMutable() {
            return this;
        }

        public static <K, V> void updateAllValues(Map<K, V> map, Consumer<V> operator){
            map.forEach((k, v) -> operator.accept(v));
        }
    }
}
