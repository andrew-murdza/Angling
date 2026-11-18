package com.eightsidedsquare.angling.core;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraftforge.registries.RegisterEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Queues vanilla registry objects until Forge opens their registration event. */
public final class AnglingRegistration {
    private static final List<Consumer<RegisterEvent>> ENTRIES = new ArrayList<>();

    public static <T, V extends T> V queue(RegistryKey<Registry<T>> key, Identifier id, V value) {
        ENTRIES.add(event -> event.register(key, helper -> helper.register(id, value)));
        return value;
    }

    public static void register(RegisterEvent event) {
        ENTRIES.forEach(entry -> entry.accept(event));
    }
}
