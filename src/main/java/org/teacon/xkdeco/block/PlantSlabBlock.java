package org.teacon.xkdeco.block;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.lighting.LayerLightEngine;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.teacon.xkdeco.XKDeco;

import com.dm.earth.deferred_registries.DeferredObject;

import java.util.Random;

@MethodsReturnNonnullByDefault
public final class PlantSlabBlock extends SlabBlock implements XKDecoBlock.Plant {
    private static final VoxelShape PATH_TOP_AABB = Block.box(0, 8, 0, 16, 15, 16);
    private static final VoxelShape PATH_BOTTOM_AABB = Block.box(0, 0, 0, 16, 7, 16);
    private static final VoxelShape PATH_DOUBLE_AABB = Block.box(0, 0, 0, 16, 15, 16);

    private final boolean isPath;
    private final DeferredObject<Block> dirtSlab;

    public PlantSlabBlock(Properties properties, boolean isPath, String dirtSlabId) {
        super(properties);
        this.isPath = isPath;
		//TODO: test
        this.dirtSlab = new DeferredObject<Block>(XKDeco.asResource(dirtSlabId), Registry.BLOCK.get(XKDeco.asResource(dirtSlabId)));
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return this.isPath || state.getValue(TYPE) == SlabType.BOTTOM;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(TYPE)) {
            case TOP -> this.isPath ? PATH_TOP_AABB : TOP_AABB;
            case BOTTOM -> this.isPath ? PATH_BOTTOM_AABB : BOTTOM_AABB;
            case DOUBLE -> this.isPath ? PATH_DOUBLE_AABB : Shapes.block();
        };
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
        if (facing.getAxis() == Direction.Axis.Y && !state.canSurvive(world, pos)) {
            world.scheduleTick(pos, this, 1);
        }
        return super.updateShape(state, facing, facingState, world, pos, facingPos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel world, BlockPos pos, Random rng) {
        if (this.isPath) {
            this.turnToDirt(state, world, pos);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        if (this.isPath && state.getValue(TYPE) != SlabType.BOTTOM) {
            var aboveState = world.getBlockState(pos.above());
            return !aboveState.getMaterial().isSolid() || aboveState.getBlock() instanceof FenceGateBlock;
        }
        return true;
    }

    @Override
    public boolean isRandomlyTicking(BlockState pState) {
        return !this.isPath;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random rng) {
        if (world.isLoaded(pos)) {
            if (!this.isPath && !canBeGrass(state, world, pos)) {
                this.turnToDirt(state, world, pos);
            }
        }
    }

    private void turnToDirt(BlockState state, ServerLevel world, BlockPos pos) {
        var dirtSlabBlock = this.dirtSlab.get();
        if (state.getBlock() != dirtSlabBlock) {
            world.setBlockAndUpdate(pos, dirtSlabBlock.defaultBlockState()
                    .setValue(TYPE, state.getValue(TYPE)).setValue(WATERLOGGED, state.getValue(WATERLOGGED)));
        }
    }

    private static boolean canBeGrass(BlockState state, LevelReader world, BlockPos pos) {
        if (state.getValue(TYPE) != SlabType.BOTTOM) {
            var abovePos = pos.above();
            var aboveState = world.getBlockState(abovePos);
            if (!aboveState.is(Blocks.SNOW) || aboveState.getValue(SnowLayerBlock.LAYERS) != 1) {
                if (aboveState.getFluidState().getAmount() != 8) {
                    return LayerLightEngine.getLightBlockInto(world, state, pos, aboveState, abovePos,
                            Direction.UP, aboveState.getLightBlock(world, abovePos)) < world.getMaxLightLevel();
                }
                return false;
            }
            return true;
        }
        return !state.getValue(WATERLOGGED);
    }
}
