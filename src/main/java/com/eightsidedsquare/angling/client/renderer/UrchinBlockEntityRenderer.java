package com.eightsidedsquare.angling.client.renderer;

import com.eightsidedsquare.angling.client.model.UrchinBlockEntityModel;
import com.eightsidedsquare.angling.common.entity.UrchinBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.util.RenderUtils;

public class UrchinBlockEntityRenderer extends GeoBlockRenderer<UrchinBlockEntity> {
    public UrchinBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(new UrchinBlockEntityModel());
    }

    @Override
    public RenderLayer getRenderType(UrchinBlockEntity entity, Identifier texture,
                                     VertexConsumerProvider buffers, float partialTick) {
        return RenderLayer.getEntityTranslucent(texture);
    }

    @Override
    public void renderRecursively(MatrixStack matrices, UrchinBlockEntity entity, GeoBone bone,
                                  RenderLayer renderType, VertexConsumerProvider buffers, VertexConsumer buffer,
                                  boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                                  float red, float green, float blue, float alpha) {
        if (!isReRender && bone.getName().equals("root") && !entity.getHat().isEmpty()) {
            matrices.push();
            RenderUtils.prepMatrixForBone(matrices, bone);
            matrices.translate(0, 0.625, 0);
            matrices.scale(0.5f, 0.5f, 0.5f);
            MinecraftClient.getInstance().getItemRenderer().renderItem(entity.getHat(),
                    ModelTransformationMode.FIXED, packedLight, OverlayTexture.DEFAULT_UV, matrices, buffers,
                    entity.getWorld(), 0);
            matrices.pop();
            buffer = buffers.getBuffer(renderType);
        }
        super.renderRecursively(matrices, entity, bone, renderType, buffers, buffer, isReRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
