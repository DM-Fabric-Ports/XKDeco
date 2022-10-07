package org.teacon.xkdeco.init;

import com.dm.earth.deferred_registries.DeferredRegistries;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.quiltmc.qsl.block.entity.api.QuiltBlockEntityTypeBuilder;
import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.api.util.RegistryUtil;
import org.teacon.xkdeco.block.*;
import org.teacon.xkdeco.blockentity.BlockDisplayBlockEntity;
import org.teacon.xkdeco.blockentity.ItemDisplayBlockEntity;
import org.teacon.xkdeco.blockentity.WallBlockEntity;
import org.teacon.xkdeco.blockentity.WardrobeBlockEntity;
import org.teacon.xkdeco.entity.CushionEntity;
import org.teacon.xkdeco.item.SpecialWallItem;


import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.teacon.xkdeco.init.XKDecoProperties.*;

@MethodsReturnNonnullByDefault

public final class XKDecoObjects {
    public static final DeferredRegistries<EntityType<?>> ENTITIES = DeferredRegistries.create(Registry.ENTITY_TYPE, XKDeco.ID);
    public static final DeferredRegistries<Block> BLOCKS = DeferredRegistries.create(Registry.BLOCK, XKDeco.ID);
    public static final DeferredRegistries<Item> ITEMS = DeferredRegistries.create(Registry.ITEM, XKDeco.ID);
    public static final DeferredRegistries<BlockEntityType<?>> BLOCK_ENTITY = DeferredRegistries.create(Registry.BLOCK_ENTITY_TYPE, XKDeco.ID);

    public static final String CUSHION_ENTITY = "cushion";

    public static final String WALL_BLOCK_ENTITY = "special_wall";
    public static final String ITEM_DISPLAY_BLOCK_ENTITY = "item_display";
    public static final String BLOCK_DISPLAY_BLOCK_ENTITY = "block_display";
    public static final String WARDROBE_BLOCK_ENTITY = "wardrobe";

    public static final String GRASS_PREFIX = "grass_";
    public static final String GLASS_PREFIX = "glass_";
    public static final String LINED_PREFIX = "lined_";
    public static final String WILLOW_PREFIX = "willow_";
    public static final String HOLLOW_PREFIX = "hollow_";
    public static final String LUXURY_PREFIX = "luxury_";
    public static final String PAINTED_PREFIX = "painted_";
    public static final String CHISELED_PREFIX = "chiseled_";
    public static final String PLANTABLE_PREFIX = "plantable_";
    public static final String TRANSLUCENT_PREFIX = "translucent_";
    public static final String DOUBLE_SCREW_PREFIX = "double_screw_";
    public static final String SPECIAL_WALL_PREFIX = "special_wall_";
    public static final String STONE_WATER_PREFIX = "stone_water_";

    public static final String LOG_SUFFIX = "_log";
    public static final String WOOD_SUFFIX = "_wood";
    public static final String SLAB_SUFFIX = "_slab";
    public static final String PATH_SUFFIX = "_path";
    public static final String ROOF_SUFFIX = "_roof";
    public static final String ROOF_EAVE_SUFFIX = "_roof_eave";
    public static final String ROOF_FLAT_SUFFIX = "_roof_flat";
    public static final String STOOL_SUFFIX = "_stool";
    public static final String CHAIR_SUFFIX = "_chair";
    public static final String TABLE_SUFFIX = "_table";
    public static final String GLASS_SUFFIX = "_glass";
    public static final String STAIRS_SUFFIX = "_stairs";
    public static final String PILLAR_SUFFIX = "_pillar";
    public static final String LEAVES_SUFFIX = "_leaves";
    public static final String BLOSSOM_SUFFIX = "_blossom";
    public static final String BIG_TABLE_SUFFIX = "_big_table";
    public static final String TALL_TABLE_SUFFIX = "_tall_table";
    public static final String LEAVES_DARK_SUFFIX = "_leaves_dark";
    public static final String ITEM_DISPLAY_SUFFIX = "_item_display";
    public static final String BLOCK_DISPLAY_SUFFIX = "_block_display";
    public static final String WARDROBE_SUFFIX = "_wardrobe";
    public static final String SHATTER_SUFFIX = "_shatter";
    public static final String CONSOLE_SUFFIX = "_console";
    public static final String VENT_FAN_SUFFIX = "_vent_fan";

    public static final String CUP_SPECIAL = "cup";
    public static final String REFRESHMENT_SPECIAL = "refreshments";
    public static final String FRUIT_PLATTER_SPECIAL = "fruit_platter";
    public static final String ITEM_PROJECTOR_SPECIAL = "item_projector";

    private static void addCushionEntity() {
        ENTITIES.register(CUSHION_ENTITY, EntityType.Builder
                .<CushionEntity>of(CushionEntity::new, MobCategory.MISC).sized(1F / 256F, 1F / 256F)
                .clientTrackingRange(256).build(new ResourceLocation(XKDeco.ID, CUSHION_ENTITY).toString()));
    }

