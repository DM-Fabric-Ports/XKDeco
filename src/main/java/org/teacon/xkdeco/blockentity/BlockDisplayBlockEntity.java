/**
 * block entity part of Block Display Block
 * it should only be responsible for data storage, verification and sync
 */
package org.teacon.xkdeco.blockentity;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.block.entity.api.QuiltBlockEntityTypeBuilder;
import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.SpecialBlockDisplayBlock;

import com.dm.earth.deferred_registries.DeferredObject;

import java.util.Objects;
import java.util.Optional;

import static org.teacon.xkdeco.init.XKDecoObjects.BLOCK_DISPLAY_BLOCK_ENTITY;
import static org.teacon.xkdeco.init.XKDecoProperties.*;

@MethodsReturnNonnullByDefault
public final class BlockDisplayBlockEntity extends BlockEntity implements Clearable {
    public static final DeferredObject<BlockEntityType<BlockDisplayBlockEntity>> TYPE =
            new DeferredObject<BlockEntityType<BlockDisplayBlockEntity>>(new ResourceLocation(XKDeco.ID, BLOCK_DISPLAY_BLOCK_ENTITY), QuiltBlockEntityTypeBuilder.create(BlockDisplayBlockEntity::new, new SpecialBlockDisplayBlock(BLOCK_STONE_DISPLAY), new SpecialBlockDisplayBlock(BLOCK_METAL_DISPLAY)).build());
    public static final String ITEMSTACK_NBT_KEY = "Display";
    private static final String BLOCKSTATE_NBT_KEY = "State";
    private static final String SELECTED_PROPERTY_NBT_KEY = "Selected";
    private static final BlockState EMPTY = Blocks.AIR.defaultBlockState();

    @NotNull
    private ItemStack item = ItemStack.EMPTY;
    @NotNull
    private BlockState blockState = EMPTY;
    @Nullable
    private Property<?> selectedProperty = null;

    public BlockDisplayBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(TYPE.get(), pWorldPosition, pBlockState);
    }

    // @Override
    public AABB getRenderBoundingBox() {
        return AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(this.getBlockPos().above()));
    }

    public @NotNull ItemStack getItem() {
        return item;
    }

    public void setItem(@NotNull ItemStack itemStack) {
        this.item = itemStack;
        var i = itemStack.getItem();
        if (i instanceof BlockItem blockItem) {
            blockState = blockItem.getBlock().defaultBlockState();
        } else {
            blockState = EMPTY;
        }
        var properties = blockState.getProperties();
        selectedProperty = properties.isEmpty() ? null : properties.iterator().next();
        var blockState = this.getBlockState();
        this.setChanged();
        Objects.requireNonNull(this.level).sendBlockUpdated(this.getBlockPos(), blockState, blockState, 0);
    }

    @NotNull
    public BlockState getStoredBlockState() {
        return blockState;
    }

    public void setStoredBlockState(@NotNull BlockState blockState) {
        this.blockState = blockState;
        this.setChanged();
        Objects.requireNonNull(this.level).sendBlockUpdated(this.getBlockPos(), blockState, blockState, 0);
    }

    public Optional<Property<?>> getSelectedProperty() {
        return Optional.ofNullable(selectedProperty);
    }

    public void setSelectedProperty(@Nullable Property<?> selectedProperty) {
        this.selectedProperty = selectedProperty;
        this.setChanged();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this,
                be -> ((BlockDisplayBlockEntity) be).writeNbtPacket(null));
    }

    // @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        readNbtPacket(pkt.getTag());
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return writeNbtPacket(null);
    }

    // @Override
    public void handleUpdateTag(CompoundTag tag) {
        readNbtPacket(tag);
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);
        if (pTag.contains(ITEMSTACK_NBT_KEY)) {
            this.item = ItemStack.of(pTag.getCompound(ITEMSTACK_NBT_KEY));
        }
        if (pTag.contains(BLOCKSTATE_NBT_KEY)) {
            this.blockState = NbtUtils.readBlockState(pTag.getCompound(BLOCKSTATE_NBT_KEY));
        }
        if (pTag.contains(SELECTED_PROPERTY_NBT_KEY)) {
            var propertyName = pTag.getString(SELECTED_PROPERTY_NBT_KEY);
            this.selectedProperty = this.blockState.getProperties()
                    .stream().filter(p -> p.getName().equals(propertyName)).findFirst()
                    .orElse(blockState.getProperties().stream().findFirst().orElse(null));
        }
    }

    @Override
    public void clearContent() {
        this.setItem(ItemStack.EMPTY);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put(ITEMSTACK_NBT_KEY, item.save(new CompoundTag()));
        pTag.put(BLOCKSTATE_NBT_KEY, NbtUtils.writeBlockState(blockState));
        pTag.putString(SELECTED_PROPERTY_NBT_KEY, selectedProperty == null ? "" : selectedProperty.getName());
    }

    private void readNbtPacket(@Nullable CompoundTag tag) {
        if (tag == null) return;
        if (tag.contains(BLOCKSTATE_NBT_KEY)) {
            this.blockState = NbtUtils.readBlockState(tag.getCompound(BLOCKSTATE_NBT_KEY));
        }
    }

    private CompoundTag writeNbtPacket(@Nullable CompoundTag tag) {
        if (tag == null) tag = new CompoundTag();
        tag.put(BLOCKSTATE_NBT_KEY, NbtUtils.writeBlockState(blockState));
        return tag;
    }
}
