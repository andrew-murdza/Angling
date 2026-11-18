package com.eightsidedsquare.angling.client.renderer;

import com.eightsidedsquare.angling.client.model.NautilusEntityModel;
import com.eightsidedsquare.angling.common.entity.NautilusEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;

public class NautilusEntityRenderer extends BasicEntityRenderer<NautilusEntity> {
    public NautilusEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new NautilusEntityModel());
        withScale(0.65f);
    }
}
