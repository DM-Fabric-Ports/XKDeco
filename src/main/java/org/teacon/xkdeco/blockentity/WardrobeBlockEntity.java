package org.teacon.xkdeco.blockentity;

import com.mojang.datafixers.DSL;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.SpecialWardrobeBlock;

import com.dm.earth.deferred_registries.DeferredObject;

import static org.teacon.xkdeco.init.XKDecoObjects.WARDROBE_BLOCK_ENTITY;
import static org.teacon.xkdeco.init.XKDecoProperties.*;

import org.quiltmc.qsl.block.entity.api.QuiltBlockEntityTypeBuilder;
import org.teacon.xkdeco.init.XKDecoObjects;

public class WardrobeBlockEntity extends BlockEntity {
	//TODO: test
    public static final DeferredObject<BlockEntityType<WardrobeBlockEntity>> TYPE =
            new DeferredObject<BlockEntityType<WardrobeBlockEntity>>(XKDeco.asResource(WARDROBE_BLOCK_ENTITY), QuiltBlockEntityTypeBuilder.create(WardrobeBlockEntity::new,
					XKDecoObjects.BLOCKS.getEntries().stream()
							.filter(b -> b instanceof SpecialWardrobeBlock)
							.toArray(Block[]::new)).build());
    public WardrobeBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(TYPE.get(), pWorldPosition, pBlockState);
    }
}
