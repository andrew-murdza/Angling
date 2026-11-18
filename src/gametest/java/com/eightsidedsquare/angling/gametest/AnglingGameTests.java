package com.eightsidedsquare.angling.gametest;

import com.eightsidedsquare.angling.cca.AnglingEntityComponents;
import com.eightsidedsquare.angling.cca.FishSpawningComponent;
import com.eightsidedsquare.angling.common.block.AlgaeBlock;
import com.eightsidedsquare.angling.common.block.FilterFeeder;
import com.eightsidedsquare.angling.common.block.WormyBlock;
import com.eightsidedsquare.angling.common.entity.*;
import com.eightsidedsquare.angling.common.entity.util.*;
import com.eightsidedsquare.angling.core.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MultifaceGrowthBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Bucketable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.IntProperty;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Runtime regression tests. This source set and its structures must not enter the release jar. */
@GameTestHolder("angling")
@PrefixGameTestTemplate(false)
public final class AnglingGameTests {
    private static final List<EntityType<?>> TYPES = List.of(
            AnglingEntities.FRY.get(), AnglingEntities.SUNFISH.get(), AnglingEntities.PELICAN.get(),
            AnglingEntities.NAUTILUS.get(), AnglingEntities.SEA_SLUG.get(), AnglingEntities.CRAB.get(),
            AnglingEntities.DONGFISH.get(), AnglingEntities.CATFISH.get(), AnglingEntities.SEAHORSE.get(),
            AnglingEntities.BUBBLE_EYE.get(), AnglingEntities.ANOMALOCARIS.get(),
            AnglingEntities.ANGLERFISH.get(), AnglingEntities.MAHI_MAHI.get());

    @GameTest(templateName = "empty")
    public static void everyEntitySurvivesSaveReload(TestContext context) {
        for (EntityType<?> type : TYPES) {
            Entity entity = type.create(context.getWorld());
            context.assertTrue(entity instanceof LivingEntity, "Entity factory failed: " + EntityType.getId(type));
            entity.setCustomName(Text.literal("Angling persistence test"));
            ((LivingEntity) entity).setHealth(1.5f);
            setDistinctiveVariant(entity);
            NbtCompound saved = entity.writeNbt(new NbtCompound());
            Entity restored = type.create(context.getWorld());
            context.assertTrue(restored != null, "Restore factory failed: " + EntityType.getId(type));
            restored.readNbt(saved);
            context.assertTrue(entity.getCustomName().equals(restored.getCustomName()), "Custom name lost: " + EntityType.getId(type));
            context.assertTrue(((LivingEntity) restored).getHealth() == 1.5f, "Health lost: " + EntityType.getId(type));
            assertDistinctiveVariant(context, restored);
        }
        context.complete();
    }

    @GameTest(templateName = "empty")
    public static void everyBucketPreservesEntityData(TestContext context) {
        int buckets = 0;
        for (EntityType<?> type : TYPES) {
            Entity entity = type.create(context.getWorld());
            if (!(entity instanceof Bucketable bucketable)) continue;
            buckets++;
            ((LivingEntity) entity).setHealth(1.5f);
            setDistinctiveVariant(entity);
            if (entity instanceof FishEntity fish) populateBreedingState(AnglingEntityComponents.FISH_SPAWNING.get(fish));
            ItemStack bucket = bucketable.getBucketItem();
            context.assertTrue(!bucket.isEmpty(), "No bucket item for " + EntityType.getId(type));
            bucketable.copyDataToStack(bucket);
            NbtCompound saved = bucket.getOrCreateNbt();
            Entity restored = type.create(context.getWorld());
            context.assertTrue(restored instanceof Bucketable, "Restored mob cannot be bucketed");
            ((MobEntity) restored).initialize(context.getWorld(), context.getWorld().getLocalDifficulty(context.getAbsolutePos(BlockPos.ORIGIN)), SpawnReason.BUCKET, null, saved);
            ((Bucketable) restored).copyDataFromNbt(saved);
            ((Bucketable) restored).setFromBucket(true);
            context.assertTrue(((Bucketable) restored).isFromBucket(), "Bucket origin flag lost");
            context.assertTrue(((LivingEntity) restored).getHealth() == 1.5f, "Bucket health lost: " + EntityType.getId(type));
            assertDistinctiveVariant(context, restored);
            if (restored instanceof FishEntity fish) assertBreedingState(context, AnglingEntityComponents.FISH_SPAWNING.get(fish));
        }
        context.assertTrue(buckets == 12, "Expected all 12 bucketable entity types, got " + buckets);
        context.complete();
    }

