package com.eightsidedsquare.angling.client.renderer;

import com.eightsidedsquare.angling.client.model.BasicEntityModel;
import com.eightsidedsquare.angling.common.entity.SeaSlugEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class SeaSlugEntityRenderer extends GeoEntityRenderer<SeaSlugEntity> {
    public SeaSlugEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new BasicEntityModel<>("sea_slug", false));
        addRenderLayer(new SeaSlugLayerRenderer(this));
        shadowRadius = 0.1f;
    }

    @Override
    public Color getRenderColor(SeaSlugEntity entity, float partialTick, int packedLight) {
        return Color.ofOpaque(entity.getBaseColor().color());
    }

    static class SeaSlugLayerRenderer extends GeoRenderLayer<SeaSlugEntity> {
        SeaSlugLayerRenderer(GeoRenderer<SeaSlugEntity> renderer) {
            super(renderer);
        }

        @Override
        public void render(MatrixStack matrices, SeaSlugEntity entity, BakedGeoModel model,
                           RenderLayer renderType, VertexConsumerProvider buffers, VertexConsumer buffer,
                           float partialTick, int packedLight, int packedOverlay) {
            Identifier texture = entity.getPattern().texture();
            if (texture == null) return;
            int color = entity.getPatternColor().color();
            RenderLayer layer = RenderLayer.getEntityCutoutNoCull(texture);
            getRenderer().reRender(model, matrices, buffers, entity, layer, buffers.getBuffer(layer),
                    partialTick, entity.isBioluminescent() ? LightmapTextureManager.MAX_LIGHT_COORDINATE : packedLight,
                    packedOverlay, ((color >> 16) & 255) / 255f, ((color >> 8) & 255) / 255f, (color & 255) / 255f, 1);
        }
    }
}
