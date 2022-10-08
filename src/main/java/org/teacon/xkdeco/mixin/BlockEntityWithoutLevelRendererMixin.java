package org.teacon.xkdeco.mixin;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityWithoutLevelRenderer.class)
public class BlockEntityWithoutLevelRendererMixin {
	@Shadow
	@Final
	private EntityModelSet entityModelSet;

	@Inject(method = "onResourceManagerReload", at = @At("HEAD"), cancellable = true)
	private void beforeRender(ResourceManager manager, CallbackInfo ci) {
		if (this.entityModelSet == null) ci.cancel();
	}
}
