package com.eightsidedsquare.angling.client.model;

import com.eightsidedsquare.angling.common.entity.CrabEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;
import software.bernie.geckolib.constant.DataTickets;

import static com.eightsidedsquare.angling.core.AnglingMod.MOD_ID;

public class CrabEntityModel extends GeoModel<CrabEntity> {

    @Override
    public Identifier getModelResource(CrabEntity entity) {
        return new Identifier(MOD_ID, "geo/crab.geo.json");
    }

    @Override
    public Identifier getTextureResource(CrabEntity entity) {
        return entity.getVariant().texture();
    }

    @Override
    public Identifier getAnimationResource(CrabEntity entity) {
        return new Identifier(MOD_ID, "animations/crab.animation.json");
    }

    @Override
    public void setCustomAnimations(CrabEntity entity, long instanceId, AnimationState<CrabEntity> animationState) {
        super.setCustomAnimations(entity, instanceId, animationState);
        CoreGeoBone root = getAnimationProcessor().getBone("root");
        CoreGeoBone eyes = getAnimationProcessor().getBone("eyes");
        EntityModelData extraData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        if (eyes != null && extraData != null) {
            eyes.setRotX(extraData.headPitch() * ((float) Math.PI / 180F));
        }
        if(root != null) {
            boolean baby = entity.isBaby();
            root.setScaleX(baby ? 0.35f : 1f);
            root.setScaleY(baby ? 0.35f : 1f);
            root.setScaleZ(baby ? 0.35f : 1f);
            root.setPosY(baby ? -1.75f : 0f);
        }
    }
}
