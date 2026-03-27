package dev.creoii.rotatablesculk.mixin.client;

import dev.creoii.rotatablesculk.util.ExtendedShriekParticleEffect;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ShriekParticle;
import net.minecraft.core.particles.ShriekParticleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShriekParticle.Provider.class)
public class ShriekParticleFactoryMixin {
    @Inject(method = "createParticle(Lnet/minecraft/core/particles/ShriekParticleOption;Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At("RETURN"))
    private void gbw$passDirectionToShriekParticle(ShriekParticleOption shriekParticleOption, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i, CallbackInfoReturnable<Particle> cir) {
        if (shriekParticleOption instanceof ExtendedShriekParticleEffect extendedShriekParticleEffect && cir.getReturnValue() instanceof ExtendedShriekParticleEffect extendedShriekParticleEffect1) {
            extendedShriekParticleEffect1.gbw$setDirection(extendedShriekParticleEffect.gbw$getDirection());
        }
    }
}
