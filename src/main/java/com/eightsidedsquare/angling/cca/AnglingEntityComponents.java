package com.eightsidedsquare.angling.cca;

import net.minecraft.entity.Entity;

/** Access to the breeding state stored directly on every fish by FishEntityMixin. */
public final class AnglingEntityComponents {
    public static final FishStateAccessor FISH_SPAWNING = new FishStateAccessor();

    private AnglingEntityComponents() {}

    public static final class FishStateAccessor {
        public FishSpawningComponent get(Entity entity) {
            return ((FishSpawningAccess) entity).angling$getFishSpawning();
        }
    }
}
