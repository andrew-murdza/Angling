package com.eightsidedsquare.angling.common.entity;

import com.eightsidedsquare.angling.common.entity.util.FishVariantInheritance;
import com.eightsidedsquare.angling.core.AnglingEntities;
import com.eightsidedsquare.angling.core.AnglingSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.registry.Registries;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RoeBlockEntity extends BlockEntity {

    int primaryColor;
    int secondaryColor;
    NbtCompound parentData;
    NbtCompound mateData;
    @Nullable
    String entityType;

    public RoeBlockEntity(BlockPos pos, BlockState state) {
        super(AnglingEntities.ROE.get(), pos, state);
        setColors(0xffffff, 0xffffff);
        setEntityType(EntityType.COD);
        setParentsData(new NbtCompound(), new NbtCompound());
    }

    public void setParentsData(NbtCompound parentData, NbtCompound mateData) {
        this.parentData = parentData.copy();
        this.mateData = mateData.copy();
        updateData();
    }

    public void setColors(int primaryColor, int secondaryColor) {
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        updateData();
    }

    public void setEntityType(EntityType<?> entityType) {
        this.entityType = Registries.ENTITY_TYPE.getId(entityType).toString();
        updateData();
    }

    private void updateData() {
        markDirty();
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
        }
    }

    public Optional<EntityType<?>> getEntityType() {
        if(entityType == null)
            return Optional.empty();
        return EntityType.get(entityType);
    }

    public void readFrom(ItemStack stack) {
        NbtCompound nbt = BlockItem.getBlockEntityNbt(stack);
        if(nbt != null) {
            readNbt(nbt);
            markDirty();
        }
    }

    public static int getColor(BlockState state, @Nullable BlockRenderView world, BlockPos pos, int tintIndex) {
        if(world != null && world.getBlockEntity(pos) instanceof RoeBlockEntity entity) {
            return tintIndex == 0 ? entity.primaryColor : entity.secondaryColor;
        }
        return 0xffffff;
    }

    public void hatch(ServerWorld world) {
        Random random = world.getRandom();
        int count = random.nextBetween(2, 5);
        getEntityType().ifPresent(type -> {
            FishVariantInheritance inheritance = FishVariantInheritance.getVariantInheritance(type);
            for(int i = 0; i < count; i++) {
                FryEntity entity = AnglingEntities.FRY.get().create(world);
                if(entity != null) {
                    entity.setPersistent();
                    entity.setGrowUpTo(entityType);
                    entity.setColor(random.nextBoolean() ? primaryColor : secondaryColor);
                    entity.setPosition(pos.getX() + 0.5d + random.nextGaussian() * 0.1d, pos.getY() + 0.25d, pos.getZ() + 0.5d + random.nextGaussian() * 0.1d);
                    entity.setYaw(random.nextFloat() * 360 - 180);
                    entity.setVariant(inheritance.getChild(parentData, mateData, world));
                    world.spawnEntity(entity);
                }
            }
            world.setBlockState(pos, Blocks.WATER.getDefaultState());
            world.playSound(null, pos, AnglingSounds.BLOCK_ROE_HATCH, SoundCategory.BLOCKS, 1, 1);
            world.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, getCachedState()), pos.getX() + 0.5d, pos.getY(), pos.getZ() + 0.5d, 20, 0.25d, 0.05d, 0.25d, 0);
        });
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("PrimaryColor", primaryColor);
        nbt.putInt("SecondaryColor", secondaryColor);
        nbt.put("ParentData", parentData);
        nbt.put("MateData", mateData);
        if(entityType != null)
            nbt.putString("EntityType", entityType);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        primaryColor = nbt.getInt("PrimaryColor");
        secondaryColor = nbt.getInt("SecondaryColor");
        if(nbt.contains("ParentData", NbtElement.COMPOUND_TYPE) && nbt.contains("MateData", NbtElement.COMPOUND_TYPE))
            setParentsData(nbt.getCompound("ParentData"), nbt.getCompound("MateData"));
        if(nbt.contains("EntityType", NbtElement.STRING_TYPE))
            entityType = nbt.getString("EntityType");
    }

    public static int getItemColor(ItemStack stack, int tintIndex) {
        NbtCompound nbt = BlockItem.getBlockEntityNbt(stack);
        if(nbt != null) {
            int primaryColor = nbt.getInt("PrimaryColor");
            int secondaryColor = nbt.getInt("SecondaryColor");
            return tintIndex == 0 ? primaryColor : secondaryColor;
        }
        return 0xffffff;
    }

}
