package com.eightsidedsquare.angling.core;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
public final class AnglingParticles {
    public static final DefaultParticleType ALGAE = AnglingRegistration.queue(RegistryKeys.PARTICLE_TYPE, new Identifier("angling", "algae"), new DefaultParticleType(true));
    public static final DefaultParticleType WORM = AnglingRegistration.queue(RegistryKeys.PARTICLE_TYPE, new Identifier("angling", "worm"), new DefaultParticleType(true));
    public static void init() {}
}
