package dev.creoii.rotatablesculk.mixin.client;

import dev.creoii.rotatablesculk.util.ExtendedShriekParticleEffect;
import dev.creoii.rotatablesculk.util.SculkRotationHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ShriekParticle;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.core.Direction;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ShriekParticle.class)
public abstract class ShriekParticleMixin extends SingleQuadParticle implements ExtendedShriekParticleEffect {
    @Unique
    private Direction direction;

    protected ShriekParticleMixin(ClientLevel clientLevel, double d, double e, double f) {
        super(clientLevel, d, e, f);
    }

    @Override
    public Direction gbw$getDirection() {
        return direction;
    }

    @Override
    public void gbw$setDirection(Direction direction) {
        this.direction = direction;
        double[] velocities = SculkRotationHelper.getShriekParticleVelocities(direction);
        xd = velocities[0];
        yd = velocities[1];
        zd = velocities[2];
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;"))
    private Quaternionf gbw$rotateShriekParticle(Quaternionf instance, float angleY, float angleX, float angleZ) {
        return switch (direction.getAxis()) {
            case X -> instance.rotationXYZ((float)Math.PI, -1.0472f, 0f);
            case Y -> instance.rotationYXZ(-(float)Math.PI, 1.0472f, 0f);
            case Z -> instance.rotationYXZ(-(float)Math.PI, 0f, 1.0472f);
        };
    }
}
