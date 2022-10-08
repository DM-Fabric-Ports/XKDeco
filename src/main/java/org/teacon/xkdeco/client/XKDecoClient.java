package org.teacon.xkdeco.client;

import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Block;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;
import org.teacon.xkdeco.block.XKDecoBlock;
import org.teacon.xkdeco.blockentity.BlockDisplayBlockEntity;
import org.teacon.xkdeco.blockentity.ItemDisplayBlockEntity;
import org.teacon.xkdeco.blockentity.WallBlockEntity;
import org.teacon.xkdeco.client.renderer.*;
import org.teacon.xkdeco.entity.CushionEntity;
import org.teacon.xkdeco.init.XKDecoObjects;
import org.teacon.xkdeco.resource.SpecialWallResources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@MethodsReturnNonnullByDefault
public final class XKDecoClient implements ClientModInitializer {
	@Override
	public void onInitializeClient(ModContainer mod) {
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
			if (stack.getItem() instanceof BlockItem item) {
				var block = item.getBlock();
				if (List.of(getColoredItems()).contains(stack.getItem())) return getBlockColor(Registry.BLOCK.getKey(block)).getColor(block.defaultBlockState(), null, null, tintIndex);
			}
			return 0;
		}, getColoredItems());

		ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
			var block = state.getBlock();
			if (List.of(getColoredBlocks()).contains(block)) return getBlockColor(Registry.BLOCK.getKey(block)).getColor(state, world, pos, tintIndex);
			return 0;
		}, getColoredBlocks());

		setItemRenderers(ResourceLoader.get(PackType.CLIENT_RESOURCES));
		setEntityRenderers();
		setAdditionalPackFinder(ResourceLoader.get(PackType.CLIENT_RESOURCES));
	}

	private static Item[] getColoredItems() {
		ArrayList<Item> list = new ArrayList<>();
		XKDecoObjects.ITEMS.getObjects().forEach((item) -> {
			List<String> prefixes = List.of(
					XKDecoObjects.GRASS_PREFIX,
					XKDecoObjects.PLANTABLE_PREFIX,
					XKDecoObjects.WILLOW_PREFIX,
					XKDecoObjects.LEAVES_DARK_SUFFIX,
					XKDecoObjects.STONE_WATER_PREFIX
			);
			for (String prefix : prefixes) {
				if (item.getId().getPath().contains(prefix)) list.add(item.get());
			}
		});
		return list.toArray(new Item[0]);
	}

	private static Block[] getColoredBlocks() {
		ArrayList<Block> list = new ArrayList<>();
		XKDecoObjects.BLOCKS.getObjects().forEach((block) -> {
			List<String> prefixes = List.of(
					XKDecoObjects.GRASS_PREFIX,
					XKDecoObjects.PLANTABLE_PREFIX,
					XKDecoObjects.WILLOW_PREFIX,
					XKDecoObjects.LEAVES_DARK_SUFFIX,
					XKDecoObjects.STONE_WATER_PREFIX
			);
			for (String prefix : prefixes) {
				if (block.getId().getPath().contains(prefix)) list.add(block.get());
			}
		});
		return list.toArray(new Block[0]);
	}

	private BlockColor getBlockColor(ResourceLocation id) {
		var grassBlockColor = (BlockColor) (state, world, pos, tintIndex) -> {
			if (pos != null && world != null) {
				return BiomeColors.getAverageGrassColor(world, pos);
			}
			return GrassColor.get(0.5, 1.0);
		};
		var leavesBlockColor = (BlockColor) (state, world, pos, tintIndex) -> {
			if (pos != null && world != null) {
				return BiomeColors.getAverageFoliageColor(world, pos);
			}
			return FoliageColor.getDefaultColor();
		};
		var waterBlockColor = (BlockColor) (state, world, pos, tintIndex) -> {
			if (pos != null && world != null) {
				return BiomeColors.getAverageWaterColor(world, pos);
			}
			return 0x3f76e4;
		};

		var defaultColor = (BlockColor) (state, world, pos, tintIndex) -> 0x000000;

		if (id.getPath().contains(XKDecoObjects.GRASS_PREFIX)) return grassBlockColor;
		if (id.getPath().contains(XKDecoObjects.PLANTABLE_PREFIX)) return grassBlockColor;
		if (id.getPath().contains(XKDecoObjects.WILLOW_PREFIX)) return leavesBlockColor;
		if (id.getPath().contains(XKDecoObjects.LEAVES_DARK_SUFFIX)) return leavesBlockColor;
		if (id.getPath().contains(XKDecoObjects.STONE_WATER_PREFIX)) return waterBlockColor;

		return defaultColor;
	}

    public static Map<Block, RenderType> setCutoutBlocks(Map<Block, RenderType> mapL) {
		Map<Block, RenderType> map = new HashMap<>(Map.copyOf(mapL));
        for (var entry : XKDecoObjects.BLOCKS.getObjects()) {
            var id = entry.getId().getPath();
            if ("mechanical_screen".equals(id) || "tech_screen".equals(id)) {
                map.put(entry.get(), RenderType.translucent());
            } else if (entry.get() instanceof XKDecoBlock.Basic) {
				map.put(entry.get(), RenderType.cutout());
            } else if (entry.get() instanceof XKDecoBlock.Special) {
				map.put(entry.get(), RenderType.cutout());
            } else if (id.contains(XKDecoObjects.GLASS_SUFFIX) || id.contains(XKDecoObjects.TRANSLUCENT_PREFIX)) {
				map.put(entry.get(), RenderType.translucent());
            } else if (id.contains(XKDecoObjects.GLASS_PREFIX) || id.contains(XKDecoObjects.HOLLOW_PREFIX) || id.contains(XKDecoObjects.BIG_TABLE_SUFFIX) || id.contains(XKDecoObjects.TALL_TABLE_SUFFIX) || id.contains(XKDecoObjects.ROOF_SUFFIX)) {
				map.put(entry.get(), RenderType.cutout());
            } else if (id.contains(XKDecoObjects.GRASS_PREFIX) || id.contains(XKDecoObjects.LEAVES_SUFFIX) || id.contains(XKDecoObjects.BLOSSOM_SUFFIX)) {
				map.put(entry.get(), RenderType.cutoutMipped());
            }
        }
		return map;
    }

    public static void setItemRenderers(ResourceLoader resourceLoader) {
		resourceLoader.registerReloader(XKDecoWithoutLevelRenderer.INSTANCE);
    }

    public static void setEntityRenderers() {
		EntityRendererRegistry.register(CushionEntity.TYPE.get(), NoopRenderer::new);

		BlockEntityRendererRegistry.register(WallBlockEntity.TYPE.get(), WallRenderer::new);
		BlockEntityRendererRegistry.register(ItemDisplayBlockEntity.TYPE.get(), ItemDisplayRenderer::new);
		BlockEntityRendererRegistry.register(BlockDisplayBlockEntity.TYPE.get(), BlockDisplayRenderer::new);
    }

    public static void setAdditionalPackFinder(ResourceLoader resourceLoader) {
		resourceLoader.registerResourcePackProfileProvider(((profileAdder, factory) -> {
			Pack pack = SpecialWallResources.create(factory);
			if (pack != null) profileAdder.accept(pack);
		}));
    }
}
