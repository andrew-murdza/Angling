package com.eightsidedsquare.angling.common.entity;

import com.eightsidedsquare.angling.core.AnglingItems;
import com.eightsidedsquare.angling.core.AnglingSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SeahorseEntity extends FishEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public SeahorseEntity(EntityType<? extends FishEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        goalSelector.add(1, new MoveToKelpGoal(this));
    }

    @Override
    protected SoundEvent getFlopSound() {
        return AnglingSounds.ENTITY_SEAHORSE_FLOP;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return AnglingSounds.ENTITY_SEAHORSE_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return AnglingSounds.ENTITY_SEAHORSE_DEATH;
    }

    @Override
    public ItemStack getBucketItem() {
        return new ItemStack(AnglingItems.SEAHORSE_BUCKET.get());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animationData) {
        animationData.add(new AnimationController<>(this, "controller", 2, this::controller));
    }

    private PlayState controller(AnimationState<SeahorseEntity> event) {
        if(!touchingWater) {
            event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.seahorse.flop"));
        }else {
            event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.seahorse.idle"));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    static class MoveToKelpGoal extends MoveToTargetPosGoal {

        public MoveToKelpGoal(SeahorseEntity mob) {
            super(mob, 1f, 6,6);
        }

        @Override
        public double getDesiredDistanceToTarget() {
            return 0d;
        }

        @Override
        protected boolean isTargetPos(WorldView world, BlockPos pos) {
            BlockState state = world.getBlockState(pos.up());
            return state.isOf(Blocks.KELP) || state.isOf(Blocks.KELP_PLANT);
        }
    }
}
