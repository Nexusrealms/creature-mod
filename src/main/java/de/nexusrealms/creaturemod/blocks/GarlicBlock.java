package de.nexusrealms.creaturemod.blocks;

import de.nexusrealms.creaturemod.items.ModItems;
import net.minecraft.block.CropBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;

public class GarlicBlock extends CropBlock {
    public GarlicBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ItemConvertible getSeedsItem() {
        return ModItems.GARLIC_CLOVE;
    }

    @Override
    public Item asItem() {
        return ModItems.GARLIC_BULB;
    }
}
