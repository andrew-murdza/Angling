package com.eightsidedsquare.angling.core;

import com.eightsidedsquare.angling.common.item.RoeBlockItem;
import com.eightsidedsquare.angling.common.item.UrchinBucketItem;
import com.eightsidedsquare.angling.common.item.WormItem;
import net.minecraft.block.ComposterBlock;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.eightsidedsquare.angling.core.AnglingMod.MOD_ID;

@SuppressWarnings("unused")
public class AnglingItems {

    private static final net.minecraftforge.registries.DeferredRegister<Item> ITEMS = net.minecraftforge.registries.DeferredRegister.create(RegistryKeys.ITEM, MOD_ID);

    public static final net.minecraftforge.registries.RegistryObject<Item> FRY_SPAWN_EGG = createSpawnEgg("fry", AnglingEntities.FRY, 0xffffff, 0xffdd00);
    public static final net.minecraftforge.registries.RegistryObject<Item> SUNFISH_SPAWN_EGG = createSpawnEgg("sunfish", AnglingEntities.SUNFISH, 0x757434, 0x92571c);
    public static final net.minecraftforge.registries.RegistryObject<Item> PELICAN_SPAWN_EGG = createSpawnEgg("pelican", AnglingEntities.PELICAN, 0xe3e1d9, 0x614a29);
    public static final net.minecraftforge.registries.RegistryObject<Item> NAUTILUS_SPAWN_EGG = createSpawnEgg("nautilus", AnglingEntities.NAUTILUS, 0xd4ccc3, 0xae4635);
    public static final net.minecraftforge.registries.RegistryObject<Item> SEA_SLUG_SPAWN_EGG = createSpawnEgg("sea_slug", AnglingEntities.SEA_SLUG, 0x6f4e37, 0xff3800);
    public static final net.minecraftforge.registries.RegistryObject<Item> CRAB_SPAWN_EGG = createSpawnEgg("crab", AnglingEntities.CRAB, 0xbf4927, 0x798696);
    public static final net.minecraftforge.registries.RegistryObject<Item> DONGFISH_SPAWN_EGG = createSpawnEgg("dongfish", AnglingEntities.DONGFISH, 0x9f6060, 0xd3d1ad);
    public static final net.minecraftforge.registries.RegistryObject<Item> CATFISH_SPAWN_EGG = createSpawnEgg("catfish", AnglingEntities.CATFISH, 0x2c2a37, 0x665a50);
    public static final net.minecraftforge.registries.RegistryObject<Item> SEAHORSE_SPAWN_EGG = createSpawnEgg("seahorse", AnglingEntities.SEAHORSE, 0xe3c556, 0xb7833e);
    public static final net.minecraftforge.registries.RegistryObject<Item> BUBBLE_EYE_SPAWN_EGG = createSpawnEgg("bubble_eye", AnglingEntities.BUBBLE_EYE, 0xc24f1e, 0xdbc35b);
    public static final net.minecraftforge.registries.RegistryObject<Item> ANOMALOCARIS_SPAWN_EGG = createSpawnEgg("anomalocaris", AnglingEntities.ANOMALOCARIS, 0xebb595, 0x333333);
    public static final net.minecraftforge.registries.RegistryObject<Item> ANGLERFISH_SPAWN_EGG = createSpawnEgg("anglerfish", AnglingEntities.ANGLERFISH, 0x58251c, 0xd9fffc);
    public static final net.minecraftforge.registries.RegistryObject<Item> MAHI_MAHI_SPAWN_EGG = createSpawnEgg("mahi_mahi", AnglingEntities.MAHI_MAHI, 0xb2b729, 0x4f8f2f);
    public static final net.minecraftforge.registries.RegistryObject<Item> ROE = create("roe", () -> new RoeBlockItem(AnglingBlocks.ROE.get(), new Item.Settings().maxCount(1)));
    public static final net.minecraftforge.registries.RegistryObject<Item> SEA_SLUG_EGGS = create("sea_slug_eggs", () -> new BlockItem(AnglingBlocks.SEA_SLUG_EGGS.get(), new Item.Settings().maxCount(1)));
    public static final net.minecraftforge.registries.RegistryObject<Item> DUCKWEED = create("duckweed", () -> new PlaceableOnWaterItem(AnglingBlocks.DUCKWEED.get(), new Item.Settings()));
    public static final net.minecraftforge.registries.RegistryObject<Item> SARGASSUM = create("sargassum", () -> new PlaceableOnWaterItem(AnglingBlocks.SARGASSUM.get(), new Item.Settings()));
    public static final net.minecraftforge.registries.RegistryObject<Item> SUNFISH_BUCKET = createBucket("sunfish", AnglingEntities.SUNFISH);
    public static final net.minecraftforge.registries.RegistryObject<Item> NAUTILUS_BUCKET = createBucket("nautilus", AnglingEntities.NAUTILUS);
    public static final net.minecraftforge.registries.RegistryObject<Item> FRY_BUCKET = createBucket("fry", AnglingEntities.FRY);
    public static final net.minecraftforge.registries.RegistryObject<Item> SEA_SLUG_BUCKET = createBucket("sea_slug", AnglingEntities.SEA_SLUG);
    public static final net.minecraftforge.registries.RegistryObject<Item> CRAB_BUCKET = createBucket("crab", AnglingEntities.CRAB);
    public static final net.minecraftforge.registries.RegistryObject<Item> DONGFISH_BUCKET = createBucket("dongfish", AnglingEntities.DONGFISH);
    public static final net.minecraftforge.registries.RegistryObject<Item> CATFISH_BUCKET = createBucket("catfish", AnglingEntities.CATFISH);
    public static final net.minecraftforge.registries.RegistryObject<Item> SEAHORSE_BUCKET = createBucket("seahorse", AnglingEntities.SEAHORSE);
    public static final net.minecraftforge.registries.RegistryObject<Item> BUBBLE_EYE_BUCKET = createBucket("bubble_eye", AnglingEntities.BUBBLE_EYE);
    public static final net.minecraftforge.registries.RegistryObject<Item> ANOMALOCARIS_BUCKET = createBucket("anomalocaris", AnglingEntities.ANOMALOCARIS);
    public static final net.minecraftforge.registries.RegistryObject<Item> ANGLERFISH_BUCKET = createBucket("anglerfish", AnglingEntities.ANGLERFISH);
    public static final net.minecraftforge.registries.RegistryObject<Item> MAHI_MAHI_BUCKET = createBucket("mahi_mahi", AnglingEntities.MAHI_MAHI);
    public static final net.minecraftforge.registries.RegistryObject<Item> URCHIN_BUCKET = create("urchin_bucket", () -> new UrchinBucketItem(new Item.Settings().maxCount(1)));
    public static final net.minecraftforge.registries.RegistryObject<Item> WORM = create("worm", () -> new WormItem(new Item.Settings()));
    public static final net.minecraftforge.registries.RegistryObject<Item> SUNFISH = create("sunfish", () -> new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.2f).build())));
    public static final net.minecraftforge.registries.RegistryObject<Item> FRIED_SUNFISH = create("fried_sunfish", () -> new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(6).saturationModifier(0.9f).build())));

    public static void init(net.minecraftforge.eventbus.api.IEventBus bus) { ITEMS.register(bus); }

    public static java.util.Collection<Item> items() { return ITEMS.getEntries().stream().map(net.minecraftforge.registries.RegistryObject::get).toList(); }

    public static void init() {
        registerCompostable(DUCKWEED.get(), 0.3f);
        registerCompostable(SARGASSUM.get(), 0.3f);
        registerCompostable(WORM.get(), 1f);
        registerCompostable(AnglingBlocks.ALGAE.get().asItem(), 0.2f);
        registerCompostable(AnglingBlocks.PAPYRUS.get().asItem(), 0.2f);

    }

    private static <T extends Item> net.minecraftforge.registries.RegistryObject<T> create(String name, java.util.function.Supplier<T> factory) {
        return ITEMS.register(name, factory);
    }

    private static net.minecraftforge.registries.RegistryObject<Item> createBucket(String name, java.util.function.Supplier<? extends EntityType<?>> type) {
        return create(name + "_bucket", () -> new EntityBucketItem(type, () -> Fluids.WATER, () -> SoundEvents.ITEM_BUCKET_EMPTY_FISH, new Item.Settings().maxCount(1)));
    }

    private static net.minecraftforge.registries.RegistryObject<Item> createSpawnEgg(String name, java.util.function.Supplier<? extends EntityType<? extends MobEntity>> type, int primary, int secondary) {
        return create(name + "_spawn_egg", () -> new ForgeSpawnEggItem(type, primary, secondary, new Item.Settings()));
    }

    private static void registerCompostable(Item item, float chance) {
        ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(item, chance);
    }
}
