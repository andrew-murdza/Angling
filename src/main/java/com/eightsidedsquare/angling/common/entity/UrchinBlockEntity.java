package com.eightsidedsquare.angling.common.entity;

import com.eightsidedsquare.angling.core.AnglingEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

public class UrchinBlockEntity extends BlockEntity implements GeoBlockEntity {

    private ItemStack hat = ItemStack.EMPTY;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public UrchinBlockEntity(BlockPos pos, BlockState state) {
        super(AnglingEntities.URCHIN.get(), pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("Hat", (hat != null ? hat : ItemStack.EMPTY).writeNbt(new NbtCompound()));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        hat = nbt.contains("Hat", NbtElement.COMPOUND_TYPE) ? ItemStack.fromNbt(nbt.getCompound("Hat")) : ItemStack.EMPTY;
    }

    public ItemStack getHat() {
        return hat;
    }

    public void setHat(ItemStack hat) {
        this.hat = hat;
    }

    public void update() {
        markDirty();
        if(world != null)
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "controller", 0, this::controller));
    }

    private PlayState controller(AnimationState<UrchinBlockEntity> event) {
        event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.urchin.idle"));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
