package dev.creoii.rotatablesculk.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.creoii.rotatablesculk.util.ExtendedShriekParticleEffect;
import dev.creoii.rotatablesculk.util.SculkRotationHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ShriekParticle;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.core.Direction;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;

@Mixin(ShriekParticle.class)
public abstract class ShriekParticleMixin extends SingleQuadParticle implements ExtendedShriekParticleEffect {
    @Shadow
    protected abstract void renderRotatedParticle(VertexConsumer vertexConsumer, Camera camera, float f, Consumer<Quaternionf> consumer);

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

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ShriekParticle;renderRotatedParticle(Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/client/Camera;FLjava/util/function/Consumer;)V", ordinal = 1))
    private void gbw$rotateShriekParticle(ShriekParticle instance, VertexConsumer vertexConsumer, Camera camera, float f, Consumer<Quaternionf> consumer, Operation<Void> original) {
        renderRotatedParticle(vertexConsumer, camera, f, quaternionf -> {
            quaternionf.mul(switch (direction.getAxis()) {
                case X -> new Quaternionf().rotationXYZ((float)Math.PI, -1.0472f, 0f);
                case Y -> new Quaternionf().rotationYXZ(-(float)Math.PI, 1.0472f, 0f);
                case Z -> new Quaternionf().rotationYXZ(-(float)Math.PI, 0f, 1.0472f);
            });
        });
    }
}
