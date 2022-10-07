package org.teacon.xkdeco.item;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.quiltmc.qsl.item.group.api.QuiltItemGroup;
import org.teacon.xkdeco.XKDeco;

@MethodsReturnNonnullByDefault
public final class XKDecoCreativeModTab {
	public static final CreativeModeTab TAB_BASIC = QuiltItemGroup.createWithIcon(XKDeco.asResource("basic"), () -> makeIcon("black_tiles"));
	public static final CreativeModeTab TAB_STRUCTURE = QuiltItemGroup.createWithIcon(XKDeco.asResource("structure"), () -> makeIcon("special_wall_minecraft_cobblestone_wall"));
	public static final CreativeModeTab TAB_NATURE = QuiltItemGroup.createWithIcon(XKDeco.asResource("nature"), () -> makeIcon("grass_block_slab"));
	public static final CreativeModeTab TAB_FURNITURE = QuiltItemGroup.createWithIcon(XKDeco.asResource("furniture"), () -> makeIcon("varnished_big_table"));
	public static final CreativeModeTab TAB_FUNCTIONAL = QuiltItemGroup.createWithIcon(XKDeco.asResource("functional"), () -> makeIcon("tech_item_display"));

    private static ItemStack makeIcon(String itemId) {
        return Registry.ITEM.get(XKDeco.asResource(itemId)).getDefaultInstance();
    }
}
