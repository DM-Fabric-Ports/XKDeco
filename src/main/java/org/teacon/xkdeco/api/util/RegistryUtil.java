package org.teacon.xkdeco.api.util;

import java.util.ArrayList;
import java.util.List;

import com.dm.earth.deferred_registries.DeferredObject;
import com.dm.earth.deferred_registries.DeferredRegistries;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class RegistryUtil {
	@SafeVarargs
	public static Block[] getBlockEntityBlocks(Class<? extends Block>... class1) {
		ArrayList<Block> list = new ArrayList<Block>();
		for (Class<? extends Block> blockClass : class1) {
			list.addAll(List.of(Registry.BLOCK.entrySet().stream().filter(blockClass::isInstance).toArray(Block[]::new)));
		}
		return list.toArray(new Block[list.size()]);
	}

	@Nullable
	public static <T> DeferredObject<T> getRegistryObject(DeferredRegistries<T> registry, String name) {
		for (DeferredObject<T> deferredObject : registry.getObjects()) {
			if (deferredObject.getId().getPath().equals(name) || deferredObject.getId().toString().equals(name)) {
				return deferredObject;
			}
		}
		return null;
	}
}
