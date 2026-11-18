package com.eightsidedsquare.angling.client.renderer;

import com.eightsidedsquare.angling.client.model.BasicEntityModel;
import com.eightsidedsquare.angling.common.entity.PelicanEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RotationAxis;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.util.RenderUtils;

public class PelicanEntityRenderer extends BasicEntityRenderer<PelicanEntity> {
    private final EntityRenderDispatcher entityRenderDispatcher;

    public PelicanEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new BasicEntityModel<>("pelican", false, "head_joint"));
        entityRenderDispatcher = ctx.getRenderDispatcher();
        shadowRadius = 0.35f;
    }

    @Override
    public void renderRecursively(MatrixStack matrices, PelicanEntity entity, GeoBone bone,
                                  RenderLayer renderType, VertexConsumerProvider buffers, VertexConsumer buffer,
                                  boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                                  float red, float green, float blue, float alpha) {
        if (!isReRender && bone.getName().equals("beak_bottom") && entity.isBeakOpen()
                && entity.getEntityInBeak().isPresent()) {
            Entity carried = entity.getEntityInBeak().get();
            matrices.push();
            // The stack already includes the animated parent bones in GeckoLib 4.
            RenderUtils.prepMatrixForBone(matrices, bone);
            // prepMatrixForBone returns to model coordinates after rotating around
            // the jaw pivot. Rest the carried entity on the jaw's upper surface.
            matrices.translate(0, 22.0 / 16.0, -17.0 / 16.0);
            matrices.scale(0.5f, 0.5f, 0.5f);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            entityRenderDispatcher.render(carried, 0, 0, 0, 0, partialTick, matrices, buffers, packedLight);
            matrices.pop();
            buffer = buffers.getBuffer(renderType);
        }
        super.renderRecursively(matrices, entity, bone, renderType, buffers, buffer, isReRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
