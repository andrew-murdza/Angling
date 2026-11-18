package com.eightsidedsquare.angling.common.entity.util;

import com.eightsidedsquare.angling.core.tags.AnglingBiomeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraft.world.biome.Biome;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.eightsidedsquare.angling.core.AnglingMod.MOD_ID;

public record CrabVariant(Identifier texture, TagKey<Biome> biomeTag) {

    private static final Map<CrabVariant, Identifier> VARIANTS = new LinkedHashMap<>();

    public static final CrabVariant DUNGENESS = create("dungeness", AnglingBiomeTags.DUNGENESS_CRAB_BIOMES);
    public static final CrabVariant GHOST = create("ghost", AnglingBiomeTags.GHOST_CRAB_BIOMES);
    public static final CrabVariant BLUE_CLAW = create("blue_claw", AnglingBiomeTags.BLUE_CLAW_CRAB_BIOMES);

    public static final AnglingVariantRegistry<CrabVariant> REGISTRY =
            new AnglingVariantRegistry<>("crab_variant", "dungeness", DUNGENESS);

    public String getTranslationKey() {
        return "crab_variant." + this.getId().getNamespace() + "." + this.getId().getPath();
    }


    public Identifier getId() {
        return REGISTRY.getId(this);
    }

    public static CrabVariant fromId(String id) {
        return fromId(Identifier.tryParse(id));
    }

    public static CrabVariant fromId(Identifier id) {
        return REGISTRY.get(id);
    }

    private static CrabVariant create(String name, TagKey<Biome> biomeTag) {
        CrabVariant pattern = new CrabVariant(new Identifier(MOD_ID, "textures/entity/crab/" + name + ".png"), biomeTag);
        VARIANTS.put(pattern, new Identifier(MOD_ID, name));
        return pattern;
    }

    public static void init(IEventBus bus) {
        REGISTRY.register(bus, VARIANTS);
    }

    public static class Tag {

        public static final TagKey<CrabVariant> NATURAL_VARIANTS = of("natural_variants");

        private static TagKey<CrabVariant> of(String id) {
            return of(new Identifier(MOD_ID, id));
        }

        public static TagKey<CrabVariant> of(Identifier id) {
            return TagKey.of(REGISTRY.getKey(), id);
        }
    }

}
