package org.teacon.xkdeco.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import org.jetbrains.annotations.NotNull;
import org.teacon.xkdeco.block.SpecialItemDisplayBlock;
import org.teacon.xkdeco.blockentity.ItemDisplayBlockEntity;

import java.util.Objects;
import java.util.Random;

public class ItemDisplayRenderer implements BlockEntityRenderer<ItemDisplayBlockEntity> {
    private final ItemRenderer itemRenderer;
    private final Random random = new Random();

    public ItemDisplayRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render(ItemDisplayBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, @NotNull MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        // borrowed from ItemEntityRenderer

        float timer = Objects.requireNonNull(pBlockEntity.getLevel()).getGameTime() + pPartialTick;
        int speed = 1;
        BlockPos pos = pBlockEntity.getBlockPos();

        ItemStack itemstack = pBlockEntity.getItem();
        this.random.setSeed(itemstack.isEmpty() ? 187 : Item.getId(itemstack.getItem()) + itemstack.getDamageValue());

        float bobOffs;
        if (randomBobbing()) {
            this.random.setSeed(((long) pos.getX() << 32) ^ ((long) pos.getY() << 16) ^ pos.getZ());
            bobOffs = (float) (random.nextFloat() * Math.PI * 2);
        } else {
            bobOffs = 0;
        }

        pPoseStack.pushPose();
        BakedModel bakedmodel = this.itemRenderer.getModel(itemstack, pBlockEntity.getLevel(), null, speed);
        boolean flag = bakedmodel.isGui3d();
        int j = this.getRenderAmount(itemstack);
        float f1 = Mth.sin(timer / 10.0F + bobOffs) * 0.1F + 0.1F;
        @SuppressWarnings("deprecation")
        float f2 = bakedmodel.getTransforms().getTransform(ItemTransforms.TransformType.GROUND).scale.y();
        pPoseStack.translate(0.5, 1 + f1 + 0.25 * f2, 0.5);

        if (!pBlockEntity.getBlockState().getOptionalValue(SpecialItemDisplayBlock.POWERED).orElse(false)) {
            pPoseStack.mulPose(Vector3f.YP.rotation(timer / 20 + bobOffs));
        }

        if (!flag) {
            pPoseStack.translate(
                    -0.0F * (float) (j - 1) * 0.5F,
                    -0.0F * (float) (j - 1) * 0.5F,
                    -0.09375F * (float) (j - 1) * 0.5F);
        }

        for (int k = 0; k < j; ++k) {
            pPoseStack.pushPose();
            if (k > 0) {
                if (flag) pPoseStack.translate(
                            (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F,
                            (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F,
                            (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F);
                else pPoseStack.translate(
                            (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F,
                            (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F,
                            0.0D);
            }

            Level level = pBlockEntity.getLevel();
            int packedLight = LightTexture.pack(level.getBrightness(LightLayer.BLOCK, pos.above()), level.getBrightness(LightLayer.SKY, pos.above()));
            this.itemRenderer.render(itemstack, ItemTransforms.TransformType.GROUND, false, pPoseStack, pBufferSource, packedLight, OverlayTexture.NO_OVERLAY, bakedmodel);
            pPoseStack.popPose();
            if (!flag) {
                pPoseStack.translate(0.0, 0.0, 0.09375F);
            }
        }

        pPoseStack.popPose();
    }

    protected int getRenderAmount(ItemStack pStack) {
        int i = 1;
        if (pStack.getCount() > 48) {
            i = 5;
        } else if (pStack.getCount() > 32) {
            i = 4;
        } else if (pStack.getCount() > 16) {
            i = 3;
        } else if (pStack.getCount() > 1) {
            i = 2;
        }

        return i;
    }

    private boolean randomBobbing() {
        return false;
    }

}
