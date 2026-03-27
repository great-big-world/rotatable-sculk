package dev.creoii.rotatablesculk.registry;

import dev.creoii.rotatablesculk.world.feature.SculkPatchFeature;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.SculkPatchConfiguration;

public final class RotatableSculkFeatures {
    public static final Feature<SculkPatchConfiguration> SCULK_PATCH = new SculkPatchFeature(SculkPatchConfiguration.CODEC);

    public static void register() {
        Registry.register(BuiltInRegistries.FEATURE, ResourceLocation.tryParse("great_big_world:sculk_patch"), SCULK_PATCH);
    }
}
