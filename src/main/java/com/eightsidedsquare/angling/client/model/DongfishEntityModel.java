package com.eightsidedsquare.angling.client.model;

import com.eightsidedsquare.angling.common.entity.DongfishEntity;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;

public class DongfishEntityModel extends BasicEntityModel<DongfishEntity> {

    public DongfishEntityModel() {
        super("dongfish", true);
    }

    @Override
    public void setCustomAnimations(DongfishEntity entity, long instanceId, AnimationState<DongfishEntity> animationState) {
        super.setCustomAnimations(entity, instanceId, animationState);
        CoreGeoBone scungle = getAnimationProcessor().getBone("scungle");
        if(scungle != null) {
            scungle.setHidden(!entity.hasHorngus());
        }
    }
}
