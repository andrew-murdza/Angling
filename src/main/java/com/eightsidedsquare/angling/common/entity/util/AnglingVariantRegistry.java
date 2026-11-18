package com.eightsidedsquare.angling.common.entity.util;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.eightsidedsquare.angling.core.AnglingMod.MOD_ID;

/** Forge owns registration, world ID persistence, tag reloads, and login synchronization. */
public final class AnglingVariantRegistry<T> implements Iterable<T> {
    private final RegistryKey<Registry<T>> key;
    private final DeferredRegister<T> entries;
    private final Supplier<IForgeRegistry<T>> registry;
    private final T fallback;

    public AnglingVariantRegistry(String name, String defaultName, T fallback) {
        Identifier id = new Identifier(MOD_ID, name);
        this.key = RegistryKey.ofRegistry(id);
        this.entries = DeferredRegister.create(id, MOD_ID);
        this.registry = entries.makeRegistry(() -> new RegistryBuilder<T>()
                .setDefaultKey(new Identifier(MOD_ID, defaultName)).hasTags());
        this.fallback = fallback;
    }

    public void register(IEventBus bus, Map<T, Identifier> values) {
        values.forEach((value, id) -> entries.register(id.getPath(), () -> value));
        entries.register(bus);
    }

    public RegistryKey<Registry<T>> getKey() {
        return key;
    }

    public T get(Identifier id) {
        if (id == null) return fallback;
        T value = registry.get().getValue(id);
        return value == null ? fallback : value;
    }

    public Identifier getId(T value) {
        return registry.get().getKey(value);
    }

    public List<T> getTagValues(TagKey<T> tag) {
        return registry.get().tags().getTag(tag).stream().toList();
    }

    @Override
    public Iterator<T> iterator() {
        return registry.get().iterator();
    }
}
