package org.teacon.xkdeco.mixin;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.teacon.xkdeco.client.XKDecoClient;

import java.util.Map;

@Mixin(ItemBlockRenderTypes.class)
public class ItemBlockRenderTypesMixin {
	@Mutable @Final @Shadow
	private static Map<Block, RenderType> TYPE_BY_BLOCK;

	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void onInit(CallbackInfo ci) {
		TYPE_BY_BLOCK = XKDecoClient.setCutoutBlocks(TYPE_BY_BLOCK);
	}
}
