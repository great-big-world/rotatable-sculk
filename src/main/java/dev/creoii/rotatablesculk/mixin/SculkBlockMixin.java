package dev.creoii.rotatablesculk.mixin;

import dev.creoii.rotatablesculk.world.feature.SculkPatchFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SculkBlock;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SculkBlock.class)
public abstract class SculkBlockMixin {
    @Shadow
    private static int getDecayPenalty(SculkSpreader spreadManager, BlockPos cursorPos, BlockPos catalystPos, int charge) {
        throw new IllegalStateException();
    }

    @Inject(method = "attemptUseCharge", at = @At("HEAD"), cancellable = true)
    private void gbw$overrideSculkSpread(SculkSpreader.ChargeCursor cursor, LevelAccessor world, BlockPos catalystPos, RandomSource random, SculkSpreader spreadManager, boolean shouldConvertToBlock, CallbackInfoReturnable<Integer> cir) {
        int i = cursor.getCharge();
        if (i != 0 && random.nextInt(spreadManager.chargeDecayRate()) == 0) {
            BlockPos cursorPos = cursor.getPos();
            boolean bl = cursorPos.closerThan(catalystPos, spreadManager.noGrowthRadius());
            if (!bl && shouldNotDecay(world, cursorPos)) {
                int j = spreadManager.growthSpawnCost();
                for (Direction direction : SculkPatchFeature.getRandomizedDirections()) {
                    if (random.nextInt(j) < i) {
                        BlockPos offset = cursorPos.relative(direction);
                        BlockState blockState = getExtraBlockState(world, offset, random, spreadManager.isWorldGeneration(), direction);
                        if (world.getBlockState(offset).isAir() && blockState.isFaceSturdy(world, offset, direction.getOpposite())) {
                            world.setBlock(offset, blockState, 3);
                            world.playSound(null, cursorPos, blockState.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1f, 1f);
                            break;
                        }
                    }
                }
                cir.setReturnValue(Math.max(0, i - j));
            } else cir.setReturnValue(random.nextInt(spreadManager.additionalDecayRate()) != 0 ? i : i - (bl ? 1 : getDecayPenalty(spreadManager, cursorPos, catalystPos, i)));
        } else {
            cir.setReturnValue(i);
        }
    }

    @Unique
    private BlockState getExtraBlockState(LevelAccessor world, BlockPos pos, RandomSource random, boolean allowShrieker, Direction direction) {
        BlockState blockState;
        if (random.nextInt(11) == 0) {
            blockState = Blocks.SCULK_SHRIEKER.defaultBlockState().setValue(SculkShriekerBlock.CAN_SUMMON, allowShrieker);
        } else {
            blockState = Blocks.SCULK_SENSOR.defaultBlockState();
        }

        blockState = blockState.setValue(BlockStateProperties.FACING, direction);

        return blockState.hasProperty(BlockStateProperties.WATERLOGGED) && !world.getFluidState(pos).isEmpty() ? blockState.setValue(BlockStateProperties.WATERLOGGED, true) : blockState;
    }

    @Unique
    private static boolean shouldNotDecay(LevelAccessor world, BlockPos pos) {
        for (Direction direction : SculkPatchFeature.getRandomizedDirections()) {
            BlockState blockState = world.getBlockState(pos.relative(direction));
            if (blockState.isAir() || blockState.is(Blocks.WATER) && blockState.getFluidState().is(Fluids.WATER)) {
                int i = 0;

                for (BlockPos blockPos : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 2, 4))) {
                    BlockState blockState2 = world.getBlockState(blockPos);
                    if (blockState2.is(Blocks.SCULK_SENSOR) || blockState2.is(Blocks.SCULK_SHRIEKER)) {
                        ++i;
                    }

                    if (i > 2)
                        return false;
                }

                return true;
            }
        }
        return false;
    }
}
