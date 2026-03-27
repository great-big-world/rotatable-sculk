package dev.creoii.rotatablesculk.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.creoii.rotatablesculk.util.ExtendedShriekParticleEffect;
import dev.creoii.rotatablesculk.util.SculkRotationHelper;
import net.minecraft.client.renderer.LevelEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelEventHandler.class)
public class WorldEventHandlerMixin {
    @Shadow
    @Final
    private Level level;

    @WrapOperation(method = "levelEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V", ordinal = 4))
    private void gbw$fixSculkShriekParticleOffset(Level instance, ParticleOptions particleOptions, double d, double e, double f, double g, double h, double i, Operation<Void> original, @Local(argsOnly = true) BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        double[] offsets = SculkRotationHelper.getShriekParticleOffsets(state);
        if (particleOptions instanceof ExtendedShriekParticleEffect extendedShriekParticleEffect && state.hasProperty(BlockStateProperties.FACING)) {
            extendedShriekParticleEffect.gbw$setDirection(state.getValue(BlockStateProperties.FACING));
        }
        instance.addParticle(particleOptions, pos.getX() + offsets[0], pos.getY() + offsets[1], pos.getZ() + offsets[2], 0f, 0f, 0f);
    }
}
