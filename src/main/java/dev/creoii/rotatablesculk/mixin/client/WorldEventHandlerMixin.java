package dev.creoii.rotatablesculk.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import dev.creoii.rotatablesculk.util.ExtendedShriekParticleEffect;
import dev.creoii.rotatablesculk.util.SculkRotationHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.world.WorldEventHandler;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldEventHandler.class)
public class WorldEventHandlerMixin {
    @Shadow @Final private ClientWorld world;

    @Redirect(method = "processWorldEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;addParticleClient(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V", ordinal = 9))
    private void gbw$fixSculkShriekParticleOffset(ClientWorld instance, ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, @Local(argsOnly = true) BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        double[] offsets = SculkRotationHelper.getShriekParticleOffsets(state);
        if (parameters instanceof ExtendedShriekParticleEffect extendedShriekParticleEffect && state.contains(Properties.FACING)) {
            extendedShriekParticleEffect.gbw$setDirection(state.get(Properties.FACING));
        }
        instance.addParticleClient(parameters, pos.getX() + offsets[0], pos.getY() + offsets[1], pos.getZ() + offsets[2], 0f, 0f, 0f);
    }
}