    private static void addBasic(String id, ShapeFunction shapeFunction, boolean isSupportNeeded,
                                 BlockBehaviour.Properties properties, Item.Properties itemProperties) {
        var horizontalShapes = Maps.toMap(Direction.Plane.HORIZONTAL, shapeFunction::getShape);
        if (!isSupportNeeded && horizontalShapes.values().stream().anyMatch(s -> Block
                .isFaceFull(Shapes.join(Shapes.block(), s, BooleanOp.ONLY_FIRST), Direction.DOWN))) {
            var shapes = Maps.toMap(Arrays.stream(Direction.values()).toList(), shapeFunction::getShape);
            var block = BLOCKS.register(id, () -> new BasicFullDirectionBlock(properties, shapes));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else if (horizontalShapes.values().stream().allMatch(Block::isShapeFullBlock)) {
            var block = BLOCKS.register(id, () -> new BasicCubeBlock(properties, horizontalShapes));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else {
            var block = BLOCKS.register(id, () -> new BasicBlock(properties, isSupportNeeded, horizontalShapes));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        }
    }

    private static void addIsotropic(String id,
                                     BlockBehaviour.Properties properties, Item.Properties itemProperties) {
        var isGlass = id.contains(GLASS_SUFFIX) || id.contains(TRANSLUCENT_PREFIX) || id.contains(GLASS_PREFIX);
        if (id.contains(SLAB_SUFFIX)) {
            var block = BLOCKS.register(id, () -> new IsotropicSlabBlock(properties, isGlass));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else if (id.contains(STAIRS_SUFFIX)) {
            var block = BLOCKS.register(id, () -> new IsotropicStairBlock(properties, isGlass));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else if (id.contains(LOG_SUFFIX) || id.contains(WOOD_SUFFIX) || id.contains(PILLAR_SUFFIX)) {
            var block = BLOCKS.register(id, () -> new IsotropicPillarBlock(properties, isGlass));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else if (id.contains(LINED_PREFIX) || id.contains(LUXURY_PREFIX) || id.contains(PAINTED_PREFIX)
                || id.contains(CHISELED_PREFIX) || id.contains(DOUBLE_SCREW_PREFIX)) {
            var block = BLOCKS.register(id, () -> new IsotropicPillarBlock(properties, isGlass));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else if (id.contains(BIG_TABLE_SUFFIX) || id.contains(TALL_TABLE_SUFFIX)) {
            var block = BLOCKS.register(id, () -> new IsotropicHollowBlock(properties, IsotropicHollowBlock.BIG_TABLE_SHAPE));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else if (id.contains(TABLE_SUFFIX)) {
            var block = BLOCKS.register(id, () -> new IsotropicHollowBlock(properties, IsotropicHollowBlock.TABLE_SHAPE));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else if (id.contains(HOLLOW_PREFIX)) {
            var block = BLOCKS.register(id, () -> new IsotropicHollowBlock(properties, Shapes.block()));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else if (id.contains(ROOF_FLAT_SUFFIX)) {
            var block = BLOCKS.register(id, () -> new IsotropicRoofFlatBlock(properties));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else if (id.contains(ROOF_EAVE_SUFFIX)) {
            var block = BLOCKS.register(id, () -> new IsotropicRoofEaveBlock(properties));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else if (id.contains(ROOF_SUFFIX)) {
            var block = BLOCKS.register(id, () -> new IsotropicRoofBlock(properties));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else {
            var block = BLOCKS.register(id, () -> new IsotropicCubeBlock(properties, isGlass));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        }
    }

    private static void addPlant(String id,
                                 BlockBehaviour.Properties properties, Item.Properties itemProperties) {
        var isPath = id.contains(PATH_SUFFIX);
        if (id.contains(LEAVES_SUFFIX) || id.contains(BLOSSOM_SUFFIX)) {
            if (id.endsWith(SHATTER_SUFFIX)) {
                var block = BLOCKS.register(id, () -> new PlantLeavesShatterBlock(properties));
                ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
                return;
            }
            var block = BLOCKS.register(id, () -> new PlantLeavesBlock(properties));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else if (id.contains(SLAB_SUFFIX)) {
            var block = BLOCKS.register(id, () -> new PlantSlabBlock(properties, isPath, "dirt_slab"));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else {
            throw new IllegalArgumentException("Illegal id (" + id + ") for plant blocks");
        }
    }

    private static void addSpecial(String id,
                                   BlockBehaviour.Properties properties, Item.Properties itemProperties) {
        if (id.equals(CUP_SPECIAL)) {
            var block = BLOCKS.register(id, () -> new SpecialCupBlock(properties));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else if (id.equals(REFRESHMENT_SPECIAL) || id.equals(FRUIT_PLATTER_SPECIAL)) {
            var block = BLOCKS.register(id, () -> new SpecialDessertBlock(properties));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else if (id.contains(ITEM_DISPLAY_SUFFIX) || id.equals(ITEM_PROJECTOR_SPECIAL)) {
            var block = BLOCKS.register(id, () -> new SpecialItemDisplayBlock(properties));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else if (id.contains(BLOCK_DISPLAY_SUFFIX)) {
            var block = BLOCKS.register(id, () -> new SpecialBlockDisplayBlock(properties));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else if (id.contains(WARDROBE_SUFFIX)) {
            var block = BLOCKS.register(id, () -> new SpecialWardrobeBlock(properties));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else if (id.contains(ROOF_SUFFIX)) {
            var block = BLOCKS.register(id, () -> new SpecialRoofRidgeBlock(properties));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else if (Objects.equals(id, "factory_light_bar")) {
            var block = BLOCKS.register(id, () -> new SpecialLightBar(properties));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else if (id.contains(VENT_FAN_SUFFIX)) {
            var shapes = Maps.toMap(Arrays.stream(Direction.values()).toList(), d -> ShapeFunction.fromVentFan().getShape(d));
            var block = BLOCKS.register(id, () -> new BasicFullDirectionBlock(properties, shapes));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else if (id.contains(CONSOLE_SUFFIX)) {
            var block = BLOCKS.register(id, () -> new SpecialConsole(properties));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else {
            throw new IllegalArgumentException("Illegal id (" + id + ") for special blocks");
        }
    }

    private static void addItem(String id, Item.Properties itemProperties) {
        ITEMS.register(id, () -> new Item(itemProperties));
    }

    private static void addDisplayBlockEntity() {
        BLOCK_ENTITY.register(ITEM_DISPLAY_BLOCK_ENTITY,
                QuiltBlockEntityTypeBuilder.create(ItemDisplayBlockEntity::new,
                        BLOCKS.getEntries().stream()
                                .filter(b -> b instanceof SpecialItemDisplayBlock)
                                .toArray(Block[]::new)).build(DSL.remainderType()));
        BLOCK_ENTITY.register(BLOCK_DISPLAY_BLOCK_ENTITY, () ->
				QuiltBlockEntityTypeBuilder.create(BlockDisplayBlockEntity::new,
                        BLOCKS.getEntries().stream()
                                .filter(b -> b instanceof SpecialBlockDisplayBlock)
                                .toArray(Block[]::new)).build(DSL.remainderType()));
    }

    private static void addWardrobeBlockEntity() {
        BLOCK_ENTITY.register(WARDROBE_BLOCK_ENTITY,
                () -> QuiltBlockEntityTypeBuilder.create(WardrobeBlockEntity::new,
                        BLOCKS.getEntries().stream()
                                .filter(b -> b instanceof SpecialWardrobeBlock)
                                .toArray(Block[]::new)).build(DSL.remainderType()));
    }

    public static void addSpecialWallBlocks() {
        for (var set : Registry.BLOCK.entrySet()) {
			Block block = set.getValue();
            if (block instanceof WallBlock wall) {
                var registryName = Objects.requireNonNull(set.getKey().location());
                if (!"minecraft".equals(registryName.getNamespace())) {
                    continue;
                }
                var name = SPECIAL_WALL_PREFIX + registryName.toString().replace(':', '_');
				Registry.register(Registry.BLOCK, XKDeco.asResource(name), new SpecialWallBlock(wall));
            }
        }
    }

    public static void addSpecialWallItems() {
        for (var set : Registry.BLOCK.entrySet()) {
			Block block = set.getValue();
            if (block instanceof SpecialWallBlock wall) {
				Registry.register(Registry.ITEM, set.getKey().location(), new SpecialWallItem(wall, XKDecoProperties.ITEM_STRUCTURE));
            }
        }
    }

    public static void addSpecialWallBlockEntity() {
        var blocks = RegistryUtil.getBlockEntityBlocks(SpecialWallBlock.class);
        var registryName = XKDeco.asResource(WALL_BLOCK_ENTITY);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, registryName, QuiltBlockEntityTypeBuilder.create(WallBlockEntity::new, blocks).build(DSL.remainderType()));
    }

	//TODO: impl tags
    public static <T> void addSpecialWallTags(Map<TagKey<T>, List<RegistryAccess.RegistryEntry<T>>> registryEntries) {
		for (var set : registryEntries.entrySet()) {
			if (BlockTags.WALLS.equals(set.getKey())) {
				List<RegistryAccess.RegistryEntry<T>> list = set.getValue();
				for (var blockSet : Registry.BLOCK.entrySet()) {
					if (blockSet.getValue() instanceof SpecialWallBlock block) {
						list.add(new RegistryAccess.RegistryEntry<>(ResourceKey.createRegistryKey(blockSet.getKey().registry()), set.getValue().get(0).value()));
					}
                }
				registryEntries.replace(set.getKey(), list);
			}
		}
    }

    @FunctionalInterface
    private interface ShapeFunction {
        VoxelShape getShape(Direction direction);

        static ShapeFunction fromBigTable() {
            return d -> Direction.Plane.HORIZONTAL.test(d) ? IsotropicHollowBlock.BIG_TABLE_SHAPE : Shapes.block();
        }

        static ShapeFunction fromLongStool() {
            return d -> switch (d) {
                case EAST, WEST -> Block.box(3, 0, 0, 13, 10, 16);
                case NORTH, SOUTH -> Block.box(0, 0, 3, 16, 10, 13);
                default -> Shapes.block();
            };
        }

        static ShapeFunction fromChair() {
            return d -> switch (d) {
                case EAST -> Shapes.or(Block.box(2, 0, 2, 14, 10, 14), Block.box(2, 10, 2, 4, 16, 14));
                case SOUTH -> Shapes.or(Block.box(2, 0, 2, 14, 10, 14), Block.box(2, 10, 2, 14, 16, 4));
                case WEST -> Shapes.or(Block.box(2, 0, 2, 14, 10, 14), Block.box(12, 10, 2, 14, 16, 14));
                case NORTH -> Shapes.or(Block.box(2, 0, 2, 14, 10, 14), Block.box(2, 10, 12, 14, 16, 14));
                default -> Shapes.block();
            };
        }

        static ShapeFunction fromShelf() {
            return d -> switch (d) {
                case EAST, WEST -> Shapes.or(
                        Block.box(0, 0, 0, 16, 1, 16), Block.box(0, 15, 0, 16, 16, 16),
                        Block.box(0, 1, 0, 16, 15, 1), Block.box(0, 1, 15, 16, 15, 16));
                case NORTH, SOUTH -> Shapes.or(
                        Block.box(15, 0, 0, 16, 16, 16), Block.box(0, 0, 0, 1, 16, 16),
                        Block.box(1, 15, 0, 15, 16, 16), Block.box(1, 0, 0, 15, 1, 16));
                default -> Shapes.block();
            };
        }

        static ShapeFunction fromMiniature() {
            return d -> switch (d) {
                case EAST, WEST -> Block.box(3, 0, 0, 13, 6, 16);
                case NORTH, SOUTH -> Block.box(0, 0, 3, 16, 6, 13);
                default -> Shapes.block();
            };
        }

        static ShapeFunction fromTeapot() {
            return d -> Direction.Plane.HORIZONTAL.test(d) ? Block.box(4, 0, 4, 12, 6, 12) : Shapes.block();
        }

        static ShapeFunction fromTeaWare() {
            return d -> switch (d) {
                case EAST, WEST -> Block.box(3, 0, 0, 13, 2, 16);
                case NORTH, SOUTH -> Block.box(0, 0, 3, 16, 2, 13);
                default -> Shapes.block();
            };
        }

        static ShapeFunction fromCarpet() {
            return d -> Direction.Plane.HORIZONTAL.test(d) ? Block.box(0, 0, 0, 16, 1, 16) : Shapes.block();
        }

        static ShapeFunction fromBoard() {
            return d -> Direction.Plane.HORIZONTAL.test(d) ? Block.box(1, 0, 1, 15, 1, 15) : Shapes.block();
        }

        static ShapeFunction fromPorcelain() {
            return d -> Block.box(2, 0, 2, 14, 16, 14);
        }

        static ShapeFunction fromPorcelainSmall() {
            return d -> Block.box(5, 0, 5, 11, 12, 11);
        }

        static ShapeFunction fromLantern() {
            return d -> Shapes.or(Block.box(2, 2, 2, 14, 14, 14),
                    Block.box(5, 0, 5, 11, 16, 11));
        }

        static ShapeFunction fromFestivalLantern() {
            return d -> Shapes.or(Block.box(2, 2, 2, 14, 14, 14),
                    Block.box(5, 0, 5, 11, 16, 11),
                    Block.box(0, 3, 0, 16, 13, 16));
        }

        static ShapeFunction fromCandlestick() {
            return d -> Block.box(5, 0, 5, 11, 13, 11);
        }

        static ShapeFunction fromBigCandlestick() {
            return d -> Block.box(2, 0, 2, 14, 14, 14);
        }

        static ShapeFunction fromCoveredLamp() {
            return d -> Block.box(4, 0, 4, 12, 16, 12);
        }

        static ShapeFunction fromStoneLamp() {
            return d -> Block.box(3, 0, 3, 13, 16, 13);
        }

        static ShapeFunction fromWaterBowl() {
            return d -> Block.box(0, 0, 0, 16, 5, 16);
        }

        static ShapeFunction fromFishBowl() {
            return d -> Block.box(1, 0, 1, 15, 6, 15);
        }

        static ShapeFunction fromFishTank() {
            return d -> Shapes.join(Shapes.block(), Block.box(1, 1, 1, 15, 16, 15), BooleanOp.ONLY_FIRST);
        }

        static ShapeFunction fromWaterTank() {
            return d -> Shapes.join(Block.box(1, 0, 1, 15, 16, 15), Block.box(3, 3, 3, 13, 16, 13), BooleanOp.ONLY_FIRST);
        }

        static ShapeFunction fromOilLamp() {
            return d -> switch (d) {
                case SOUTH -> Block.box(5, 4, 0, 11, 12, 8);
                case EAST -> Block.box(0, 4, 5, 8, 12, 11);
                case NORTH -> Block.box(5, 4, 8, 11, 12, 16);
                case WEST -> Block.box(8, 4, 5, 16, 12, 11);
                case DOWN -> Block.box(5, 5, 5, 11, 16, 11);
                case UP -> Block.box(5, 0, 5, 11, 8, 11);
            };
        }

        static ShapeFunction fromEmptyCandlestick() {
            return d -> switch (d) {
                case SOUTH -> Block.box(5, 5, 0, 11, 16, 11);
                case EAST -> Block.box(0, 5, 5, 11, 16, 11);
                case NORTH -> Block.box(5, 5, 5, 11, 16, 16);
                case WEST -> Block.box(5, 5, 5, 16, 16, 11);
                default -> Block.box(5, 0, 5, 11, 16, 11);
            };
        }

        static ShapeFunction fromFactoryLamp() {
            return d -> switch (d) {
                case SOUTH -> Block.box(4, 4, 0, 12, 12, 8);
                case EAST -> Block.box(0, 4, 4, 8, 12, 12);
                case NORTH -> Block.box(4, 4, 8, 12, 12, 16);
                case WEST -> Block.box(8, 4, 4, 16, 12, 12);
                case DOWN -> Block.box(4, 8, 4, 12, 16, 12);
                case UP -> Block.box(4, 0, 4, 12, 8, 12);
            };
        }

        static ShapeFunction fromFan() {
            return d -> switch (d) {
                case EAST -> Block.box(1, 0, 1, 6, 15, 15);
                case SOUTH -> Block.box(1, 0, 1, 15, 15, 6);
                case WEST -> Block.box(10, 1, 1, 16, 15, 15);
                case NORTH -> Block.box(1, 1, 10, 15, 15, 16);
                case UP -> Block.box(1, 0, 1, 15, 6, 15);
                case DOWN -> Block.box(1, 10, 1, 15, 16, 15);
            };
        }

        static ShapeFunction fromScreen() {
            return d -> switch (d) {
                case SOUTH -> Block.box(0, 0, 0, 16, 16, 2);
                case WEST -> Block.box(14, 0, 0, 16, 16, 16);
                case NORTH -> Block.box(0, 0, 14, 16, 16, 16);
                default -> Block.box(0, 0, 0, 2, 16, 16);
            };
        }

        static ShapeFunction fromScreen2() {
            return d -> switch (d) {
                case SOUTH -> Block.box(0, 0, 2, 16, 16, 3);
                case WEST -> Block.box(13, 0, 0, 14, 16, 16);
                case NORTH -> Block.box(0, 0, 13, 16, 16, 14);
                default -> Block.box(2, 0, 0, 3, 16, 16);
            };
        }

        static ShapeFunction fromVentFan() {
            return d -> switch (d) {
                case SOUTH, NORTH -> Block.box(0, 0, 2, 16, 16, 14);
                case EAST, WEST -> Block.box(2, 0, 0, 14, 16, 16);
                default -> Block.box(0, 2, 0, 16, 14, 16);
            };
        }

        static ShapeFunction fromTechTable() {
            return d -> Shapes.or(Block.box(2, 0, 2, 14, 10, 14),
                    Block.box(0, 10, 0, 16, 16, 16));
        }

        static ShapeFunction fromHologramBase() {
            return d -> switch (d) {
                case EAST -> Block.box(0, 1, 1, 2, 15, 15);
                case SOUTH -> Block.box(1, 1, 0, 15, 15, 2);
                case WEST -> Block.box(14, 1, 1, 16, 15, 15);
                case NORTH -> Block.box(1, 1, 14, 15, 15, 16);
                case UP -> Block.box(1, 0, 1, 15, 2, 15);
                case DOWN -> Block.box(1, 14, 1, 15, 16, 15);
            };
        }
    }

    static {
        addCushionEntity();

        addIsotropic("black_tiles", BLOCK_STONE, ITEM_BASIC);
        addIsotropic("black_tile_slab", BLOCK_STONE, ITEM_BASIC);
        addIsotropic("black_tile_stairs", BLOCK_STONE, ITEM_BASIC);

        addIsotropic("cyan_tiles", BLOCK_STONE, ITEM_BASIC);
        addIsotropic("cyan_tile_slab", BLOCK_STONE, ITEM_BASIC);
        addIsotropic("cyan_tile_stairs", BLOCK_STONE, ITEM_BASIC);

        addIsotropic("yellow_tiles", BLOCK_STONE, ITEM_BASIC);
        addIsotropic("yellow_tile_slab", BLOCK_STONE, ITEM_BASIC);
        addIsotropic("yellow_tile_stairs", BLOCK_STONE, ITEM_BASIC);

        addIsotropic("blue_tiles", BLOCK_STONE, ITEM_BASIC);
        addIsotropic("blue_tile_slab", BLOCK_STONE, ITEM_BASIC);
        addIsotropic("blue_tile_stairs", BLOCK_STONE, ITEM_BASIC);

        addIsotropic("green_tiles", BLOCK_STONE, ITEM_BASIC);
        addIsotropic("green_tile_slab", BLOCK_STONE, ITEM_BASIC);
        addIsotropic("green_tile_stairs", BLOCK_STONE, ITEM_BASIC);

        addIsotropic("red_tiles", BLOCK_STONE, ITEM_BASIC);
        addIsotropic("red_tile_slab", BLOCK_STONE, ITEM_BASIC);
        addIsotropic("red_tile_stairs", BLOCK_STONE, ITEM_BASIC);

        addIsotropic("steel_tiles", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("steel_tile_slab", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("steel_tile_stairs", BLOCK_IRON, ITEM_BASIC);

        addIsotropic("copper_tiles", BLOCK_COPPER, ITEM_BASIC);
        addIsotropic("copper_tile_slab", BLOCK_COPPER, ITEM_BASIC);
        addIsotropic("copper_tile_stairs", BLOCK_COPPER, ITEM_BASIC);

        addIsotropic("glass_tiles", BLOCK_GLASS, ITEM_BASIC);
        addIsotropic("glass_tile_slab", BLOCK_GLASS, ITEM_BASIC);
        addIsotropic("glass_tile_stairs", BLOCK_GLASS, ITEM_BASIC);

        addIsotropic("mud_wall_block", BLOCK_MUD, ITEM_BASIC);
        addIsotropic("mud_wall_slab", BLOCK_MUD, ITEM_BASIC);
        addIsotropic("mud_wall_stairs", BLOCK_MUD, ITEM_BASIC);

        addIsotropic("framed_mud_wall_block", BLOCK_MUD, ITEM_BASIC);

        addIsotropic("lined_mud_wall_block", BLOCK_MUD, ITEM_BASIC);
        addIsotropic("lined_mud_wall_slab", BLOCK_MUD, ITEM_BASIC);
        addIsotropic("lined_mud_wall_stairs", BLOCK_MUD, ITEM_BASIC);

        addIsotropic("crossed_mud_wall_slab", BLOCK_MUD, ITEM_BASIC);
        addIsotropic("crossed_mud_wall_stairs", BLOCK_MUD, ITEM_BASIC);

        addIsotropic("dirty_mud_wall_block", BLOCK_MUD, ITEM_BASIC);
        addIsotropic("dirty_mud_wall_slab", BLOCK_MUD, ITEM_BASIC);
        addIsotropic("dirty_mud_wall_stairs", BLOCK_MUD, ITEM_BASIC);

        addIsotropic("cyan_bricks", BLOCK_BRICK, ITEM_BASIC);
        addIsotropic("cyan_brick_slab", BLOCK_BRICK, ITEM_BASIC);
        addIsotropic("cyan_brick_stairs", BLOCK_BRICK, ITEM_BASIC);

        addIsotropic("black_bricks", BLOCK_BRICK, ITEM_BASIC);
        addIsotropic("black_brick_slab", BLOCK_BRICK, ITEM_BASIC);
        addIsotropic("black_brick_stairs", BLOCK_BRICK, ITEM_BASIC);

        addIsotropic("varnished_wood", BLOCK_WOOD, ITEM_BASIC);
        addIsotropic("varnished_log", BLOCK_WOOD, ITEM_BASIC);
        addIsotropic("varnished_log_slab", BLOCK_WOOD, ITEM_BASIC);

        addIsotropic("ebony_wood", BLOCK_WOOD, ITEM_BASIC);
        addIsotropic("ebony_log", BLOCK_WOOD, ITEM_BASIC);
        addIsotropic("ebony_log_slab", BLOCK_WOOD, ITEM_BASIC);

        addIsotropic("mahogany_wood", BLOCK_WOOD, ITEM_BASIC);
        addIsotropic("mahogany_log", BLOCK_WOOD, ITEM_BASIC);
        addIsotropic("mahogany_log_slab", BLOCK_WOOD, ITEM_BASIC);

        addIsotropic("varnished_planks", BLOCK_WOOD, ITEM_BASIC);
        addIsotropic("varnished_slab", BLOCK_WOOD, ITEM_BASIC);
        addIsotropic("varnished_stairs", BLOCK_WOOD, ITEM_BASIC);

        addIsotropic("ebony_planks", BLOCK_WOOD, ITEM_BASIC);
        addIsotropic("ebony_slab", BLOCK_WOOD, ITEM_BASIC);
        addIsotropic("ebony_stairs", BLOCK_WOOD, ITEM_BASIC);

        addIsotropic("mahogany_planks", BLOCK_WOOD, ITEM_BASIC);
        addIsotropic("mahogany_slab", BLOCK_WOOD, ITEM_BASIC);
        addIsotropic("mahogany_stairs", BLOCK_WOOD, ITEM_BASIC);

        addIsotropic("sandstone_pillar", BLOCK_SANDSTONE, ITEM_BASIC);

        addIsotropic("polished_sandstone", BLOCK_SANDSTONE, ITEM_BASIC);
        addIsotropic("polished_sandstone_slab", BLOCK_SANDSTONE, ITEM_BASIC);

        addIsotropic("sandstone_bricks", BLOCK_SANDSTONE, ITEM_BASIC);
        addIsotropic("sandstone_brick_slab", BLOCK_SANDSTONE, ITEM_BASIC);
        addIsotropic("sandstone_brick_stairs", BLOCK_SANDSTONE, ITEM_BASIC);

        addIsotropic("sandstone_small_bricks", BLOCK_SANDSTONE, ITEM_BASIC);
        addIsotropic("sandstone_small_brick_slab", BLOCK_SANDSTONE, ITEM_BASIC);
        addIsotropic("sandstone_small_brick_stairs", BLOCK_SANDSTONE, ITEM_BASIC);

        addIsotropic("red_sandstone_pillar", BLOCK_SANDSTONE, ITEM_BASIC);

        addIsotropic("polished_red_sandstone", BLOCK_SANDSTONE, ITEM_BASIC);
        addIsotropic("polished_red_sandstone_slab", BLOCK_SANDSTONE, ITEM_BASIC);

        addIsotropic("red_sandstone_bricks", BLOCK_SANDSTONE, ITEM_BASIC);
        addIsotropic("red_sandstone_brick_slab", BLOCK_SANDSTONE, ITEM_BASIC);
        addIsotropic("red_sandstone_brick_stairs", BLOCK_SANDSTONE, ITEM_BASIC);

        addIsotropic("red_sandstone_small_bricks", BLOCK_SANDSTONE, ITEM_BASIC);
        addIsotropic("red_sandstone_small_brick_slab", BLOCK_SANDSTONE, ITEM_BASIC);
        addIsotropic("red_sandstone_small_brick_stairs", BLOCK_SANDSTONE, ITEM_BASIC);

        addIsotropic("stone_brick_pillar", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("stone_brick_pavement", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("stone_brick_pavement_slab", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("deepslate_pillar", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("deepslate_pavement", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("deepslate_pavement_slab", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("mossy_deepslate_bricks", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("mossy_deepslate_brick_slab", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("mossy_deepslate_brick_stairs", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("blackstone_pillar", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("blackstone_pavement", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("blackstone_pavement_slab", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("gilded_blackstone_bricks", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("gilded_blackstone_brick_slab", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("gilded_blackstone_brick_stairs", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("gilded_blackstone_brick_pillar", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("chiseled_gilded_blackstone", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("luxury_gilded_blackstone", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("maya_stone", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("maya_stone_slab", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("maya_stone_stairs", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("maya_stonebricks", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("maya_stonebrick_slab", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("maya_stonebrick_stairs", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("maya_bricks", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("maya_brick_slab", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("maya_brick_stairs", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("maya_polished_stonebricks", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("maya_polished_stonebrick_slab", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("maya_polished_stonebrick_stairs", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("maya_mossy_stonebricks", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("maya_mossy_stonebrick_slab", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("maya_mossy_stonebrick_stairs", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("maya_mossy_bricks", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("maya_mossy_brick_slab", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("maya_mossy_brick_stairs", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("maya_chiseled_stonebricks", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("maya_cut_stonebricks", BLOCK_HARD_STONE, ITEM_BASIC);

        addBasic("maya_single_screw_thread_stone", s -> Shapes.block(), false, BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("maya_double_screw_thread_stone", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("maya_quad_screw_thread_stone", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("maya_pictogram_stone", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("maya_skull_stone", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("maya_pillar", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("maya_mossy_pillar", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("aztec_stonebricks", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("aztec_stonebrick_slab", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("aztec_stonebrick_stairs", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("aztec_mossy_stonebricks", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("aztec_mossy_stonebrick_slab", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("aztec_mossy_stonebrick_stairs", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("aztec_sculpture_stone", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("aztec_chiseled_stonebricks", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("aztec_cut_stonebricks", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("inca_stone", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("inca_stone_slab", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("inca_stone_stairs", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("inca_stonebricks", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("inca_stonebrick_slab", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("inca_stonebrick_stairs", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("inca_bricks", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("inca_brick_slab", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("inca_brick_stairs", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("cut_obsidian", BLOCK_OBSIDIAN, ITEM_BASIC);
        addIsotropic("cut_obsidian_slab", BLOCK_OBSIDIAN, ITEM_BASIC);
        addIsotropic("cut_obsidian_pillar", BLOCK_OBSIDIAN, ITEM_BASIC);

        addIsotropic("cut_obsidian_bricks", BLOCK_OBSIDIAN, ITEM_BASIC);
        addIsotropic("cut_obsidian_brick_slab", BLOCK_OBSIDIAN, ITEM_BASIC);
        addIsotropic("cut_obsidian_brick_stairs", BLOCK_OBSIDIAN, ITEM_BASIC);

        addIsotropic("crying_obsidian_bricks", BLOCK_OBSIDIAN, ITEM_BASIC);
        addIsotropic("crying_obsidian_brick_slab", BLOCK_OBSIDIAN, ITEM_BASIC);
        addIsotropic("crying_obsidian_brick_stairs", BLOCK_OBSIDIAN, ITEM_BASIC);

        addIsotropic("cut_gold_block", BLOCK_GOLD, ITEM_BASIC);
        addIsotropic("cut_gold_block_slab", BLOCK_GOLD, ITEM_BASIC);
        addIsotropic("cut_gold_block_stairs", BLOCK_GOLD, ITEM_BASIC);

        addIsotropic("gold_bricks", BLOCK_GOLD, ITEM_BASIC);
        addIsotropic("gold_brick_slab", BLOCK_GOLD, ITEM_BASIC);
        addIsotropic("gold_brick_stairs", BLOCK_GOLD, ITEM_BASIC);
        addIsotropic("gold_pillar", BLOCK_GOLD, ITEM_BASIC);

        addIsotropic("chiseled_gold_block", BLOCK_GOLD, ITEM_BASIC);
        addIsotropic("painted_gold_block", BLOCK_GOLD, ITEM_BASIC);

        addIsotropic("bronze_block", BLOCK_BRONZE, ITEM_BASIC);
        addIsotropic("smooth_bronze_block", BLOCK_BRONZE, ITEM_BASIC);
        addIsotropic("inscription_bronze_block", BLOCK_BRONZE, ITEM_BASIC);

        addIsotropic("cut_bronze_block", BLOCK_BRONZE, ITEM_BASIC);
        addIsotropic("cut_bronze_block_slab", BLOCK_BRONZE, ITEM_BASIC);
        addIsotropic("cut_bronze_block_stairs", BLOCK_BRONZE, ITEM_BASIC);

        addIsotropic("chiseled_bronze_block", BLOCK_BRONZE, ITEM_BASIC);
        addBasic("screw_thread_bronze_block", s -> Shapes.block(), false, BLOCK_BRONZE, ITEM_BASIC);
        addIsotropic("bronze_pillar", BLOCK_BRONZE, ITEM_BASIC);

        addIsotropic("steel_block", BLOCK_HARD_IRON, ITEM_BASIC);
        addIsotropic("smooth_steel_block", BLOCK_HARD_IRON, ITEM_BASIC);
        addIsotropic("steel_pillar", BLOCK_HARD_IRON, ITEM_BASIC);

        addIsotropic("steel_floor", BLOCK_HARD_IRON, ITEM_BASIC);
        addIsotropic("steel_floor_slab", BLOCK_HARD_IRON, ITEM_BASIC);
        addIsotropic("steel_floor_stairs", BLOCK_HARD_IRON, ITEM_BASIC);

        addIsotropic("chiseled_steel_block", BLOCK_HARD_IRON, ITEM_BASIC);
        addIsotropic("hollow_steel_block", BLOCK_HOLLOW_IRON, ITEM_BASIC);
        addIsotropic("framed_steel_block", BLOCK_HARD_IRON, ITEM_BASIC);

        addIsotropic("factory_block", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_slab", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_stairs", BLOCK_IRON, ITEM_BASIC);

        addIsotropic("factory_block_rusting", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_slab_rusting", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_stairs_rusting", BLOCK_IRON, ITEM_BASIC);

        addIsotropic("factory_block_rusted", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_slab_rusted", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_stairs_rusted", BLOCK_IRON, ITEM_BASIC);

        addIsotropic("factory_danger", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_danger_rusting", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_danger_rusted", BLOCK_IRON, ITEM_BASIC);

        addIsotropic("factory_attention", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_attention_rusting", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_attention_rusted", BLOCK_IRON, ITEM_BASIC);

        addIsotropic("factory_electricity", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_electricity_rusting", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_electricity_rusted", BLOCK_IRON, ITEM_BASIC);

        addIsotropic("factory_toxic", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_toxic_rusting", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_toxic_rusted", BLOCK_IRON, ITEM_BASIC);

        addIsotropic("factory_radiation", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_radiation_rusting", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_radiation_rusted", BLOCK_IRON, ITEM_BASIC);

        addIsotropic("factory_biohazard", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_biohazard_rusting", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_biohazard_rusted", BLOCK_IRON, ITEM_BASIC);

        addIsotropic("factory_lamp_block", BLOCK_LIGHT, ITEM_BASIC);
        addIsotropic("factory_lamp_slab", BLOCK_LIGHT, ITEM_BASIC);
        addIsotropic("factory_lamp_stairs", BLOCK_LIGHT, ITEM_BASIC);

        addIsotropic("tech_lamp_block", BLOCK_LIGHT, ITEM_BASIC);
        addIsotropic("tech_lamp_slab", BLOCK_LIGHT, ITEM_BASIC);
        addIsotropic("tech_lamp_stairs", BLOCK_LIGHT, ITEM_BASIC);

        addIsotropic("translucent_lamp_block", BLOCK_LIGHT, ITEM_BASIC);
        addIsotropic("translucent_lamp_slab", BLOCK_LIGHT, ITEM_BASIC);
        addIsotropic("translucent_lamp_stairs", BLOCK_LIGHT, ITEM_BASIC);

        addIsotropic("steel_filings", BLOCK_SAND, ITEM_BASIC);

        addIsotropic("quartz_sand", BLOCK_SAND, ITEM_BASIC);
        addIsotropic("toughened_sand", BLOCK_HARD_SAND, ITEM_BASIC);

        addIsotropic("quartz_glass", BLOCK_LIGHT, ITEM_BASIC);
        addIsotropic("quartz_glass_slab", BLOCK_LIGHT, ITEM_BASIC);
        addIsotropic("quartz_glass_stairs", BLOCK_LIGHT, ITEM_BASIC);

        addIsotropic("toughened_glass", BLOCK_LIGHT, ITEM_BASIC);
        addIsotropic("toughened_glass_slab", BLOCK_LIGHT, ITEM_BASIC);
        addIsotropic("toughened_glass_stairs", BLOCK_LIGHT, ITEM_BASIC);

        addIsotropic("black_roof", BLOCK_ROOF, ITEM_STRUCTURE);
        addSpecial("black_roof_ridge", BLOCK_ROOF, ITEM_STRUCTURE);
        addIsotropic("black_roof_eave", BLOCK_ROOF, ITEM_STRUCTURE);
        addIsotropic("black_roof_flat", BLOCK_ROOF, ITEM_STRUCTURE);

        addPlant("dirt_slab", BLOCK_DIRT, ITEM_NATURE);
        addPlant("dirt_path_slab", BLOCK_DIRT, ITEM_NATURE);
        addPlant("grass_block_slab", BLOCK_DIRT, ITEM_NATURE);
        addPlant("mycelium_slab", BLOCK_DIRT, ITEM_NATURE);
        addPlant("podzol_slab", BLOCK_DIRT, ITEM_NATURE);

        addIsotropic("netherrack_slab", BLOCK_NETHER_STONE, ITEM_NATURE);
        addIsotropic("crimson_nylium_slab", BLOCK_NETHER_STONE, ITEM_NATURE);
        addIsotropic("warped_nylium_slab", BLOCK_NETHER_STONE, ITEM_NATURE);
        addIsotropic("end_stone_slab", BLOCK_END_STONE, ITEM_NATURE);

        addIsotropic("dirt_cobblestone", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("grass_cobblestone", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("sandy_cobblestone", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("snowy_cobblestone", BLOCK_SANDSTONE, ITEM_NATURE);

        addIsotropic("cobblestone_path", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("dirt_cobblestone_path", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("grass_cobblestone_path", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("sandy_cobblestone_path", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("snowy_cobblestone_path", BLOCK_SANDSTONE, ITEM_NATURE);

        addIsotropic("dirt_cobblestone_slab", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("grass_cobblestone_slab", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("sandy_cobblestone_slab", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("snowy_cobblestone_slab", BLOCK_SANDSTONE, ITEM_NATURE);

        addIsotropic("cobblestone_path_slab", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("dirt_cobblestone_path_slab", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("grass_cobblestone_path_slab", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("sandy_cobblestone_path_slab", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("snowy_cobblestone_path_slab", BLOCK_SANDSTONE, ITEM_NATURE);

        addIsotropic("dirt_cobblestone_stairs", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("grass_cobblestone_stairs", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("sandy_cobblestone_stairs", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("snowy_cobblestone_stairs", BLOCK_SANDSTONE, ITEM_NATURE);

        addIsotropic("cobblestone_path_stairs", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("dirt_cobblestone_path_stairs", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("grass_cobblestone_path_stairs", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("sandy_cobblestone_path_stairs", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("snowy_cobblestone_path_stairs", BLOCK_SANDSTONE, ITEM_NATURE);

        addPlant("ginkgo_leaves", BLOCK_LEAVES, ITEM_NATURE);
        addPlant("orange_maple_leaves", BLOCK_LEAVES, ITEM_NATURE);
        addPlant("red_maple_leaves", BLOCK_LEAVES, ITEM_NATURE);
        addPlant("peach_blossom", BLOCK_LEAVES, ITEM_NATURE);
        addPlant("peach_blossom_leaves", BLOCK_LEAVES, ITEM_NATURE);
        addPlant("cherry_blossom", BLOCK_LEAVES, ITEM_NATURE);
        addPlant("cherry_blossom_leaves", BLOCK_LEAVES, ITEM_NATURE);
        addPlant("white_cherry_blossom", BLOCK_LEAVES, ITEM_NATURE);
        addPlant("white_cherry_blossom_leaves", BLOCK_LEAVES, ITEM_NATURE);
        addPlant("plantable_leaves", BLOCK_LEAVES, ITEM_NATURE);
        addPlant("plantable_leaves_dark", BLOCK_LEAVES, ITEM_NATURE);
        addPlant("willow_leaves", BLOCK_LEAVES, ITEM_NATURE);

        addIsotropic("varnished_table", BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addIsotropic("varnished_big_table", BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addIsotropic("varnished_tall_table", BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("varnished_desk", ShapeFunction.fromBigTable(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("varnished_stool", ShapeFunction.fromLongStool(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("varnished_chair", ShapeFunction.fromChair(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("varnished_empty_shelf", ShapeFunction.fromShelf(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("varnished_shelf", ShapeFunction.fromShelf(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("varnished_divided_shelf", ShapeFunction.fromShelf(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);

        addIsotropic("ebony_table", BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addIsotropic("ebony_big_table", BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addIsotropic("ebony_tall_table", BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("ebony_desk", ShapeFunction.fromBigTable(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("ebony_stool", ShapeFunction.fromLongStool(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("ebony_chair", ShapeFunction.fromChair(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("ebony_empty_shelf", ShapeFunction.fromShelf(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("ebony_shelf", ShapeFunction.fromShelf(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("ebony_divided_shelf", ShapeFunction.fromShelf(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);

        addIsotropic("mahogany_table", BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addIsotropic("mahogany_big_table", BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addIsotropic("mahogany_tall_table", BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("mahogany_desk", ShapeFunction.fromBigTable(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("mahogany_stool", ShapeFunction.fromLongStool(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("mahogany_chair", ShapeFunction.fromChair(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("mahogany_empty_shelf", ShapeFunction.fromShelf(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("mahogany_shelf", ShapeFunction.fromShelf(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("mahogany_divided_shelf", ShapeFunction.fromShelf(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);

        addBasic("miniature_tree", ShapeFunction.fromMiniature(), false, BLOCK_MINIATURE, ITEM_FURNITURE);
        addBasic("miniature_cherry", ShapeFunction.fromMiniature(), false, BLOCK_MINIATURE, ITEM_FURNITURE);
        addBasic("miniature_ginkgo", ShapeFunction.fromMiniature(), false, BLOCK_MINIATURE, ITEM_FURNITURE);
        addBasic("miniature_maple", ShapeFunction.fromMiniature(), false, BLOCK_MINIATURE, ITEM_FURNITURE);
        addBasic("miniature_bamboo", ShapeFunction.fromMiniature(), false, BLOCK_MINIATURE, ITEM_FURNITURE);
        addBasic("miniature_coral", ShapeFunction.fromMiniature(), false, BLOCK_MINIATURE, ITEM_FURNITURE);
        addBasic("miniature_red_coral", ShapeFunction.fromMiniature(), false, BLOCK_MINIATURE, ITEM_FURNITURE);
        addBasic("miniature_mount", ShapeFunction.fromMiniature(), false, BLOCK_MINIATURE, ITEM_FURNITURE);
        addBasic("miniature_succulents", ShapeFunction.fromMiniature(), false, BLOCK_MINIATURE, ITEM_FURNITURE);

        addBasic("teapot", ShapeFunction.fromTeapot(), true, BLOCK_MINIATURE, ITEM_FURNITURE);
        addSpecial("cup", BLOCK_MINIATURE, ITEM_FURNITURE);
        addBasic("tea_ware", ShapeFunction.fromTeaWare(), true, BLOCK_MINIATURE, ITEM_FURNITURE);
        addSpecial("refreshments", BLOCK_DESSERT, ITEM_FURNITURE);
        addSpecial("fruit_platter", BLOCK_DESSERT, ITEM_FURNITURE);
        addBasic("calligraphy", ShapeFunction.fromCarpet(), true, BLOCK_CARPET, ITEM_FURNITURE);
        addBasic("ink_painting", ShapeFunction.fromCarpet(), true, BLOCK_CARPET, ITEM_FURNITURE);
        addBasic("weiqi_board", ShapeFunction.fromBoard(), true, BLOCK_BOARD, ITEM_FURNITURE);
        addBasic("xiangqi_board", ShapeFunction.fromBoard(), true, BLOCK_BOARD, ITEM_FURNITURE);

        addSpecial("plain_item_display", BLOCK_STONE_DISPLAY, ITEM_FUNCTIONAL);
        addSpecial("gorgeous_item_display", BLOCK_STONE_DISPLAY, ITEM_FUNCTIONAL);
        addSpecial("mechanical_item_display", BLOCK_METAL_DISPLAY, ITEM_FUNCTIONAL);
        addSpecial("tech_item_display", BLOCK_METAL_DISPLAY, ITEM_FUNCTIONAL);
        addSpecial("item_projector", BLOCK_METAL_DISPLAY, ITEM_FUNCTIONAL);

        addSpecial("plain_block_display", BLOCK_STONE_DISPLAY, ITEM_FUNCTIONAL);
        addSpecial("gorgeous_block_display", BLOCK_STONE_DISPLAY, ITEM_FUNCTIONAL);
        addSpecial("mechanical_block_display", BLOCK_METAL_DISPLAY, ITEM_FUNCTIONAL);
        addSpecial("tech_block_display", BLOCK_METAL_DISPLAY, ITEM_FUNCTIONAL);

        addDisplayBlockEntity();

        addSpecial("varnished_wardrobe", BLOCK_WOOD_WARDROBE, ITEM_FUNCTIONAL);
        addSpecial("ebony_wardrobe", BLOCK_WOOD_WARDROBE, ITEM_FUNCTIONAL);
        addSpecial("mahogany_wardrobe", BLOCK_WOOD_WARDROBE, ITEM_FUNCTIONAL);
        addSpecial("iron_wardrobe", BLOCK_METAL_WARDROBE, ITEM_FUNCTIONAL);
        addSpecial("glass_wardrobe", BLOCK_METAL_WARDROBE, ITEM_FUNCTIONAL);
        addSpecial("full_glass_wardrobe", BLOCK_GLASS_WARDROBE, ITEM_FUNCTIONAL);

        addWardrobeBlockEntity();

        addBasic("white_porcelain", ShapeFunction.fromPorcelain(), false, BLOCK_PORCELAIN, ITEM_FURNITURE);
        addBasic("white_porcelain_tall", ShapeFunction.fromPorcelain(), false, BLOCK_PORCELAIN, ITEM_FURNITURE);
        addBasic("white_porcelain_small", ShapeFunction.fromPorcelainSmall(), false, BLOCK_PORCELAIN, ITEM_FURNITURE);
        addBasic("bluewhite_porcelain", ShapeFunction.fromPorcelain(), false, BLOCK_PORCELAIN, ITEM_FURNITURE);
        addBasic("bluewhite_porcelain_tall", ShapeFunction.fromPorcelain(), false, BLOCK_PORCELAIN, ITEM_FURNITURE);
        addBasic("bluewhite_porcelain_small", ShapeFunction.fromPorcelainSmall(), false, BLOCK_PORCELAIN, ITEM_FURNITURE);
        addBasic("celadon_porcelain", ShapeFunction.fromPorcelain(), false, BLOCK_PORCELAIN, ITEM_FURNITURE);
        addBasic("celadon_porcelain_tall", ShapeFunction.fromPorcelain(), false, BLOCK_PORCELAIN, ITEM_FURNITURE);
        addBasic("celadon_porcelain_small", ShapeFunction.fromPorcelainSmall(), false, BLOCK_PORCELAIN, ITEM_FURNITURE);

        addBasic("paper_lantern", ShapeFunction.fromLantern(), false, BLOCK_LANTERN, ITEM_FURNITURE);
        addBasic("red_lantern", ShapeFunction.fromLantern(), false, BLOCK_LANTERN, ITEM_FURNITURE);
        addBasic("festival_lantern", ShapeFunction.fromFestivalLantern(), false, BLOCK_LANTERN, ITEM_FURNITURE);
        addBasic("oil_lamp", ShapeFunction.fromOilLamp(), false, BLOCK_METAL_LIGHT, ITEM_FURNITURE);
        addBasic("candlestick", ShapeFunction.fromCandlestick(), false, BLOCK_METAL_LIGHT, ITEM_FURNITURE);
        addBasic("big_candlestick", ShapeFunction.fromBigCandlestick(), false, BLOCK_METAL_LIGHT, ITEM_FURNITURE);
        addBasic("empty_candlestick", ShapeFunction.fromEmptyCandlestick(), false, BLOCK_METAL_WITHOUT_LIGHT, ITEM_FURNITURE);
        addBasic("covered_lamp", ShapeFunction.fromCoveredLamp(), false, BLOCK_WOOD_LIGHT, ITEM_FURNITURE);
        addBasic("roofed_lamp", ShapeFunction.fromBigCandlestick(), false, BLOCK_STONE_LIGHT, ITEM_FURNITURE);
        addBasic("stone_lamp", ShapeFunction.fromStoneLamp(), false, BLOCK_STONE_LIGHT, ITEM_FURNITURE);
        addBasic("deepslate_lamp", ShapeFunction.fromStoneLamp(), false, BLOCK_STONE_LIGHT, ITEM_FURNITURE);
        addBasic("blackstone_lamp", ShapeFunction.fromStoneLamp(), false, BLOCK_STONE_LIGHT, ITEM_FURNITURE);
        addBasic("fish_bowl", ShapeFunction.fromFishBowl(), false, BLOCK_STONE_NO_OCCLUSION, ITEM_FURNITURE);
        addBasic("dark_fish_bowl", ShapeFunction.fromFishBowl(), false, BLOCK_STONE_NO_OCCLUSION, ITEM_FURNITURE);
        addBasic("stone_water_bowl", ShapeFunction.fromWaterBowl(), false, BLOCK_STONE_NO_OCCLUSION, ITEM_FURNITURE);
        addBasic("stone_water_tank", ShapeFunction.fromWaterTank(), false, BLOCK_STONE_NO_OCCLUSION, ITEM_FURNITURE);
        addBasic("fish_tank", ShapeFunction.fromFishTank(), false, BLOCK_GLASS_NO_OCCLUSION, ITEM_FURNITURE);
        addBasic("empty_fish_tank", ShapeFunction.fromFishTank(), false, BLOCK_GLASS_NO_OCCLUSION, ITEM_FURNITURE);

        addBasic("small_book_stack", s -> Block.box(2, 0, 2, 14, 8, 14), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("big_book_stack", s -> Block.box(0, 0, 0, 16, 10, 16), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("empty_bottle_stack", s -> Block.box(2, 0, 2, 14, 8, 14), false, BLOCK_GLASS_NO_OCCLUSION, ITEM_FURNITURE);
        addBasic("bottle_stack", s -> Block.box(2, 0, 2, 14, 8, 14), false, BLOCK_GLASS_NO_OCCLUSION, ITEM_FURNITURE);
        addBasic("wood_globe", ShapeFunction.fromCoveredLamp(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("globe", ShapeFunction.fromCoveredLamp(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("solar_system_model", s -> Block.box(0, 0, 0, 16, 10, 16), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("big_solar_system_model", s -> Shapes.block(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("telescope", s -> Shapes.block(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);

        addPlant("ginkgo_leaves_shatter", BLOCK_LEAVES, ITEM_NATURE);
        addPlant("orange_maple_leaves_shatter", BLOCK_LEAVES, ITEM_NATURE);
        addPlant("red_maple_leaves_shatter", BLOCK_LEAVES, ITEM_NATURE);
        addPlant("peach_blossom_shatter", BLOCK_LEAVES, ITEM_NATURE);
        addPlant("cherry_blossom_shatter", BLOCK_LEAVES, ITEM_NATURE);
        addPlant("white_cherry_blossom_shatter", BLOCK_LEAVES, ITEM_NATURE);

        addBasic("factory_lamp", ShapeFunction.fromFactoryLamp(), false, BLOCK_METAL_LIGHT, ITEM_FURNITURE);
        addBasic("factory_lamp_broken", ShapeFunction.fromFactoryLamp(), false, BLOCK_METAL_WITHOUT_LIGHT, ITEM_FURNITURE);
        addBasic("factory_warning_lamp", ShapeFunction.fromFactoryLamp(), false, BLOCK_METAL_HALF_LIGHT, ITEM_FURNITURE);

        addSpecial("factory_light_bar", BLOCK_METAL_LIGHT_NO_COLLISSION, ITEM_FURNITURE);

        addBasic("factory_ceiling_lamp", s -> Block.box(0, 12, 0, 16, 16, 16), false, BLOCK_METAL_LIGHT, ITEM_FURNITURE);
        addBasic("factory_pendant", s -> Block.box(2, 4, 2, 14, 16, 14), false, BLOCK_METAL_LIGHT, ITEM_FURNITURE);

        addBasic("fan_blade", ShapeFunction.fromFan(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE);

        addSpecial("factory_vent_fan", BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE);
        addSpecial("factory_vent_fan_big", BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE);

        addBasic("steel_windmill", ShapeFunction.fromFan(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE);
        addBasic("iron_windmill", ShapeFunction.fromFan(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE);
        addBasic("wooden_windmill", ShapeFunction.fromFan(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);

        addSpecial("mechanical_console", BLOCK_METAL_LIGHT, ITEM_FURNITURE);

        addBasic("mechanical_screen", ShapeFunction.fromScreen2(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE);
        addBasic("mechanical_chair", ShapeFunction.fromChair(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE);

        addSpecial("tech_console", BLOCK_METAL_LIGHT, ITEM_FURNITURE);
        addBasic("tech_screen", ShapeFunction.fromScreen2(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE);
        addBasic("tech_chair", ShapeFunction.fromChair(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE);

        addBasic("screen_off", ShapeFunction.fromScreen(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE);
        addBasic("screen", ShapeFunction.fromScreen(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE);
        addBasic("screen_cube", ShapeFunction.fromScreen(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE);
        addBasic("screen_diagram", ShapeFunction.fromScreen(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE);
        addBasic("screen_dna", ShapeFunction.fromScreen(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE);
        addBasic("screen_list", ShapeFunction.fromScreen(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE);
        addBasic("screen_message", ShapeFunction.fromScreen(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE);
        addBasic("screen_threebodies", ShapeFunction.fromScreen(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE);
        addBasic("screen_transport", ShapeFunction.fromScreen(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE);

        addBasic("tech_table", ShapeFunction.fromTechTable(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE);
        addBasic("tech_table_circle", ShapeFunction.fromTechTable(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE);
        addBasic("tech_table_bigcircle", ShapeFunction.fromTechTable(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE);

        addBasic("hologram_base", ShapeFunction.fromHologramBase(), false, BLOCK_METAL_NO_COLLISSION, ITEM_FURNITURE);
        addItem("hologram_planet", ITEM_FURNITURE);
        addItem("hologram_dna", ITEM_FURNITURE);
        addItem("hologram_pictures", ITEM_FURNITURE);
        addItem("hologram_message", ITEM_FURNITURE);
        addItem("hologram_xekr_logo", ITEM_FURNITURE);

        addBasic("sign_entrance", ShapeFunction.fromScreen(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE);
        addBasic("sign_exit", ShapeFunction.fromScreen(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE);
        addBasic("sign_left", ShapeFunction.fromScreen(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE);
        addBasic("sign_right", ShapeFunction.fromScreen(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE);

        addBasic("small_sign_left", ShapeFunction.fromScreen(), false, BLOCK_METAL_LIGHT_NO_COLLISSION, ITEM_FURNITURE);
        addBasic("small_sign_right", ShapeFunction.fromScreen(), false, BLOCK_METAL_LIGHT_NO_COLLISSION, ITEM_FURNITURE);
        addBasic("small_sign_ground", s -> Block.box(0, 0, 0, 16, 1, 16), false, BLOCK_METAL_LIGHT_NO_COLLISSION, ITEM_FURNITURE);
    }
}
