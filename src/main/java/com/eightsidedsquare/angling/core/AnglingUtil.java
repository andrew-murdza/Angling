package com.eightsidedsquare.angling.core;

import net.minecraftforge.fml.ModList;
import com.eightsidedsquare.angling.common.entity.util.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class AnglingUtil {

    public static <T> List<T> getTagValues(World world, TagKey<T> tagKey) {
        for (AnglingVariantRegistry<?> registry : List.of(SunfishVariant.REGISTRY, CrabVariant.REGISTRY,
                SeaSlugColor.REGISTRY, SeaSlugPattern.REGISTRY)) {
            if (registry.getKey().equals(tagKey.registry())) {
                @SuppressWarnings("unchecked") AnglingVariantRegistry<T> typed = (AnglingVariantRegistry<T>) registry;
                return typed.getTagValues(tagKey);
            }
        }
        return world.getRegistryManager().get(tagKey.registry()).getEntryList(tagKey).map(entries -> entries.stream().map(RegistryEntry::value).toList()).orElse(List.of());
    }

    public static <T> T getRandomTagValue(World world, TagKey<T> tagKey, Random random) {
        return Util.getRandom(getTagValues(world, tagKey), random);
    }

    public static NbtCompound entityToNbt(Entity entity, boolean stripData) {
        NbtCompound nbt = entity.writeNbt(new NbtCompound());
        nbt.putString("id", Registries.ENTITY_TYPE.getId(entity.getType()).toString());
        if(stripData){
            stripEntityNbt(nbt);
        }
        return nbt;
    }

    public static boolean runningSodium() {
        return ModList.get().isLoaded("embeddium") || ModList.get().isLoaded("rubidium");
    }

    public static void stripEntityNbt(NbtCompound nbt) {
        // Yes, this is cursed. No, I'm not sorry.
        nbt.remove("AbsorptionAmount");
        nbt.remove("Air");
        nbt.remove("ArmorDropChances");
        nbt.remove("ArmorItems");
        nbt.remove("Attributes");
        nbt.remove("CanPickUpLoot");
        nbt.remove("DeathTime");
        nbt.remove("FallDistance");
        nbt.remove("FallFlying");
        nbt.remove("Fire");
        nbt.remove("FromBucket");
        nbt.remove("HandDropChances");
        nbt.remove("HandItems");
        nbt.remove("Health");
        nbt.remove("HurtByTimestamp");
        nbt.remove("HurtTime");
        nbt.remove("Invulnerable");
        nbt.remove("LeftHanded");
        nbt.remove("Motion");
        nbt.remove("OnGround");
        nbt.remove("PersistenceRequired");
        nbt.remove("PortalCooldown");
        nbt.remove("Pos");
        nbt.remove("Rotation");
        nbt.remove("cardinal_components");
        nbt.remove(com.eightsidedsquare.angling.cca.FishSpawningComponent.NBT_KEY);
        nbt.remove("UUID");
    }

    public static <A, B, C, D> boolean pairsAreEqual(A a1, B a2, C b1, D b2) {
        return (a1.equals(b1) && a2.equals(b2)) || (a1.equals(b2) && a2.equals(b1));
    }

    public static Optional<Entity> entityFromNbt(NbtCompound nbt, World world) {
        if(!nbt.contains("id", NbtElement.STRING_TYPE))
            return Optional.empty();
        return Optional.ofNullable(EntityType.loadEntityWithPassengers(nbt, world, Function.identity()));
    }


}
