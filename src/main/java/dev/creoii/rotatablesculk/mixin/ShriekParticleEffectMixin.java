package dev.creoii.rotatablesculk.mixin;

import dev.creoii.rotatablesculk.util.ExtendedShriekParticleEffect;
import net.minecraft.particle.ShriekParticleEffect;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ShriekParticleEffect.class)
public class ShriekParticleEffectMixin implements ExtendedShriekParticleEffect {
    @Unique
    private Direction direction = Direction.UP;

    @Override
    public Direction gbw$getDirection() {
        return direction;
    }

    @Override
    public void gbw$setDirection(Direction direction) {
        this.direction = direction;
    }
}
