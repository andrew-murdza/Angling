package com.eightsidedsquare.angling.core.world;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;

import static com.eightsidedsquare.angling.core.AnglingMod.MOD_ID;

/** Keys for the configured features loaded from the mod's built-in data pack. */
public final class AnglingConfiguredFeatures {
    public static final RegistryKey<ConfiguredFeature<?, ?>> OYSTER_REEF = key("oyster_reef");
    public static final RegistryKey<ConfiguredFeature<?, ?>> CLAMS = key("clams");
    public static final RegistryKey<ConfiguredFeature<?, ?>> WORMY_BLOCK = key("wormy_block");
    public static final RegistryKey<ConfiguredFeature<?, ?>> PATCH_SARGASSUM = key("patch_sargassum");
    public static final RegistryKey<ConfiguredFeature<?, ?>> PATCH_DUCKWEED = key("patch_duckweed");
    public static final RegistryKey<ConfiguredFeature<?, ?>> PATCH_PAPYRUS = key("patch_papyrus");

    private AnglingConfiguredFeatures() {}

    private static RegistryKey<ConfiguredFeature<?, ?>> key(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, new Identifier(MOD_ID, name));
    }
}
