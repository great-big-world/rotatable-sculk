package dev.creoii.rotatablesculk.mixin.client;

import dev.creoii.rotatablesculk.util.ExtendedShriekParticleEffect;
import dev.creoii.rotatablesculk.util.SculkRotationHelper;
import net.minecraft.client.particle.ShriekParticle;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShriekParticle.class)
public abstract class ShriekParticleMixin extends SpriteBillboardParticle implements ExtendedShriekParticleEffect {
    protected ShriekParticleMixin(ClientWorld clientWorld, double d, double e, double f) {
        super(clientWorld, d, e, f);
    }

    @Override
    public Direction gbw$getDirection() {
        return Direction.UP;
    }

    @Override
    public void gbw$setDirection(Direction direction) {
        double[] velocities = SculkRotationHelper.getShriekParticleVelocities(direction);
        velocityX = velocities[0];
        velocityY = velocities[1];
        velocityZ = velocities[2];
    }
}
