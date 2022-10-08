package org.teacon.xkdeco.blockentity;

import com.mojang.datafixers.DSL;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.quiltmc.qsl.block.entity.api.QuiltBlockEntityTypeBuilder;
import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.api.util.RegistryUtil;
import org.teacon.xkdeco.block.SpecialWallBlock;

import com.dm.earth.deferred_registries.DeferredObject;

import java.util.Objects;

import static org.teacon.xkdeco.init.XKDecoObjects.WALL_BLOCK_ENTITY;
import static org.teacon.xkdeco.init.XKDecoProperties.*;

@MethodsReturnNonnullByDefault
public final class WallBlockEntity extends BlockEntity {
    public static final DeferredObject<BlockEntityType<WallBlockEntity>> TYPE =
            new DeferredObject<BlockEntityType<WallBlockEntity>>(new ResourceLocation(XKDeco.ID, WALL_BLOCK_ENTITY), QuiltBlockEntityTypeBuilder.create(WallBlockEntity::new, RegistryUtil.getBlockEntityBlocks(SpecialWallBlock.class)).build());

    private Block eastBlock = Blocks.AIR;
    private Block westBlock = Blocks.AIR;
    private Block southBlock = Blocks.AIR;
    private Block northBlock = Blocks.AIR;

    public WallBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(TYPE.get(), pWorldPosition, pBlockState);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    public void updateBlocksFromLevel(SpecialWallBlock wall) {
        var pos = this.getBlockPos();
        var block = this.getBlockState().getBlock();
        if (this.level != null) {
            var northWall = wall.connectsTo(this.level.getBlockState(pos.north()));
            var eastWall = wall.connectsTo(this.level.getBlockState(pos.east()));
            var southWall = wall.connectsTo(this.level.getBlockState(pos.south()));
            var westWall = wall.connectsTo(this.level.getBlockState(pos.west()));
            this.northBlock = northWall.orElse(Blocks.AIR);
            this.eastBlock = eastWall.orElse(Blocks.AIR);
            this.southBlock = southWall.orElse(Blocks.AIR);
            this.westBlock = westWall.orElse(Blocks.AIR);
            this.setChanged();
        }
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.northBlock = Objects.requireNonNullElse(Registry.BLOCK
				.get(new ResourceLocation(pTag.getString("NorthBlockName"))), Blocks.AIR);
        this.eastBlock = Objects.requireNonNullElse(Registry.BLOCK
                .get(new ResourceLocation(pTag.getString("EastBlockName"))), Blocks.AIR);
        this.southBlock = Objects.requireNonNullElse(Registry.BLOCK
                .get(new ResourceLocation(pTag.getString("SouthBlockName"))), Blocks.AIR);
        this.westBlock = Objects.requireNonNullElse(Registry.BLOCK
                .get(new ResourceLocation(pTag.getString("WestBlockName"))), Blocks.AIR);
        if (this.getBlockState().getBlock() instanceof SpecialWallBlock wall) {
            this.updateBlocksFromLevel(wall);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putString("NorthBlockName", Objects.requireNonNullElse(Registry.BLOCK
                .getKey(this.northBlock), Registry.BLOCK.getKey(Blocks.AIR)).toString());
        pTag.putString("EastBlockName", Objects.requireNonNullElse(Registry.BLOCK
                .getKey(this.eastBlock), Registry.BLOCK.getKey(Blocks.AIR)).toString());
        pTag.putString("SouthBlockName", Objects.requireNonNullElse(Registry.BLOCK
                .getKey(this.southBlock), Registry.BLOCK.getKey(Blocks.AIR)).toString());
        pTag.putString("WestBlockName", Objects.requireNonNullElse(Registry.BLOCK
                .getKey(this.westBlock), Registry.BLOCK.getKey(Blocks.AIR)).toString());
    }
}
