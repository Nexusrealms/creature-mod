package de.nexusrealms.creaturemod.util;

import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;

public class DirectedBlockPosition extends CachedBlockPosition {
    private final Direction direction;

    public DirectedBlockPosition(WorldView world, BlockPos pos, boolean forceLoad, Direction direction) {
        super(world, pos, forceLoad);
        this.direction = direction;
    }
    public Direction getDirection() {
        return this.direction;
    }
    public DirectedBlockPosition offset(int offset){
        return new DirectedBlockPosition(getWorld(), getBlockPos().offset(direction, offset), false, direction);
    }
}
