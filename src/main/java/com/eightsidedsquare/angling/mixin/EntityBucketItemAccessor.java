package com.eightsidedsquare.angling.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.item.EntityBucketItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityBucketItem.class)
public interface EntityBucketItemAccessor {
    @Invoker(value = "getFishType", remap = false)
    EntityType<?> angling$getFishType();
}
