package com.eightsidedsquare.angling.common.entity.ai;

import com.eightsidedsquare.angling.common.entity.PelicanEntity;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

import java.util.function.Predicate;

/** Keeps a multi-tick activity conditional without restarting it each tick. */
final class ConditionalPelicanTask implements Task<PelicanEntity> {
    private final Predicate<PelicanEntity> condition;
    private final Task<? super PelicanEntity> task;

    ConditionalPelicanTask(Predicate<PelicanEntity> condition, Task<? super PelicanEntity> task) {
        this.condition = condition;
        this.task = task;
    }

    public MultiTickTask.Status getStatus() { return task.getStatus(); }
    public boolean tryStarting(ServerWorld world, PelicanEntity entity, long time) {
        return condition.test(entity) && task.tryStarting(world, entity, time);
    }
    public void tick(ServerWorld world, PelicanEntity entity, long time) {
        if (condition.test(entity)) task.tick(world, entity, time);
        else task.stop(world, entity, time);
    }
    public void stop(ServerWorld world, PelicanEntity entity, long time) { task.stop(world, entity, time); }
    public String getName() { return "Conditional " + task.getName(); }
}
