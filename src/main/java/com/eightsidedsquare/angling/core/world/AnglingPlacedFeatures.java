package com.eightsidedsquare.angling.core.world;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.PlacedFeature;

import static com.eightsidedsquare.angling.core.AnglingMod.MOD_ID;

/** Placed features and their Forge biome modifiers are defined in data/angling. */
public final class AnglingPlacedFeatures {
    public static final RegistryKey<PlacedFeature> PATCH_DUCKWEED = key("patch_duckweed");
    public static final RegistryKey<PlacedFeature> PATCH_SARGASSUM = key("patch_sargassum");
    public static final RegistryKey<PlacedFeature> OYSTER_REEF = key("oyster_reef");
    public static final RegistryKey<PlacedFeature> CLAMS = key("clams");
    public static final RegistryKey<PlacedFeature> WORMY_BLOCK = key("wormy_block");
    public static final RegistryKey<PlacedFeature> PATCH_PAPYRUS = key("patch_papyrus");

    private AnglingPlacedFeatures() {}

    private static RegistryKey<PlacedFeature> key(String name) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, new Identifier(MOD_ID, name));
    }

    public static void init() {
        // Dynamic registry contents are loaded from JSON when a world is loaded.
    }
}