    @GameTest(templateName = "empty")
    public static void fishStatePersistsAndMigrates(TestContext context) {
        for (EntityType<?> type : List.of(EntityType.COD, EntityType.SALMON, EntityType.PUFFERFISH, EntityType.TROPICAL_FISH, EntityType.TADPOLE, AnglingEntities.FRY.get(), AnglingEntities.SUNFISH.get())) {
            Entity original = type.create(context.getWorld());
            FishSpawningComponent state = AnglingEntityComponents.FISH_SPAWNING.get(original);
            state.readFromNbt(new NbtCompound());
            context.assertTrue(state.canGrowUp(), "Missing old NBT must default CanGrowUp to true");
            populateBreedingState(state);
            NbtCompound saved = original.writeNbt(new NbtCompound());
            Entity restored = type.create(context.getWorld());
            restored.readNbt(saved);
            assertBreedingState(context, AnglingEntityComponents.FISH_SPAWNING.get(restored));
            NbtCompound legacyComponents = new NbtCompound();
            legacyComponents.put(FishSpawningComponent.NBT_KEY, saved.getCompound(FishSpawningComponent.NBT_KEY).copy());
            saved.remove(FishSpawningComponent.NBT_KEY);
            saved.put("cardinal_components", legacyComponents);
            Entity migrated = type.create(context.getWorld());
            migrated.readNbt(saved);
            assertBreedingState(context, AnglingEntityComponents.FISH_SPAWNING.get(migrated));
        }
        context.complete();
    }

    @GameTest(templateName = "empty")
    public static void growthLockedTadpoleSurvivesBucketRelease(TestContext context) {
        var tadpole = EntityType.TADPOLE.create(context.getWorld());
        populateBreedingState(AnglingEntityComponents.FISH_SPAWNING.get(tadpole));
        ItemStack bucket = tadpole.getBucketItem();
        tadpole.copyDataToStack(bucket);
        NbtCompound saved = bucket.getOrCreateNbt();
        saved.putInt("Age", 24000);
        var released = EntityType.TADPOLE.create(context.getWorld());
        released.copyDataFromNbt(saved);
        context.assertTrue(!released.isRemoved(), "Loading a mature, growth-locked tadpole must not turn it into a frog");
        assertBreedingState(context, AnglingEntityComponents.FISH_SPAWNING.get(released));
        context.complete();
    }

    @GameTest(templateName = "empty")
    public static void roeItemDataHatchesCorrectSpecies(TestContext context) {
        BlockPos local = new BlockPos(3, 2, 3);
        BlockPos pos = context.getAbsolutePos(local);
        context.setBlockState(local.down(), Blocks.STONE);
        BlockState state = AnglingBlocks.ROE.get().getDefaultState().with(Properties.WATERLOGGED, true);
        context.setBlockState(local, state);
        RoeBlockEntity roe = (RoeBlockEntity) context.getBlockEntity(local);
        NbtCompound parent = new NbtCompound();
        parent.putString("Variant", "angling:bluegill");
        roe.setEntityType(AnglingEntities.SUNFISH.get());
        roe.setColors(0x123456, 0xabcdef);
        roe.setParentsData(parent, parent.copy());
        ItemStack stack = new ItemStack(AnglingItems.ROE.get());
        BlockItem.setBlockEntityNbt(stack, AnglingEntities.ROE.get(), roe.createNbt());
        roe.setEntityType(EntityType.COD);
        AnglingBlocks.ROE.get().onPlaced(context.getWorld(), pos, state, null, stack);
        context.assertTrue(roe.getEntityType().orElseThrow() == AnglingEntities.SUNFISH.get(), "Roe item lost the parent species on server placement");
        context.assertTrue(roe.createNbt().getInt("PrimaryColor") == 0x123456, "Roe item lost its colors");
        AnglingBlocks.ROE.get().scheduledTick(state, context.getWorld(), pos, context.getWorld().getRandom());
        context.expectBlock(Blocks.WATER, local);
        // Chunk entity indexes may become ready asynchronously after the hatch.
        context.addInstantFinalTask(() -> {
            List<FryEntity> fry = context.getWorld().getEntitiesByClass(FryEntity.class, new Box(pos).expand(2), e -> true);
            context.assertTrue(fry.size() >= 2 && fry.size() <= 5, "Roe must hatch 2-5 fry; got " + fry.size());
            for (FryEntity child : fry) {
                context.assertTrue(child.getGrowUpTo().equals("angling:sunfish"), "Fry target species changed");
                context.assertTrue(child.getVariant().getString("Variant").equals("angling:bluegill"), "Sunfish variant inheritance failed");
            }
            context.expectBlock(Blocks.WATER, local);
        });
    }

