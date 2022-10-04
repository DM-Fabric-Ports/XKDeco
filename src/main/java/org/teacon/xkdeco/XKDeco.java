package org.teacon.xkdeco;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.teacon.xkdeco.client.XKDecoClient;
import org.teacon.xkdeco.data.XKDecoBlockStateProvider;
import org.teacon.xkdeco.data.XKDecoEnUsLangProvider;
import org.teacon.xkdeco.entity.CushionEntity;
import org.teacon.xkdeco.init.XKDecoObjects;

@MethodsReturnNonnullByDefault
public final class XKDeco implements ModInitializer{
    public static final String ID = "xkdeco";

	@Override
	public void onInitialize(ModContainer mod) {

	}

    // public XKDeco() {
    //     // var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
    //     // XKDecoObjects.ENTITIES.register(modEventBus);
    //     // XKDecoObjects.BLOCKS.register(modEventBus);
    //     // XKDecoObjects.ITEMS.register(modEventBus);
    //     // XKDecoObjects.BLOCK_ENTITY.register(modEventBus);

    //     modEventBus.addGenericListener(Block.class, EventPriority.LOWEST, XKDecoObjects::addSpecialWallBlocks);
    //     modEventBus.addGenericListener(Item.class, EventPriority.LOWEST, XKDecoObjects::addSpecialWallItems);
    //     modEventBus.addGenericListener(BlockEntityType.class, EventPriority.LOWEST, XKDecoObjects::addSpecialWallBlockEntity);

    //     modEventBus.addListener(XKDecoBlockStateProvider::register);
    //     modEventBus.addListener(XKDecoEnUsLangProvider::register);

    //     if (FMLEnvironment.dist.isClient()) {
    //         modEventBus.addListener(XKDecoClient::setItemColors);
    //         modEventBus.addListener(XKDecoClient::setBlockColors);
    //         modEventBus.addListener(XKDecoClient::setCutoutBlocks);
    //         modEventBus.addListener(XKDecoClient::setItemRenderers);
    //         modEventBus.addListener(XKDecoClient::setEntityRenderers);
    //         modEventBus.addListener(XKDecoClient::setAdditionalPackFinder);
    //     }

    //     var forgeEventBus = MinecraftForge.EVENT_BUS;

    //     forgeEventBus.addListener(XKDecoObjects::addSpecialWallTags);

    //     forgeEventBus.addListener(CushionEntity::onRightClickBlock);
    //     forgeEventBus.addListener(CushionEntity::onBreakBlock);
    // }


}
