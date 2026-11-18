package com.eightsidedsquare.angling.core;

import com.eightsidedsquare.angling.common.entity.util.*;
import com.eightsidedsquare.angling.common.world.PelicanSpawner;
import com.eightsidedsquare.angling.common.item.RoeBlockItem;
import com.eightsidedsquare.angling.core.ai.*;
import com.eightsidedsquare.angling.core.world.AnglingFeatures;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import software.bernie.geckolib.GeckoLib;
import java.util.Map;
import java.util.WeakHashMap;

@Mod(AnglingMod.MOD_ID)
public final class AnglingMod {
    public static final String MOD_ID = "angling";
    private final Map<ServerWorld, PelicanSpawner> pelicanSpawners = new WeakHashMap<>();

    public AnglingMod() {
        GeckoLib.initialize();
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        // Construct every queued registry entry before the first RegisterEvent.
        AnglingBlocks.init(bus);
        AnglingItems.init(bus);
        AnglingEntities.init(bus);
        AnglingParticles.init();
        Object memory = AnglingMemoryModuleTypes.CAN_TRADE;
        Object sensor = AnglingSensorTypes.PELICAN_ATTACKABLES;
        SunfishVariant.init(bus);
        SeaSlugPattern.init(bus);
        SeaSlugColor.init(bus);
        CrabVariant.init(bus);
        AnglingFeatures.init(bus);
        AnglingRegistration.queue(RegistryKeys.ITEM_GROUP, new Identifier(MOD_ID, "main"),
                ItemGroup.builder().displayName(Text.translatable("itemGroup.angling"))
                        .icon(() -> new ItemStack(AnglingItems.SUNFISH_BUCKET.get()))
                        .entries((context, entries) -> {
                            AnglingBlocks.items().forEach(entries::add);
                            AnglingItems.items().stream().filter(item -> item != AnglingItems.ROE.get()).forEach(entries::add);
                            ((RoeBlockItem) AnglingItems.ROE.get()).appendRoeStacks(entries::add);
                        }).build());
        bus.addListener(AnglingRegistration::register);
        bus.addListener(AnglingSounds::register);
        bus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.addListener(this::worldTick);
        MinecraftForge.EVENT_BUS.addListener(this::worldUnload);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            AnglingItems.init();
            AnglingCriteria.init();
            FishVariantInheritance.init();
            PelicanBeakEntityInitializer.init();
        });
    }

    private void worldTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.level instanceof ServerWorld world) {
            pelicanSpawners.computeIfAbsent(world, ignored -> new PelicanSpawner())
                    .spawn(world, world.getServer().isMonsterSpawningEnabled(), world.getServer().shouldSpawnAnimals());
        }
    }

    private void worldUnload(LevelEvent.Unload event) {
        pelicanSpawners.remove(event.getLevel());
    }
}
