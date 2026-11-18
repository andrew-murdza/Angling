package com.eightsidedsquare.angling.client.renderer;

import com.eightsidedsquare.angling.client.model.FryEntityModel;
import com.eightsidedsquare.angling.common.entity.FryEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import static com.eightsidedsquare.angling.core.AnglingMod.MOD_ID;

public class FryEntityRenderer extends BasicEntityRenderer<FryEntity> {
    public FryEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new FryEntityModel());
        addRenderLayer(new FryEntityLayerRenderer(this));
    }

    @Override
    public Color getRenderColor(FryEntity entity, float partialTick, int packedLight) {
        return Color.ofOpaque(entity.getColor());
    }

    static class FryEntityLayerRenderer extends GeoRenderLayer<FryEntity> {
        private static final Identifier OUTSIDE_LAYER = new Identifier(MOD_ID, "textures/entity/fry/fry.png");

        FryEntityLayerRenderer(GeoRenderer<FryEntity> renderer) {
            super(renderer);
        }

        @Override
        public void render(MatrixStack matrices, FryEntity entity, BakedGeoModel model,
                           RenderLayer renderType, VertexConsumerProvider buffers, VertexConsumer buffer,
                           float partialTick, int packedLight, int packedOverlay) {
            RenderLayer layer = RenderLayer.getEntityTranslucent(OUTSIDE_LAYER);
            getRenderer().reRender(model, matrices, buffers, entity, layer, buffers.getBuffer(layer),
                    partialTick, packedLight, packedOverlay, 1, 1, 1, 1);
        }
    }
}
