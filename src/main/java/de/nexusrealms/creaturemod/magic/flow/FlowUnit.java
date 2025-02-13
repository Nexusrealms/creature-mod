package de.nexusrealms.creaturemod.magic.flow;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.nexusrealms.creaturemod.ModRegistries;
import de.nexusrealms.creaturemod.magic.element.Element;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public interface FlowUnit{
    Codec<Immutable> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ModRegistries.ELEMENTS.getEntryCodec().fieldOf("element").forGetter(Immutable::element),
            Codec.INT.fieldOf("value").forGetter(Immutable::value)
    ).apply(instance, Immutable::new));
    Codec<Map<RegistryEntry<Element>, Mutable>> MAP_STORAGE_CODEC = Codec.unboundedMap(ModRegistries.ELEMENTS.getEntryCodec(), Codec.INT).xmap(map -> {
        HashMap<RegistryEntry<Element>, Mutable> newmap = new HashMap<>();
        map.forEach((elementRegistryEntry, integer) -> newmap.put(elementRegistryEntry, new Mutable(elementRegistryEntry, integer)));
        return newmap;
    }, registryEntryMutableMap -> {
        Map<RegistryEntry<Element>, Integer> newmap = new HashMap<>();
        registryEntryMutableMap.forEach((elementRegistryEntry, mutable) -> newmap.put(elementRegistryEntry, mutable.getValue()));
        return newmap;
    });

    RegistryEntry<Element> getElement();
    int getValue();
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
        public static final Codec<Mutable> CODEC = FlowUnit.CODEC.xmap(Immutable::toMutable, Mutable::toImmutable);

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
        public static <K, V> void updateAllValues(Map<K, V> map, Consumer<V> operator){
            map.forEach((k, v) -> operator.accept(v));
        }
    }
}
