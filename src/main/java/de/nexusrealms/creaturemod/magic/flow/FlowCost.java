package de.nexusrealms.creaturemod.magic.flow;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.nexusrealms.creaturemod.ModRegistries;

import java.util.List;
import java.util.Optional;

public interface FlowCost {
    Codec<FlowCost> CODEC = ModRegistries.FLOW_COST_TYPES.getCodec()
            .dispatch("type", FlowCost::getType, FlowCostType::codec);

    FlowCostType<?> getType();
    boolean drain(FlowStorage flowStorage);
    boolean canDrain(FlowStorage flowStorage);
    record Simple(FlowUnit flowUnit) implements FlowCost {
        public static final MapCodec<Simple> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                FlowUnit.CODEC.fieldOf("flowUnit").forGetter(Simple::flowUnit)
                ).apply(instance, Simple::new));

        @Override
        public FlowCostType<?> getType() {
            return FlowCostType.SIMPLE;
        }

        @Override
        public boolean drain(FlowStorage flowStorage) {
            return flowStorage.drainFlow(flowUnit);
        }

        @Override
        public boolean canDrain(FlowStorage flowStorage) {
            return flowStorage.canDrain(flowUnit);
        }
    }
    record All(List<FlowCost> list) implements FlowCost {
        public static final MapCodec<All> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                FlowCost.CODEC.listOf().fieldOf("list").forGetter(All::list)
        ).apply(instance, All::new));

        @Override
        public FlowCostType<?> getType() {
            return FlowCostType.ALL;
        }

        @Override
        public boolean drain(FlowStorage flowStorage) {
            if(canDrain(flowStorage)){
                list.forEach(flowCost -> flowCost.drain(flowStorage));
                return true;
            }
            return false;
        }

        @Override
        public boolean canDrain(FlowStorage flowStorage) {
            return list.stream().allMatch(flowCost -> flowCost.canDrain(flowStorage));
        }
    }
    record Any(List<FlowCost> list) implements FlowCost {
        public static final MapCodec<Any> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                FlowCost.CODEC.listOf().fieldOf("list").forGetter(Any::list)
        ).apply(instance, Any::new));

        @Override
        public FlowCostType<?> getType() {
            return FlowCostType.ANY;
        }

        @Override
        public boolean drain(FlowStorage flowStorage) {
            if(canDrain(flowStorage)){
                Optional<FlowCost> flowCost = list.stream().filter(flowCost1 -> flowCost1.canDrain(flowStorage)).findFirst();
                return flowCost.isPresent() && flowCost.get().drain(flowStorage);
            }
            return false;
        }

        @Override
        public boolean canDrain(FlowStorage flowStorage) {
            return list.stream().anyMatch(flowCost -> flowCost.canDrain(flowStorage));
        }
    }
}
