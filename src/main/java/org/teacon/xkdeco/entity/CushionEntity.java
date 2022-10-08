package org.teacon.xkdeco.entity;

import com.dm.earth.deferred_registries.DeferredObject;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.init.XKDecoObjects;


import java.util.Objects;

@MethodsReturnNonnullByDefault

public final class CushionEntity extends Entity {
    private static final EntityDataSerializer<Vec3> LOCATION_DATA_SERIALIZER;
    public static final DeferredObject<EntityType<Entity>> TYPE;
    private static final EntityDataAccessor<Vec3> DATA_DIFF_LOCATION;
    static final double MAX_DISTANCE = 3.0;

    static {
        LOCATION_DATA_SERIALIZER = new Vec3Serializer();
        EntityDataSerializers.registerSerializer(LOCATION_DATA_SERIALIZER);
		TYPE = new DeferredObject<>(XKDeco.asResource(XKDecoObjects.CUSHION_ENTITY), FabricEntityTypeBuilder.create(MobCategory.MISC, CushionEntity::new).dimensions(EntityDimensions.fixed(1F / 256F, 1F / 256F)).trackRangeBlocks(256).build());
        DATA_DIFF_LOCATION = SynchedEntityData.defineId(CushionEntity.class, LOCATION_DATA_SERIALIZER);
    }

    public CushionEntity(BlockPos pos, Player player) {
        super(TYPE.get(), player.level);
        this.noPhysics = true;
        this.setPos(pos.getX() + 0.5, pos.getY() + 0.375, pos.getZ() + 0.5);
        this.setStandingDiffLocation(this.calculateStandingDiff(player));
        player.setPos(this.position());
        player.startRiding(this);
    }

	public CushionEntity(EntityType<Entity> entityEntityType, Level level) {
		super(entityEntityType, level);
		this.noPhysics = true;
	}

	private Vec3 calculateStandingDiff(Entity entity) {
        if (!entity.isPassenger()) {
            var diff = entity.position().add(0.0, 0.5, 0.0).subtract(this.position());
            return diff.lengthSqr() > MAX_DISTANCE ? diff.normalize().scale(Math.sqrt(MAX_DISTANCE)) : diff;
        }
        return Vec3.ZERO;
    }

    public Vec3 getStandingDiffLocation() {
        return this.entityData.get(DATA_DIFF_LOCATION);
    }

    public void setStandingDiffLocation(Vec3 value) {
        this.entityData.set(DATA_DIFF_LOCATION, value);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_DIFF_LOCATION, Vec3.ZERO);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        var standingDiffX = tag.getDouble("StandingDiffX");
        var standingDiffY = tag.getDouble("StandingDiffY");
        var standingDiffZ = tag.getDouble("StandingDiffZ");
        this.setStandingDiffLocation(new Vec3(standingDiffX, standingDiffY, standingDiffZ));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        var standingDiff = this.getStandingDiffLocation();
        tag.putDouble("StandingDiffX", standingDiff.x);
        tag.putDouble("StandingDiffY", standingDiff.y);
        tag.putDouble("StandingDiffZ", standingDiff.z);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity entity) {
        var targetPosition = this.position().add(this.getStandingDiffLocation());
        var targetBelow = new BlockPos(targetPosition.x, targetPosition.y - 1.0, targetPosition.z);
        var canStand = entity.level.getBlockState(targetBelow).isFaceSturdy(entity.level, targetBelow, Direction.UP);
        return canStand ? targetPosition : targetPosition.add(0.0, 1.0, 0.0);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide()) {
            if (this.getPassengers().isEmpty() || !canBlockBeSeated(this.getBlockStateOn())) {
                this.remove(RemovalReason.DISCARDED);
            }
        }
    }

    public static void onRightClickBlock(Player player, Level world, InteractionHand hand, BlockHitResult hit) {
        if (!world.isClientSide()) {
            var pos = hit.getBlockPos();
            if (hit.getDirection() == Direction.UP && !player.isShiftKeyDown()) {
                var cushions = world.getEntitiesOfClass(CushionEntity.class, new AABB(pos));
                if (cushions.isEmpty() && canBlockBeSeated(world.getBlockState(pos))) {
                    world.addFreshEntity(new CushionEntity(pos, player));
                }
            }
        }
    }

    private static boolean canBlockBeSeated(BlockState state) {
        var name = Objects.requireNonNull(Registry.BLOCK.getKey(state.getBlock()));
        if (XKDeco.ID.equals(name.getNamespace())) {
            var id = name.getPath();
            return id.contains(XKDecoObjects.CHAIR_SUFFIX) || id.contains(XKDecoObjects.STOOL_SUFFIX);
        }
        return false;
    }

    public static void onBreakBlock(Level world, BlockPos pos) {
        if (!world.isClientSide()) {
            var cushions = world.getEntitiesOfClass(CushionEntity.class, new AABB(pos));
            for (var cushion : cushions) {
                cushion.remove(RemovalReason.DISCARDED);
            }
        }
    }

    private static final class Vec3Serializer implements EntityDataSerializer<Vec3> {
        @Override
        public void write(FriendlyByteBuf buffer, Vec3 value) {
            buffer.writeDouble(value.x).writeDouble(value.y).writeDouble(value.z);
        }

        @Override
        public Vec3 read(FriendlyByteBuf buffer) {
            return new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        }

        @Override
        public Vec3 copy(Vec3 value) {
            return value;
        }
    }
}
