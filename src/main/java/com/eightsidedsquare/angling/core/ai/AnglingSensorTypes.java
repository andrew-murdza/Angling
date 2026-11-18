package com.eightsidedsquare.angling.core.ai;

import com.eightsidedsquare.angling.common.entity.ai.PelicanAttackablesSensor;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;

import java.util.function.Supplier;

import static com.eightsidedsquare.angling.core.AnglingMod.MOD_ID;

public class AnglingSensorTypes {

    public static final SensorType<PelicanAttackablesSensor> PELICAN_ATTACKABLES =  register("pelican_attackables", PelicanAttackablesSensor::new);

    private static <U extends Sensor<?>> SensorType<U> register(String id, Supplier<U> factory) {
        return com.eightsidedsquare.angling.core.AnglingRegistration.queue(net.minecraft.registry.RegistryKeys.SENSOR_TYPE, new Identifier(MOD_ID, id), new SensorType<>(factory));
    }
}
