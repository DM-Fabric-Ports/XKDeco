package org.teacon.xkdeco;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.teacon.xkdeco.entity.CushionEntity;
import org.teacon.xkdeco.init.XKDecoObjects;
import org.teacon.xkdeco.item.XKDecoCreativeModTab;

@MethodsReturnNonnullByDefault
public final class XKDeco implements ModInitializer{
    public static final String ID = "xkdeco";

	@Override
	public void onInitialize(ModContainer mod) {
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			CushionEntity.onRightClickBlock(player, world, hand, hitResult);
			return InteractionResult.PASS;
		});

		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
			CushionEntity.onBreakBlock(world, pos);
		});

		XKDecoCreativeModTab.init();

		XKDecoObjects.ENTITIES.register();
		XKDecoObjects.BLOCKS.register();
		XKDecoObjects.ITEMS.register();
		XKDecoObjects.BLOCK_ENTITY.register();

		XKDecoObjects.addSpecialWallBlocks();
		XKDecoObjects.addSpecialWallItems();
		XKDecoObjects.addSpecialWallBlockEntity();
	}

	public static ResourceLocation asResource(String path) {
		return new ResourceLocation(ID, path);
	}
}
