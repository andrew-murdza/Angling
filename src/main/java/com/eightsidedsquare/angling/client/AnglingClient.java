package com.eightsidedsquare.angling.client;

import com.eightsidedsquare.angling.client.model.CrabEntityModel;
import com.eightsidedsquare.angling.client.model.DongfishEntityModel;
import com.eightsidedsquare.angling.client.model.SunfishEntityModel;
import com.eightsidedsquare.angling.client.particle.AlgaeParticle;
import com.eightsidedsquare.angling.client.particle.WormParticle;
import com.eightsidedsquare.angling.client.renderer.*;
import com.eightsidedsquare.angling.common.entity.RoeBlockEntity;
import com.eightsidedsquare.angling.common.entity.SeaSlugEggsBlockEntity;
import com.eightsidedsquare.angling.common.entity.StarfishBlockEntity;
import com.eightsidedsquare.angling.common.entity.util.SeaSlugColor;
import com.eightsidedsquare.angling.common.entity.util.SeaSlugPattern;
import com.eightsidedsquare.angling.core.AnglingBlocks;
import com.eightsidedsquare.angling.core.AnglingEntities;
import com.eightsidedsquare.angling.core.AnglingItems;
import com.eightsidedsquare.angling.core.AnglingParticles;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.core.object.Color;

import static com.eightsidedsquare.angling.core.AnglingMod.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class AnglingClient {
    private AnglingClient() {}

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            for (net.minecraft.block.Block block : new net.minecraft.block.Block[] {
                    AnglingBlocks.ROE.get(), AnglingBlocks.DUCKWEED.get(), AnglingBlocks.OYSTERS.get(),
                    AnglingBlocks.CLAM.get(), AnglingBlocks.SEA_SLUG_EGGS.get(), AnglingBlocks.PAPYRUS.get(),
                    AnglingBlocks.SARGASSUM.get() }) {
                RenderLayers.setRenderLayer(block, RenderLayer.getCutout());
            }
            RenderLayers.setRenderLayer(AnglingBlocks.ALGAE.get(), RenderLayer.getTranslucent());
            ModelPredicateProviderRegistry.register(AnglingItems.DONGFISH_BUCKET.get(),
                    new Identifier(MOD_ID, "has_horngus"), AnglingClient::dongfishBucketItemHasHorngus);
        });
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(AnglingEntities.FRY.get(), FryEntityRenderer::new);
        event.registerEntityRenderer(AnglingEntities.SUNFISH.get(), BasicEntityRenderer.create(new SunfishEntityModel()));
        event.registerEntityRenderer(AnglingEntities.PELICAN.get(), PelicanEntityRenderer::new);
        event.registerEntityRenderer(AnglingEntities.NAUTILUS.get(), NautilusEntityRenderer::new);
        event.registerEntityRenderer(AnglingEntities.SEA_SLUG.get(), SeaSlugEntityRenderer::new);
        event.registerEntityRenderer(AnglingEntities.CRAB.get(), BasicEntityRenderer.create(new CrabEntityModel()));
        event.registerEntityRenderer(AnglingEntities.DONGFISH.get(), BasicEntityRenderer.create(new DongfishEntityModel()));
        event.registerEntityRenderer(AnglingEntities.CATFISH.get(), BasicEntityRenderer.create("catfish", true));
        event.registerEntityRenderer(AnglingEntities.SEAHORSE.get(), BasicEntityRenderer.create("seahorse", true, "head"));
        event.registerEntityRenderer(AnglingEntities.BUBBLE_EYE.get(), BasicEntityRenderer.create("bubble_eye", true));
        event.registerEntityRenderer(AnglingEntities.ANOMALOCARIS.get(), BasicEntityRenderer.create("anomalocaris", false, "head"));
        event.registerEntityRenderer(AnglingEntities.ANGLERFISH.get(), AnglerfishEntityRenderer::new);
        event.registerEntityRenderer(AnglingEntities.MAHI_MAHI.get(), BasicEntityRenderer.create("mahi_mahi", true));

        event.registerBlockEntityRenderer(AnglingEntities.STARFISH.get(), StarfishBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(AnglingEntities.ANEMONE.get(), AnemoneBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(AnglingEntities.URCHIN.get(), UrchinBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register(RoeBlockEntity::getColor, AnglingBlocks.ROE.get());
        event.register(SeaSlugEggsBlockEntity::getColor, AnglingBlocks.SEA_SLUG_EGGS.get());
        event.register(StarfishBlockEntity::getColor, AnglingBlocks.STARFISH.get());

    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(RoeBlockEntity::getItemColor, AnglingItems.ROE.get());
        event.register(SeaSlugEggsBlockEntity::getItemColor, AnglingItems.SEA_SLUG_EGGS.get());
        event.register(StarfishBlockEntity::getItemColor, AnglingBlocks.STARFISH.get().asItem());
        event.register(AnglingClient::getTropicalFishBucketColor, Items.TROPICAL_FISH_BUCKET);
        event.register(AnglingClient::getFryBucketColor, AnglingItems.FRY_BUCKET.get());
        event.register(AnglingClient::getSeaSlugBucketColor, AnglingItems.SEA_SLUG_BUCKET.get());

    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(AnglingParticles.ALGAE, AlgaeParticle.Factory::new);
        event.registerSpriteSet(AnglingParticles.WORM, WormParticle.Factory::new);
    }

    private static float dongfishBucketItemHasHorngus(ItemStack stack, ClientWorld clientWorld, LivingEntity livingEntity, int i) {
        NbtCompound nbt = stack.getNbt();
        return nbt == null || !nbt.contains("HasHorngus") || nbt.getBoolean("HasHorngus") ? 1 : 0;

    }

    private static int getFryBucketColor(ItemStack stack, int i) {
        if(i == 0)
            return 0xffffff;
        NbtCompound nbt = stack.getNbt();
        if(nbt != null && nbt.contains("Color"))
            return nbt.getInt("Color");
        return 0xffffff;
    }

    private static int getTropicalFishBucketColor(ItemStack stack, int i) {
        NbtCompound nbt = stack.getNbt();
        if(i == 0)
            return 0xffffff;
        float[] colorComponents = (i == 1 ? DyeColor.WHITE : DyeColor.ORANGE).getColorComponents();
        if(nbt != null && nbt.contains("BucketVariantTag")) {
            int variant = nbt.getInt("BucketVariantTag");
            colorComponents = (i == 1 ? TropicalFishEntity.getPatternDyeColor(variant) : TropicalFishEntity.getBaseDyeColor(variant)).getColorComponents();
        }
        return Color.ofRGB(colorComponents[0], colorComponents[1], colorComponents[2]).getColor();
    }

    private static int getSeaSlugBucketColor(ItemStack stack, int i) {
        NbtCompound nbt = stack.getNbt();
        if(i == 0)
            return 0xffffff;
        else if(i == 1 || (nbt != null && nbt.contains("Pattern", NbtElement.STRING_TYPE) && SeaSlugPattern.fromId(nbt.getString("Pattern")).equals(SeaSlugPattern.NONE)))
            return nbt != null && nbt.contains("BaseColor", NbtElement.STRING_TYPE) ? SeaSlugColor.fromId(nbt.getString("BaseColor")).color() : 0x6f4e37;
        return nbt != null && nbt.contains("PatternColor", NbtElement.STRING_TYPE) ? SeaSlugColor.fromId(nbt.getString("PatternColor")).color() : 0xff3800;

    }
}