    @GameTest(templateName = "empty")
    public static void seaSlugEggItemDataHatches(TestContext context) {
        BlockPos local = new BlockPos(3, 2, 3);
        BlockPos pos = context.getAbsolutePos(local);
        context.setBlockState(local.down(), Blocks.STONE);
        BlockState state = AnglingBlocks.SEA_SLUG_EGGS.get().getDefaultState().with(Properties.WATERLOGGED, true);
        context.setBlockState(local, state);
        SeaSlugEggsBlockEntity eggs = (SeaSlugEggsBlockEntity) context.getBlockEntity(local);
        NbtCompound parent = new NbtCompound();
        parent.putString("BaseColor", "angling:jade");
        parent.putString("PatternColor", "angling:ivory");
        parent.putString("Pattern", "angling:stripes");
        parent.putBoolean("Bioluminescent", true);
        eggs.setParentsData(parent, parent.copy());
        eggs.setColor(0x123456);
        ItemStack stack = new ItemStack(AnglingItems.SEA_SLUG_EGGS.get());
        BlockItem.setBlockEntityNbt(stack, AnglingEntities.SEA_SLUG_EGGS.get(), eggs.createNbt());
        eggs.setColor(0);
        AnglingBlocks.SEA_SLUG_EGGS.get().onPlaced(context.getWorld(), pos, state, null, stack);
        context.assertTrue(eggs.createNbt().getInt("Color") == 0x123456, "Egg item lost its color on server placement");
        List<SeaSlugEntity> spawned = new ArrayList<>();
        Consumer<EntityJoinLevelEvent> capture = event -> {
            if (event.getEntity() instanceof SeaSlugEntity slug && event.getLevel() == context.getWorld()) spawned.add(slug);
        };
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, EntityJoinLevelEvent.class, capture);
        try {
            AnglingBlocks.SEA_SLUG_EGGS.get().scheduledTick(state, context.getWorld(), pos, context.getWorld().getRandom());
        } finally {
            MinecraftForge.EVENT_BUS.unregister(capture);
        }
        context.expectBlock(Blocks.WATER, local);
        context.assertTrue(spawned.size() >= 1 && spawned.size() <= 3, "Hatch must add 1-3 sea slugs to the world; events=" + spawned.size());
        for (SeaSlugEntity slug : spawned) {
            context.assertTrue(new Box(pos).expand(1).intersects(slug.getBoundingBox()), "Hatched slug bounding box must move with its position");
        }
        context.addInstantFinalTask(() -> {
            List<SeaSlugEntity> slugs = context.getWorld().getEntitiesByClass(SeaSlugEntity.class, new Box(pos).expand(2), e -> true);
            context.assertTrue(slugs.size() >= 1 && slugs.size() <= 3, "Eggs must hatch 1-3 sea slugs; got " + slugs.size());
            for (SeaSlugEntity slug : slugs) {
                context.assertTrue(slug.isBioluminescent(), "Bioluminescent inheritance failed");
                context.assertTrue(slug.getBaseColor() != null && slug.getPatternColor() != null && slug.getPattern() != null, "Child has an invalid variant");
            }
            context.expectBlock(Blocks.WATER, local);
        });
    }

    @GameTest(templateName = "empty")
    public static void worldgenAndVariantTagsLoad(TestContext context) {
        for (String name : List.of("put_worm_in_block", "obtain_roe", "obtain_sea_slug_eggs", "look_at_nautilus", "traded_with_pelican")) {
            context.assertTrue(context.getWorld().getServer().getAdvancementLoader().get(new Identifier("angling", "husbandry/" + name)) != null,
                    "Advancement failed loading: " + name);
        }
        for (String name : List.of("paper_from_papyrus", "fried_sunfish", "fried_sunfish_from_smoking", "fried_sunfish_from_campfire_cooking")) {
            context.assertTrue(context.getWorld().getRecipeManager().get(new Identifier("angling", name)).isPresent(), "Recipe failed loading: " + name);
        }
        for (String name : List.of("oyster_reef", "clams", "wormy_block", "patch_sargassum", "patch_duckweed", "patch_papyrus")) {
            Identifier id = new Identifier("angling", name);
            context.assertTrue(context.getWorld().getRegistryManager().get(RegistryKeys.CONFIGURED_FEATURE).get(id) != null, "Configured feature failed decoding: " + name);
            context.assertTrue(context.getWorld().getRegistryManager().get(RegistryKeys.PLACED_FEATURE).get(id) != null, "Placed feature failed decoding: " + name);
        }
        for (String[] pair : List.of(new String[]{"oyster_reef", "ocean"}, new String[]{"clams", "ocean"},
                new String[]{"wormy_block", "plains"}, new String[]{"patch_duckweed", "swamp"},
                new String[]{"patch_sargassum", "lukewarm_ocean"}, new String[]{"patch_papyrus", "savanna"})) {
            var biome = context.getWorld().getRegistryManager().get(RegistryKeys.BIOME).get(new Identifier("minecraft", pair[1]));
            var feature = context.getWorld().getRegistryManager().get(RegistryKeys.PLACED_FEATURE).get(new Identifier("angling", pair[0]));
            context.assertTrue(biome.getGenerationSettings().getFeatures().stream().flatMap(entries -> entries.stream()).anyMatch(entry -> entry.value() == feature),
                    "Forge biome modifier did not add " + pair[0] + " to " + pair[1]);
        }
        context.assertTrue(AnglingUtil.getRandomTagValue(context.getWorld(), SunfishVariant.Tag.NATURAL_SUNFISH, context.getWorld().getRandom()) != null, "Sunfish tags unavailable");
        context.assertTrue(AnglingUtil.getRandomTagValue(context.getWorld(), CrabVariant.Tag.NATURAL_VARIANTS, context.getWorld().getRandom()) != null, "Crab tags unavailable");
        context.assertTrue(AnglingUtil.getRandomTagValue(context.getWorld(), SeaSlugPattern.Tag.NATURAL_PATTERNS, context.getWorld().getRandom()) != null, "Slug pattern tags unavailable");
        context.assertTrue(AnglingUtil.getRandomTagValue(context.getWorld(), SeaSlugColor.Tag.BASE_COLORS, context.getWorld().getRandom()) != null, "Slug color tags unavailable");
        context.complete();
    }

    @GameTest(templateName = "empty")
    public static void allBlockEntitiesSaveReload(TestContext context) {
        BlockPos local = new BlockPos(3, 2, 3);
        context.setBlockState(local.down(), Blocks.STONE);
        for (Block block : List.of(AnglingBlocks.ROE.get(), AnglingBlocks.SEA_SLUG_EGGS.get(), AnglingBlocks.STARFISH.get(), AnglingBlocks.DEAD_STARFISH.get(), AnglingBlocks.ANEMONE.get(), AnglingBlocks.URCHIN.get())) {
            context.setBlockState(local, block.getDefaultState());
            BlockEntity entity = context.getBlockEntity(local);
            context.assertTrue(entity != null, "Missing block entity: " + Registries.BLOCK.getId(block));
            if (entity instanceof StarfishBlockEntity starfish) {
                starfish.setColor(0x123456);
                starfish.setRainbow(true);
                starfish.setRandomRotation(47.5);
            }
            if (entity instanceof UrchinBlockEntity urchin) {
                ItemStack hat = new ItemStack(Items.CARVED_PUMPKIN);
                hat.setCustomName(Text.literal("Urchin test hat"));
                urchin.setHat(hat);
            }
            NbtCompound saved = entity.createNbt();
            entity.readNbt(saved.copy());
            context.assertTrue(saved.equals(entity.createNbt()), "Block entity data changed after load: " + Registries.BLOCK.getId(block));
            if (entity instanceof StarfishBlockEntity starfish) {
                ItemStack stack = new ItemStack(block.asItem());
                BlockItem.setBlockEntityNbt(stack, AnglingEntities.STARFISH.get(), saved);
                starfish.setRainbow(false);
                starfish.setRandomRotation(0);
                block.onPlaced(context.getWorld(), context.getAbsolutePos(local), block.getDefaultState(), null, stack);
                context.assertTrue(starfish.isRainbow() && starfish.getRandomRotation() == 47.5 && starfish.getColor() == 0x123456, "Starfish item placement lost appearance");
            }
        }
        context.complete();
    }

    @GameTest(templateName = "empty")
    public static void fishFeedingRespectsLoveCooldownAndCreative(TestContext context) {
        var player = context.createMockSurvivalPlayer();
        for (EntityType<? extends FishEntity> type : List.of(EntityType.COD, AnglingEntities.SUNFISH.get())) {
            FishEntity fish = type.create(context.getWorld());
            FishSpawningComponent state = AnglingEntityComponents.FISH_SPAWNING.get(fish);
            ItemStack worms = new ItemStack(AnglingItems.WORM.get(), 3);
            player.setStackInHand(Hand.MAIN_HAND, worms);
            player.getAbilities().creativeMode = false;
            fish.interact(player, Hand.MAIN_HAND);
            context.assertTrue(state.isInLove() && state.wasFed() && worms.getCount() == 2, "Survival feeding must start breeding and consume one worm");
            fish.interact(player, Hand.MAIN_HAND);
            context.assertTrue(worms.getCount() == 2, "Already-in-love fish consumed another worm");
            state.setLoveTicks(0);
            state.setLoveCooldown(100);
            fish.interact(player, Hand.MAIN_HAND);
            context.assertTrue(!state.isInLove() && worms.getCount() == 2, "Breeding cooldown was bypassed");
            state.setLoveCooldown(0);
            player.getAbilities().creativeMode = true;
            fish.interact(player, Hand.MAIN_HAND);
            context.assertTrue(state.isInLove() && worms.getCount() == 2, "Creative feeding should start breeding without consuming worms");
        }
        player.getAbilities().creativeMode = false;
        var nautilus = AnglingEntities.NAUTILUS.get().create(context.getWorld());
        ItemStack worms = new ItemStack(AnglingItems.WORM.get(), 2);
        player.setStackInHand(Hand.MAIN_HAND, worms);
        nautilus.interact(player, Hand.MAIN_HAND);
        context.assertTrue(!AnglingEntityComponents.FISH_SPAWNING.get(nautilus).isInLove() && worms.getCount() == 2, "Species excluded from spawning_fish must not consume breeding worms");
        context.complete();
    }

    @GameTest(templateName = "empty")
    public static void wormBlocksConsumeCapAndReturnWorms(TestContext context) {
        BlockPos local = new BlockPos(3, 2, 3);
        BlockPos pos = context.getAbsolutePos(local);
        BlockHitResult hit = new BlockHitResult(Vec3d.ofCenter(pos), Direction.UP, pos, false);
        for (Block base : List.of(Blocks.DIRT, Blocks.MUD)) {
            Block wormy = base == Blocks.DIRT ? AnglingBlocks.WORMY_DIRT.get() : AnglingBlocks.WORMY_MUD.get();
            var player = context.createMockSurvivalPlayer();
            ItemStack worms = new ItemStack(AnglingItems.WORM.get(), 3);
            player.setStackInHand(Hand.MAIN_HAND, worms);
            context.setBlockState(local, base);
            worms.getItem().useOnBlock(new ItemUsageContext(player, Hand.MAIN_HAND, hit));
            context.assertTrue(context.getBlockState(local).isOf(wormy) && worms.getCount() == 2, "First worm insertion failed or consumed the wrong count");
            for (int count = 2; count <= 3; count++) {
                context.getBlockState(local).onUse(context.getWorld(), player, Hand.MAIN_HAND, hit);
                context.assertTrue(context.getBlockState(local).get(WormyBlock.WORMS) == count && worms.getCount() == 3 - count, "Worm count/item count did not advance together");
            }
            player.setStackInHand(Hand.MAIN_HAND, new ItemStack(AnglingItems.WORM.get()));
            context.getBlockState(local).onUse(context.getWorld(), player, Hand.MAIN_HAND, hit);
            context.assertTrue(context.getBlockState(local).get(WormyBlock.WORMS) == 3 && player.getMainHandStack().getCount() == 1, "Full worm block consumed another worm");
            player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
            context.getBlockState(local).onUse(context.getWorld(), player, Hand.MAIN_HAND, hit);
            context.assertTrue(context.getBlockState(local).get(WormyBlock.WORMS) == 2 && player.getInventory().count(AnglingItems.WORM.get()) == 1, "Worm harvesting must return exactly one item");
            var emptyPlayer = context.createMockSurvivalPlayer();
            context.setBlockState(local, wormy.getDefaultState());
            context.getBlockState(local).onUse(context.getWorld(), emptyPlayer, Hand.MAIN_HAND, hit);
            context.assertTrue(context.getBlockState(local).isOf(base) && emptyPlayer.getInventory().count(AnglingItems.WORM.get()) == 1, "Removing the final worm must restore the original substrate");
            var creative = context.createMockCreativePlayer();
            creative.getAbilities().creativeMode = true;
            ItemStack creativeWorms = new ItemStack(AnglingItems.WORM.get(), 3);
            creative.setStackInHand(Hand.MAIN_HAND, creativeWorms);
            creativeWorms.getItem().useOnBlock(new ItemUsageContext(creative, Hand.MAIN_HAND, hit));
            context.getBlockState(local).onUse(context.getWorld(), creative, Hand.MAIN_HAND, hit);
            context.assertTrue(context.getBlockState(local).get(WormyBlock.WORMS) == 2 && creativeWorms.getCount() == 3, "Creative insertion must not consume worms");
        }
        context.complete();
    }

    @GameTest(templateName = "empty")
    public static void algaeFeedsOystersAndPreservesWater(TestContext context) {
        BlockPos local = new BlockPos(3, 2, 3);
        BlockPos pos = context.getAbsolutePos(local);
        BlockPos oysterPos = pos.east();
        context.setBlockState(local.down(), Blocks.STONE);
        context.setBlockState(local.north(), Blocks.STONE);
        context.setBlockState(local.east().down(), Blocks.STONE);
        BlockState algae = AnglingBlocks.ALGAE.get().getDefaultState()
                .with(MultifaceGrowthBlock.getProperty(Direction.DOWN), true)
                .with(MultifaceGrowthBlock.getProperty(Direction.NORTH), true);
        context.setBlockState(local, algae);
        context.setBlockState(local.east(), AnglingBlocks.OYSTERS.get().getDefaultState());
        IntProperty fed = (IntProperty) AnglingBlocks.OYSTERS.get().getStateManager().getProperty("times_fed");
        for (int feed = 1; feed <= 2; feed++) {
            // First sampled block is the adjacent oyster; remaining attempts sample air overhead.
            CheckedRandom samples = new CheckedRandom(1L) {
                private int calls;
                @Override public double nextGaussian() {
                    int call = calls++;
                    return call < 3 ? (call == 0 ? 1d / 6d : 0d) : (call % 3 == 1 ? 4d : 0d);
                }
            };
            AnglingBlocks.ALGAE.get().randomTick(context.getWorld().getBlockState(pos), context.getWorld(), pos, samples);
            context.assertTrue(context.getWorld().getBlockState(oysterPos).get(fed) == feed, "Algae did not feed the nearby oyster exactly once");
            if (feed == 1) context.assertTrue(MultifaceGrowthBlock.collectDirections(context.getBlockState(local)).size() == 1, "Feeding must consume one algae face");
        }
        context.expectBlock(Blocks.WATER, local);
        for (Direction direction : Direction.Type.HORIZONTAL) {
            context.getWorld().setBlockState(oysterPos.offset(direction).down(), Blocks.STONE.getDefaultState());
            context.getWorld().setBlockState(oysterPos.offset(direction), Blocks.WATER.getDefaultState());
        }
        for (int feed = 0; feed < 3; feed++) {
            ((FilterFeeder) AnglingBlocks.OYSTERS.get()).onFeed(oysterPos, context.getWorld().getBlockState(oysterPos), context.getWorld());
        }
        long children = Direction.stream().filter(d -> d.getAxis().isHorizontal())
                .filter(d -> context.getWorld().getBlockState(oysterPos.offset(d)).isOf(AnglingBlocks.OYSTERS.get())).count();
        context.assertTrue(children == 1 && context.getWorld().getBlockState(oysterPos).get(fed) == 0, "Five meals must reproduce one oyster and reset feeding progress");
        context.complete();
    }

    @GameTest(templateName = "empty")
    public static void pelicanTradesBucketAndPreservesFishVariant(TestContext context) {
        var player = context.createMockSurvivalPlayer();
        var pelican = AnglingEntities.PELICAN.get().create(context.getWorld());
        BlockPos pos = context.getAbsolutePos(new BlockPos(3, 2, 3));
        pelican.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        pelican.setEntityInBeak(EntityType.COD.create(context.getWorld()));
        pelican.setBeakOpen(true);
        var fish = AnglingEntities.SUNFISH.get().create(context.getWorld());
        fish.setVariant(SunfishVariant.BLUEGILL);
        ItemStack offer = fish.getBucketItem();
        fish.copyDataToStack(offer);
        player.setStackInHand(Hand.MAIN_HAND, offer);
        pelican.interactMob(player, Hand.MAIN_HAND);
        context.assertTrue(player.getMainHandStack().isOf(Items.WATER_BUCKET), "Survival trade must return a water bucket");
        context.assertTrue(pelican.getEntityInBeak().orElseThrow() instanceof SunfishEntity held && held.getVariant() == SunfishVariant.BLUEGILL, "Pelican trade lost bucket species/variant");
        var creative = context.createMockCreativePlayer();
        creative.getAbilities().creativeMode = true;
        creative.setStackInHand(Hand.MAIN_HAND, offer.copy());
        var otherPelican = AnglingEntities.PELICAN.get().create(context.getWorld());
        otherPelican.setBeakOpen(true);
        otherPelican.interactMob(creative, Hand.MAIN_HAND);
        context.assertTrue(creative.getMainHandStack().isOf(AnglingItems.SUNFISH_BUCKET.get()), "Creative trade consumed the offered bucket");
        context.addInstantFinalTask(() -> {
            context.assertTrue(context.getWorld().getEntitiesByClass(net.minecraft.entity.passive.CodEntity.class, new Box(pos).expand(3), e -> true).size() == 1, "Pelican did not release exactly one previous catch");
        });
    }

    @GameTest(templateName = "empty")
    public static void fryFeedingGrowthLockAndAdultTransformation(TestContext context) {
        var player = context.createMockSurvivalPlayer();
        var locked = AnglingEntities.FRY.get().create(context.getWorld());
        locked.setAge(-1000);
        ItemStack worms = new ItemStack(AnglingItems.WORM.get(), 2);
        player.setStackInHand(Hand.MAIN_HAND, worms);
        locked.interact(player, Hand.MAIN_HAND);
        context.assertTrue(locked.getAge() == -950 && worms.getCount() == 1, "A worm must consume one item and advance fry growth by five percent");
        ItemStack eyes = new ItemStack(Items.FERMENTED_SPIDER_EYE, 2);
        player.setStackInHand(Hand.MAIN_HAND, eyes);
        locked.interact(player, Hand.MAIN_HAND);
        context.assertTrue(!AnglingEntityComponents.FISH_SPAWNING.get(locked).canGrowUp() && eyes.getCount() == 1, "Fermented spider eye must lock growth and consume one item");
        locked.setAge(0);
        locked.growUp();
        context.assertTrue(!locked.isRemoved(), "Growth-locked fry must not transform");

        BlockPos pos = context.getAbsolutePos(new BlockPos(3, 2, 3));
        var growing = AnglingEntities.FRY.get().create(context.getWorld());
        growing.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        setDistinctiveVariant(growing);
        growing.setAge(0);
        growing.growUp();
        context.assertTrue(growing.isRemoved(), "Adult transformation must remove the fry");
        context.addInstantFinalTask(() -> {
            List<SunfishEntity> adults = context.getWorld().getEntitiesByClass(SunfishEntity.class, new Box(pos).expand(2), e -> true);
            context.assertTrue(adults.size() == 1, "Fry must create exactly one adult at its position");
            context.assertTrue(adults.get(0).getVariant() == SunfishVariant.BLUEGILL && adults.get(0).isPersistent(), "Adult must inherit the fry variant and stay persistent");
        });
    }

    private static void populateBreedingState(FishSpawningComponent state) {
        state.setLoveTicks(123);
        state.setLoveCooldown(456);
        state.setCarryingRoe(true);
        state.setCanGrowUp(false);
        state.setWasFed(true);
        NbtCompound mate = new NbtCompound();
        mate.putString("Variant", "angling:bluegill");
        state.setMateData(mate);
    }

    private static void assertBreedingState(TestContext context, FishSpawningComponent state) {
        NbtCompound saved = new NbtCompound();
        state.writeToNbt(saved);
        context.assertTrue(saved.getInt("LoveTicks") == 123 && saved.getInt("LoveCooldown") == 456, "Breeding timers changed");
        context.assertTrue(state.isCarryingRoe() && !state.canGrowUp() && state.wasFed(), "Breeding flags changed");
        context.assertTrue(state.getMateData() != null && state.getMateData().getString("Variant").equals("angling:bluegill"), "Mate data changed");
    }

    private static void setDistinctiveVariant(Entity entity) {
        if (entity instanceof SunfishEntity fish) fish.setVariant(SunfishVariant.BLUEGILL);
        if (entity instanceof CrabEntity crab) crab.setVariant(CrabVariant.GHOST);
        if (entity instanceof DongfishEntity fish) fish.setHasHorngus(false);
        if (entity instanceof SeaSlugEntity slug) {
            slug.setBaseColor(SeaSlugColor.JADE);
            slug.setPatternColor(SeaSlugColor.IVORY);
            slug.setPattern(SeaSlugPattern.STRIPES);
            slug.setBioluminescent(true);
        }
        if (entity instanceof FryEntity fry) {
            fry.setGrowUpTo("angling:sunfish");
            NbtCompound variant = new NbtCompound();
            variant.putString("Variant", "angling:bluegill");
            fry.setVariant(variant);
        }
    }

    private static void assertDistinctiveVariant(TestContext context, Entity entity) {
        if (entity instanceof SunfishEntity fish) context.assertTrue(fish.getVariant() == SunfishVariant.BLUEGILL, "Sunfish variant lost");
        if (entity instanceof CrabEntity crab) context.assertTrue(crab.getVariant() == CrabVariant.GHOST, "Crab variant lost");
        if (entity instanceof DongfishEntity fish) context.assertTrue(!fish.hasHorngus(), "Dongfish horn regrew after load");
        if (entity instanceof SeaSlugEntity slug) context.assertTrue(slug.getBaseColor() == SeaSlugColor.JADE && slug.getPatternColor() == SeaSlugColor.IVORY && slug.getPattern() == SeaSlugPattern.STRIPES && slug.isBioluminescent(), "Sea slug appearance changed");
        if (entity instanceof FryEntity fry) context.assertTrue(fry.getGrowUpTo().equals("angling:sunfish") && fry.getVariant().getString("Variant").equals("angling:bluegill"), "Fry inherited data lost");
    }
}
