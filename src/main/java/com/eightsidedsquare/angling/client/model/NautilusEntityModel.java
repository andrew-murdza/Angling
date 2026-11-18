package com.eightsidedsquare.angling.client.model;

import com.eightsidedsquare.angling.common.entity.NautilusEntity;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;

public class NautilusEntityModel extends BasicEntityModel<NautilusEntity> {
    public NautilusEntityModel() {
        super("nautilus", true);
    }

    @Override
    public void setCustomAnimations(NautilusEntity entity, long instanceId, AnimationState<NautilusEntity> animationState) {
        super.setCustomAnimations(entity, instanceId, animationState);
        CoreGeoBone root = getAnimationProcessor().getBone("root");
        if(root != null) {
            root.setRotZ(entity.isTouchingWater() ? 0 : (float) (Math.PI / -2d));
            if (!entity.isTouchingWater()) root.setPosY(-1.5f);
        }
    }
}
