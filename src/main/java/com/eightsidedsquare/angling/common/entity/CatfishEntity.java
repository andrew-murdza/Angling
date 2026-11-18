package com.eightsidedsquare.angling.common.entity;

import com.eightsidedsquare.angling.common.entity.ai.EatAlgaeGoal;
import com.eightsidedsquare.angling.core.AnglingItems;
import com.eightsidedsquare.angling.core.AnglingSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CatfishEntity extends FishEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public CatfishEntity(EntityType<? extends FishEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(1, new EatAlgaeGoal(this, 1.25d, 12));
    }

    @Override
    protected SoundEvent getFlopSound() {
        return AnglingSounds.ENTITY_CATFISH_FLOP;
    }

    @Nullable @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return AnglingSounds.ENTITY_CATFISH_HURT;
    }

    @Nullable @Override
    protected SoundEvent getDeathSound() {
        return AnglingSounds.ENTITY_CATFISH_DEATH;
    }

    @Override
    public ItemStack getBucketItem() {
        return new ItemStack(AnglingItems.CATFISH_BUCKET.get());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animationData) {
        animationData.add(new AnimationController<>(this, "controller", 2, this::controller));
    }

    private PlayState controller(AnimationState<CatfishEntity> event) {
        if(!touchingWater) {
            event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.catfish.flop"));
        }else {
            event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.catfish.idle"));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
