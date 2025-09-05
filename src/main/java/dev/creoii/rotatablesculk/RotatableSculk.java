package dev.creoii.rotatablesculk;

import dev.creoii.rotatablesculk.registry.RotatableSculkFeatures;
import net.fabricmc.api.ModInitializer;

public class RotatableSculk implements ModInitializer {
    @Override
    public void onInitialize() {
        RotatableSculkFeatures.register();
    }
}
