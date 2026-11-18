package com.eightsidedsquare.angling.client.renderer;

import com.eightsidedsquare.angling.client.model.StarfishBlockEntityModel;
import com.eightsidedsquare.angling.common.entity.StarfishBlockEntity;
import com.eightsidedsquare.angling.core.AnglingBlocks;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class StarfishBlockEntityRenderer extends GeoBlockRenderer<StarfishBlockEntity> {
    public StarfishBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(new StarfishBlockEntityModel());
    }

    @Override
    public RenderLayer getRenderType(StarfishBlockEntity entity, Identifier texture,
                                     VertexConsumerProvider buffers, float partialTick) {
        return RenderLayer.getEntityTranslucent(texture);
    }

    @Override
    public Color getRenderColor(StarfishBlockEntity entity, float partialTick, int packedLight) {
        return entity.getCachedState().isOf(AnglingBlocks.DEAD_STARFISH.get()) ? Color.WHITE
                : Color.ofOpaque(entity.isRainbow() ? StarfishBlockEntity.getRainbowColor() : entity.getColor());
    }

    @Override
    protected void rotateBlock(Direction facing, MatrixStack matrices) {
        // The model applies all six attachment directions itself.
    }

    @Override
    public void preRender(MatrixStack matrices, StarfishBlockEntity entity, BakedGeoModel model,
                          VertexConsumerProvider buffers, VertexConsumer buffer, boolean isReRender,
                          float partialTick, int packedLight, int packedOverlay,
                          float red, float green, float blue, float alpha) {
        super.preRender(matrices, entity, model, buffers, buffer, isReRender, partialTick, packedLight,
                packedOverlay, red, green, blue, alpha);
        if (!isReRender) matrices.translate(0, 0.01, 0);
    }
}
