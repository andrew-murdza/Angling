package com.eightsidedsquare.angling.core;

import com.eightsidedsquare.angling.common.block.*;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.Registries;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.eightsidedsquare.angling.core.AnglingMod.MOD_ID;

public class AnglingBlocks {
    private static final net.minecraftforge.registries.DeferredRegister<Block> BLOCKS = net.minecraftforge.registries.DeferredRegister.create(RegistryKeys.BLOCK, MOD_ID);
    private static final net.minecraftforge.registries.DeferredRegister<Item> ITEMS = net.minecraftforge.registries.DeferredRegister.create(RegistryKeys.ITEM, MOD_ID);

    public static final net.minecraftforge.registries.RegistryObject<Block> ROE = create("roe", () -> new RoeBlock(AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).noCollision().nonOpaque().breakInstantly().sounds(BlockSoundGroup.FROGSPAWN)), false);
    public static final net.minecraftforge.registries.RegistryObject<Block> SEA_SLUG_EGGS = create("sea_slug_eggs", () -> new SeaSlugEggsBlock(AbstractBlock.Settings.copy(ROE.get()).offset(AbstractBlock.OffsetType.XZ).dynamicBounds()), false);
    public static final net.minecraftforge.registries.RegistryObject<Block> DUCKWEED = create("duckweed", () -> new WaterFloatingPlant(AbstractBlock.Settings.create().mapColor(MapColor.LIME).replaceable().breakInstantly().nonOpaque().noCollision().sounds(BlockSoundGroup.WET_GRASS)), false);
    public static final net.minecraftforge.registries.RegistryObject<Block> ALGAE = create("algae", () -> new AlgaeBlock(AbstractBlock.Settings.create().mapColor(MapColor.GREEN).replaceable().sounds(BlockSoundGroup.FROGSPAWN).noCollision().nonOpaque().strength(0.1f).ticksRandomly()), true);
    public static final net.minecraftforge.registries.RegistryObject<Block> WORMY_DIRT = create("wormy_dirt", () -> new WormyDirtBlock(AbstractBlock.Settings.copy(Blocks.DIRT)), true);
    public static final net.minecraftforge.registries.RegistryObject<Block> WORMY_MUD = create("wormy_mud", () -> new WormyMudBlock(AbstractBlock.Settings.copy(Blocks.MUD)), true);
    public static final net.minecraftforge.registries.RegistryObject<Block> OYSTERS = create("oysters", () -> new OystersBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(0.5f).nonOpaque().sounds(AnglingSounds.SHELL_SOUND_GROUP)), true);
    public static final net.minecraftforge.registries.RegistryObject<Block> STARFISH = create("starfish", () -> new StarfishBlock(AbstractBlock.Settings.create().mapColor(MapColor.WHITE).strength(0.1f).nonOpaque().noCollision().sounds(AnglingSounds.SHELL_SOUND_GROUP).ticksRandomly(), false), true);
    public static final net.minecraftforge.registries.RegistryObject<Block> DEAD_STARFISH = create("dead_starfish", () -> new StarfishBlock(AbstractBlock.Settings.copy(STARFISH.get()), true), true);
    public static final net.minecraftforge.registries.RegistryObject<Block> CLAM = create("clam", () -> new ClamBlock(AbstractBlock.Settings.create().mapColor(MapColor.WHITE).sounds(AnglingSounds.SHELL_SOUND_GROUP).offset(AbstractBlock.OffsetType.XZ).dynamicBounds().strength(0.05f).nonOpaque()), true);
    public static final net.minecraftforge.registries.RegistryObject<Block> ANEMONE = create("anemone", () -> new AnemoneBlock(AbstractBlock.Settings.create().mapColor(MapColor.WHITE).strength(0.1f).nonOpaque().noCollision().sounds(BlockSoundGroup.SLIME).ticksRandomly()), true);
    public static final net.minecraftforge.registries.RegistryObject<Block> URCHIN = create("urchin", () -> new UrchinBlock(AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_BLUE).strength(0.1f).nonOpaque().noCollision().sounds(AnglingSounds.SHELL_SOUND_GROUP)), false);
    public static final net.minecraftforge.registries.RegistryObject<Block> SARGASSUM = create("sargassum", () -> new WaterFloatingPlant(AbstractBlock.Settings.create().mapColor(MapColor.YELLOW).replaceable().breakInstantly().nonOpaque().noCollision().sounds(BlockSoundGroup.WET_GRASS)), false);
    public static final net.minecraftforge.registries.RegistryObject<Block> PAPYRUS = create("papyrus", () -> new PapyrusBlock(AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(0).breakInstantly().nonOpaque().noCollision().sounds(BlockSoundGroup.AZALEA).ticksRandomly().offset(AbstractBlock.OffsetType.XZ).dynamicBounds()), true);

    public static java.util.Collection<Item> items() { return ITEMS.getEntries().stream().map(net.minecraftforge.registries.RegistryObject::get).toList(); }

    private static <T extends Block> net.minecraftforge.registries.RegistryObject<T> create(String name, java.util.function.Supplier<T> factory, boolean hasItem) {
        var block = BLOCKS.register(name, factory);
        if (hasItem) ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Settings()));
        return block;
    }

    public static void init(net.minecraftforge.eventbus.api.IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }
}
