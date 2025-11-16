package dev.creoii.rotatablesculk.mixin.client;

import dev.creoii.rotatablesculk.util.ExtendedShriekParticleEffect;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ShriekParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ShriekParticleEffect;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShriekParticle.Factory.class)
public class ShriekParticleFactoryMixin {
    @Inject(method = "createParticle(Lnet/minecraft/particle/ShriekParticleEffect;Lnet/minecraft/client/world/ClientWorld;DDDDDDLnet/minecraft/util/math/random/Random;)Lnet/minecraft/client/particle/Particle;", at = @At("RETURN"))
    private void gbw$passDirectionToShriekParticle(ShriekParticleEffect shriekParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random, CallbackInfoReturnable<Particle> cir) {
        if (shriekParticleEffect instanceof ExtendedShriekParticleEffect extendedShriekParticleEffect && cir.getReturnValue() instanceof ExtendedShriekParticleEffect extendedShriekParticleEffect1) {
            extendedShriekParticleEffect1.gbw$setDirection(extendedShriekParticleEffect.gbw$getDirection());
        }
    }
}
