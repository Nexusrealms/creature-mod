package de.nexusrealms.creaturemod.magic.flow;

import com.mojang.serialization.MapCodec;
import de.nexusrealms.creaturemod.CreatureMod;
import de.nexusrealms.creaturemod.ModRegistries;
import net.minecraft.registry.Registry;

public record FlowCostType<T extends FlowCost>(MapCodec<T> codec) {
    public static final FlowCostType<FlowCost.Simple> SIMPLE = create("simple", FlowCost.Simple.CODEC);
    public static final FlowCostType<FlowCost.All> ALL = create("all", FlowCost.All.CODEC);
    public static final FlowCostType<FlowCost.Any> ANY = create("any", FlowCost.Any.CODEC);
    public static final FlowCostType<FlowCost.None> NONE = create("none", MapCodec.unit(new FlowCost.None()));

    private static <T extends FlowCost> FlowCostType<T> create(String name, MapCodec<T> codec){
        return Registry.register(ModRegistries.FLOW_COST_TYPES, CreatureMod.id(name), new FlowCostType<>(codec));
    }
    public static void init(){}
}
