package org.teacon.xkdeco.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.SpecialWardrobeBlock;

import com.dm.earth.deferred_registries.DeferredObject;

import static org.teacon.xkdeco.init.XKDecoObjects.WARDROBE_BLOCK_ENTITY;
import static org.teacon.xkdeco.init.XKDecoProperties.*;

import org.quiltmc.qsl.block.entity.api.QuiltBlockEntityTypeBuilder;

public class WardrobeBlockEntity extends BlockEntity {
	//TODO: test
    public static final DeferredObject<BlockEntityType<WardrobeBlockEntity>> TYPE =
            new DeferredObject<BlockEntityType<WardrobeBlockEntity>>(XKDeco.asResource(WARDROBE_BLOCK_ENTITY), QuiltBlockEntityTypeBuilder.create(WardrobeBlockEntity::new, new SpecialWardrobeBlock(BLOCK_WOOD_WARDROBE), new SpecialWardrobeBlock(BLOCK_METAL_WARDROBE), new SpecialWardrobeBlock(BLOCK_GLASS_WARDROBE)).build());
    public WardrobeBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(TYPE.get(), pWorldPosition, pBlockState);
    }
}
