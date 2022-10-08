package org.teacon.xkdeco.mixin;

import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.tags.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.teacon.xkdeco.init.XKDecoObjects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(MappedRegistry.class)
public class MappedRegistryMixin<T> {
	@ModifyVariable(method = "bindTags", at = @At("HEAD"), argsOnly = true)
	private Map<TagKey<T>, List<RegistryAccess.RegistryEntry<T>>> addEntries(Map<TagKey<T>, List<RegistryAccess.RegistryEntry<T>>> entries) {
		if (entries instanceof HashMap<TagKey<T>, List<RegistryAccess.RegistryEntry<T>>>) return entries;
		Map<TagKey<T>, List<RegistryAccess.RegistryEntry<T>>> newEntries = Map.copyOf(entries);
		//TODO: impl tags. broken here
		//XKDecoObjects.addSpecialWallTags(newEntries);
		return newEntries;
	}
}
