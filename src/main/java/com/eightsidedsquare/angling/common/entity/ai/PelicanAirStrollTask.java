package com.eightsidedsquare.angling.common.entity.ai;

import com.eightsidedsquare.angling.common.entity.PelicanEntity;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.Map;

final class PelicanAirStrollTask extends MultiTickTask<PelicanEntity> {
    PelicanAirStrollTask() {
        super(Map.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT));
    }

    @Override
    protected boolean shouldRun(ServerWorld world, PelicanEntity entity) {
        return entity.isFlying();
    }

    @Override
    protected void run(ServerWorld world, PelicanEntity entity, long time) {
        Vec3d target = NoPenaltyTargeting.find(entity, 10, 7);
        if (target != null) {
            entity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(target, 1f, 0));
        }
    }
}
