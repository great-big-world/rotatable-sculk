package dev.creoii.rotatablesculk.registry;

import dev.creoii.rotatablesculk.world.feature.SculkPatchFeature;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SculkPatchFeatureConfig;

public final class RotatableSculkFeatures {
    public static final Feature<SculkPatchFeatureConfig> SCULK_PATCH = new SculkPatchFeature(SculkPatchFeatureConfig.CODEC);

    public static void register() {
        Registry.register(Registries.FEATURE, Identifier.of("great_big_world:sculk_patch"), SCULK_PATCH);
    }
}
