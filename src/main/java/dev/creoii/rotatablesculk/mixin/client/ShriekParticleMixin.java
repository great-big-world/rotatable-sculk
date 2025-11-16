package dev.creoii.rotatablesculk.mixin.client;

import dev.creoii.rotatablesculk.util.ExtendedShriekParticleEffect;
import dev.creoii.rotatablesculk.util.SculkRotationHelper;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.ShriekParticle;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Direction;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ShriekParticle.class)
public abstract class ShriekParticleMixin extends BillboardParticle implements ExtendedShriekParticleEffect {
    @Unique
    private Direction direction;

    protected ShriekParticleMixin(ClientWorld world, double x, double y, double z, Sprite sprite) {
        super(world, x, y, z, sprite);
    }

    @Override
    public Direction gbw$getDirection() {
        return direction;
    }

    @Override
    public void gbw$setDirection(Direction direction) {
        this.direction = direction;
        double[] velocities = SculkRotationHelper.getShriekParticleVelocities(direction);
        velocityX = velocities[0];
        velocityY = velocities[1];
        velocityZ = velocities[2];
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
