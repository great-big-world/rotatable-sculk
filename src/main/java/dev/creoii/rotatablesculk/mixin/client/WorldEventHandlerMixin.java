package dev.creoii.rotatablesculk.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import dev.creoii.rotatablesculk.util.ExtendedShriekParticleEffect;
import dev.creoii.rotatablesculk.util.SculkRotationHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelEventHandler.class)
public class WorldEventHandlerMixin {
    @Shadow @Final private ClientLevel level;

    @Redirect(method = "levelEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V", ordinal = 9))
    private void gbw$fixSculkShriekParticleOffset(ClientLevel instance, ParticleOptions parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, @Local(argsOnly = true) BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        double[] offsets = SculkRotationHelper.getShriekParticleOffsets(state);
        if (parameters instanceof ExtendedShriekParticleEffect extendedShriekParticleEffect && state.hasProperty(BlockStateProperties.FACING)) {
            extendedShriekParticleEffect.gbw$setDirection(state.getValue(BlockStateProperties.FACING));
        }
        instance.addParticle(parameters, pos.getX() + offsets[0], pos.getY() + offsets[1], pos.getZ() + offsets[2], 0f, 0f, 0f);
    }
}
