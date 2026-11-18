package com.eightsidedsquare.angling.client.renderer;

import com.eightsidedsquare.angling.client.model.BasicEntityModel;
import com.eightsidedsquare.angling.common.entity.AnglerfishEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import static com.eightsidedsquare.angling.core.AnglingMod.MOD_ID;

public class AnglerfishEntityRenderer extends BasicEntityRenderer<AnglerfishEntity> {
    private static final Identifier OVERLAY = new Identifier(MOD_ID, "textures/entity/anglerfish/anglerfish_overlay.png");

    public AnglerfishEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new BasicEntityModel<>("anglerfish", true));
        addRenderLayer(new AnglerfishLayerRenderer(this));
    }

    static class AnglerfishLayerRenderer extends GeoRenderLayer<AnglerfishEntity> {
        AnglerfishLayerRenderer(GeoRenderer<AnglerfishEntity> renderer) {
            super(renderer);
        }

        @Override
        public void render(MatrixStack matrices, AnglerfishEntity entity, BakedGeoModel model,
                           RenderLayer renderType, VertexConsumerProvider buffers, VertexConsumer buffer,
                           float partialTick, int packedLight, int packedOverlay) {
            RenderLayer layer = RenderLayer.getEntityTranslucent(OVERLAY);
            getRenderer().reRender(model, matrices, buffers, entity, layer, buffers.getBuffer(layer),
                    partialTick, LightmapTextureManager.MAX_LIGHT_COORDINATE, packedOverlay, 1, 1, 1, 1);
        }
    }
}
