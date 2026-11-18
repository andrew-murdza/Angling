package com.eightsidedsquare.angling.common.entity;

import com.eightsidedsquare.angling.core.AnglingItems;
import com.eightsidedsquare.angling.core.AnglingSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
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

public class DongfishEntity extends FishEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final TrackedData<Boolean> HAS_HORNGUS = DataTracker.registerData(DongfishEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public DongfishEntity(EntityType<? extends FishEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if(hasHorngus() && stack.isOf(Items.SHEARS)) {
            if (getWorld().isClient) return ActionResult.SUCCESS;
            playSound(AnglingSounds.ENTITY_DONGFISH_SHEAR, 1, 1);
            setHasHorngus(false);
            damage(getDamageSources().playerAttack(player), 1);
            if(!player.getAbilities().creativeMode)
                stack.damage(1, player, p -> p.sendToolBreakStatus(hand));
            return ActionResult.success(getWorld().isClient);
        }
        return super.interactMob(player, hand);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        dataTracker.startTracking(HAS_HORNGUS, true);
    }

    public boolean hasHorngus() {
        return dataTracker.get(HAS_HORNGUS);
    }

    public void setHasHorngus(boolean hasHorngus) {
        dataTracker.set(HAS_HORNGUS, hasHorngus);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("HasHorngus", hasHorngus());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        setHasHorngus(nbt.getBoolean("HasHorngus"));
    }

    @Override
    protected SoundEvent getFlopSound() {
        return AnglingSounds.ENTITY_DONGFISH_FLOP;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return AnglingSounds.ENTITY_DONGFISH_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return AnglingSounds.ENTITY_DONGFISH_DEATH;
    }

    @Override
    public ItemStack getBucketItem() {
        return new ItemStack(AnglingItems.DONGFISH_BUCKET.get());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animationData) {
        animationData.add(new AnimationController<>(this, "controller", 2, this::controller));
    }

    private PlayState controller(AnimationState<DongfishEntity> event) {
        if(!touchingWater) {
            event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.dongfish.flop"));
        }else {
            event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.dongfish.idle"));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void copyDataToStack(ItemStack stack) {
        super.copyDataToStack(stack);
        stack.getOrCreateNbt().putBoolean("HasHorngus", hasHorngus());
    }

    @Override
    public void copyDataFromNbt(NbtCompound nbt) {
        super.copyDataFromNbt(nbt);
        if(nbt.contains("HasHorngus"))
            setHasHorngus(nbt.getBoolean("HasHorngus"));
        else
            setHasHorngus(true);
    }
}
