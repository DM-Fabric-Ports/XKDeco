package org.teacon.xkdeco.api.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;

public class RegistryUtil {
	@SafeVarargs
	public static Block[] getBlockEntityBlocks(Class<? extends Block>... class1) {
		ArrayList<Block> list = new ArrayList<Block>();
		for (Class<? extends Block> blockClass : class1) {
			list.addAll(List.of(Registry.BLOCK.entrySet().stream().filter(blockClass::isInstance).toArray(Block[]::new)));
		}
		return list.toArray(new Block[list.size()]);
	}
}
