package org.teacon.xkdeco.block;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.LeavesBlock;



@MethodsReturnNonnullByDefault

public final class PlantLeavesBlock extends LeavesBlock implements XKDecoBlock.Plant {
    public PlantLeavesBlock(Properties properties) {
        super(properties);
    }
}
