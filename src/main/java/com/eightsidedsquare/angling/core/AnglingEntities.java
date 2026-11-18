package com.eightsidedsquare.angling.core;

import com.eightsidedsquare.angling.common.entity.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public final class AnglingEntities {
    private static final net.minecraftforge.registries.DeferredRegister<EntityType<?>> ENTITIES = net.minecraftforge.registries.DeferredRegister.create(RegistryKeys.ENTITY_TYPE, AnglingMod.MOD_ID);
    private static final net.minecraftforge.registries.DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = net.minecraftforge.registries.DeferredRegister.create(RegistryKeys.BLOCK_ENTITY_TYPE, AnglingMod.MOD_ID);
    public static final net.minecraftforge.registries.RegistryObject<BlockEntityType<RoeBlockEntity>> ROE = BLOCK_ENTITIES.register("roe", () -> BlockEntityType.Builder.create(RoeBlockEntity::new, AnglingBlocks.ROE.get()).build(null));
    public static final net.minecraftforge.registries.RegistryObject<BlockEntityType<StarfishBlockEntity>> STARFISH = BLOCK_ENTITIES.register("starfish", () -> BlockEntityType.Builder.create(StarfishBlockEntity::new, AnglingBlocks.STARFISH.get(), AnglingBlocks.DEAD_STARFISH.get()).build(null));
    public static final net.minecraftforge.registries.RegistryObject<BlockEntityType<SeaSlugEggsBlockEntity>> SEA_SLUG_EGGS = BLOCK_ENTITIES.register("sea_slug_eggs", () -> BlockEntityType.Builder.create(SeaSlugEggsBlockEntity::new, AnglingBlocks.SEA_SLUG_EGGS.get()).build(null));
    public static final net.minecraftforge.registries.RegistryObject<BlockEntityType<AnemoneBlockEntity>> ANEMONE = BLOCK_ENTITIES.register("anemone", () -> BlockEntityType.Builder.create(AnemoneBlockEntity::new, AnglingBlocks.ANEMONE.get()).build(null));
    public static final net.minecraftforge.registries.RegistryObject<BlockEntityType<UrchinBlockEntity>> URCHIN = BLOCK_ENTITIES.register("urchin", () -> BlockEntityType.Builder.create(UrchinBlockEntity::new, AnglingBlocks.URCHIN.get()).build(null));
    public static final net.minecraftforge.registries.RegistryObject<EntityType<FryEntity>> FRY = ENTITIES.register("fry", () -> EntityType.Builder.create(FryEntity::new, SpawnGroup.WATER_AMBIENT).setDimensions(0.2f, 0.15f).maxTrackingRange(8).build("angling:fry"));
    public static final net.minecraftforge.registries.RegistryObject<EntityType<SunfishEntity>> SUNFISH = ENTITIES.register("sunfish", () -> EntityType.Builder.create(SunfishEntity::new, SpawnGroup.WATER_AMBIENT).setDimensions(0.5f, 0.3f).maxTrackingRange(8).build("angling:sunfish"));
    public static final net.minecraftforge.registries.RegistryObject<EntityType<PelicanEntity>> PELICAN = ENTITIES.register("pelican", () -> EntityType.Builder.create(PelicanEntity::new, SpawnGroup.AMBIENT).setDimensions(0.7f, 1.65f).maxTrackingRange(8).build("angling:pelican"));
    public static final net.minecraftforge.registries.RegistryObject<EntityType<NautilusEntity>> NAUTILUS = ENTITIES.register("nautilus", () -> EntityType.Builder.create(NautilusEntity::new, SpawnGroup.WATER_AMBIENT).setDimensions(0.5f, 0.4f).maxTrackingRange(8).build("angling:nautilus"));
    public static final net.minecraftforge.registries.RegistryObject<EntityType<SeaSlugEntity>> SEA_SLUG = ENTITIES.register("sea_slug", () -> EntityType.Builder.create(SeaSlugEntity::new, SpawnGroup.WATER_AMBIENT).setDimensions(0.5f, 0.3f).maxTrackingRange(8).build("angling:sea_slug"));
    public static final net.minecraftforge.registries.RegistryObject<EntityType<CrabEntity>> CRAB = ENTITIES.register("crab", () -> EntityType.Builder.create(CrabEntity::new, SpawnGroup.CREATURE).setDimensions(0.7f, 0.4f).maxTrackingRange(8).build("angling:crab"));
    public static final net.minecraftforge.registries.RegistryObject<EntityType<DongfishEntity>> DONGFISH = ENTITIES.register("dongfish", () -> EntityType.Builder.create(DongfishEntity::new, SpawnGroup.WATER_AMBIENT).setDimensions(0.4f, 0.3f).maxTrackingRange(8).build("angling:dongfish"));
    public static final net.minecraftforge.registries.RegistryObject<EntityType<CatfishEntity>> CATFISH = ENTITIES.register("catfish", () -> EntityType.Builder.create(CatfishEntity::new, SpawnGroup.WATER_AMBIENT).setDimensions(0.6f, 0.4f).maxTrackingRange(8).build("angling:catfish"));
    public static final net.minecraftforge.registries.RegistryObject<EntityType<SeahorseEntity>> SEAHORSE = ENTITIES.register("seahorse", () -> EntityType.Builder.create(SeahorseEntity::new, SpawnGroup.WATER_AMBIENT).setDimensions(0.35f, 0.6f).maxTrackingRange(8).build("angling:seahorse"));
    public static final net.minecraftforge.registries.RegistryObject<EntityType<BubbleEyeEntity>> BUBBLE_EYE = ENTITIES.register("bubble_eye", () -> EntityType.Builder.create(BubbleEyeEntity::new, SpawnGroup.WATER_AMBIENT).setDimensions(0.4f, 0.3f).maxTrackingRange(8).build("angling:bubble_eye"));
    public static final net.minecraftforge.registries.RegistryObject<EntityType<AnomalocarisEntity>> ANOMALOCARIS = ENTITIES.register("anomalocaris", () -> EntityType.Builder.create(AnomalocarisEntity::new, SpawnGroup.UNDERGROUND_WATER_CREATURE).setDimensions(0.8f, 0.3f).maxTrackingRange(8).build("angling:anomalocaris"));
    public static final net.minecraftforge.registries.RegistryObject<EntityType<AnglerfishEntity>> ANGLERFISH = ENTITIES.register("anglerfish", () -> EntityType.Builder.create(AnglerfishEntity::new, SpawnGroup.WATER_AMBIENT).setDimensions(0.8f, 0.5f).maxTrackingRange(8).build("angling:anglerfish"));
    public static final net.minecraftforge.registries.RegistryObject<EntityType<MahiMahiEntity>> MAHI_MAHI = ENTITIES.register("mahi_mahi", () -> EntityType.Builder.create(MahiMahiEntity::new, SpawnGroup.WATER_AMBIENT).setDimensions(1f, 0.8f).maxTrackingRange(8).build("angling:mahi_mahi"));

    private static Identifier id(String name) { return new Identifier(AnglingMod.MOD_ID, name); }
    public static void init(IEventBus bus) {
        ENTITIES.register(bus);
        BLOCK_ENTITIES.register(bus);
        bus.addListener(AnglingEntities::attributes);
        bus.addListener(AnglingEntities::spawnPlacements);
    }
    private static void attributes(EntityAttributeCreationEvent event) {
        event.put(FRY.get(), FryEntity.createAttributes().build());
        event.put(SUNFISH.get(), FishEntity.createFishAttributes().build());
        event.put(PELICAN.get(), PelicanEntity.createAttributes().build());
        event.put(NAUTILUS.get(), FishEntity.createFishAttributes().build());
        event.put(SEA_SLUG.get(), SeaSlugEntity.createAttributes().build());
        event.put(CRAB.get(), CrabEntity.createAttributes().build());
        event.put(DONGFISH.get(), FishEntity.createFishAttributes().build());
        event.put(CATFISH.get(), FishEntity.createFishAttributes().build());
        event.put(SEAHORSE.get(), FishEntity.createFishAttributes().build());
        event.put(BUBBLE_EYE.get(), FishEntity.createFishAttributes().build());
        event.put(ANOMALOCARIS.get(), FishEntity.createFishAttributes().build());
        event.put(ANGLERFISH.get(), FishEntity.createFishAttributes().build());
        event.put(MAHI_MAHI.get(), MahiMahiEntity.createAttributes().build());
    }
    private static void spawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(FRY.get(), SpawnRestriction.Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, WaterCreatureEntity::canSpawn, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(SUNFISH.get(), SpawnRestriction.Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, WaterCreatureEntity::canSpawn, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(PELICAN.get(), SpawnRestriction.Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::canMobSpawn, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(NAUTILUS.get(), SpawnRestriction.Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, NautilusEntity::canSpawn, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(SEA_SLUG.get(), SpawnRestriction.Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, WaterCreatureEntity::canSpawn, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(CRAB.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, CrabEntity::canSpawn, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(DONGFISH.get(), SpawnRestriction.Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, WaterCreatureEntity::canSpawn, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(CATFISH.get(), SpawnRestriction.Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, WaterCreatureEntity::canSpawn, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(SEAHORSE.get(), SpawnRestriction.Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, WaterCreatureEntity::canSpawn, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(BUBBLE_EYE.get(), SpawnRestriction.Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, WaterCreatureEntity::canSpawn, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ANOMALOCARIS.get(), SpawnRestriction.Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnomalocarisEntity::canSpawn, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ANGLERFISH.get(), SpawnRestriction.Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, NautilusEntity::canSpawn, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(MAHI_MAHI.get(), SpawnRestriction.Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, FishEntity::canSpawn, SpawnPlacementRegisterEvent.Operation.REPLACE);
    }
}
