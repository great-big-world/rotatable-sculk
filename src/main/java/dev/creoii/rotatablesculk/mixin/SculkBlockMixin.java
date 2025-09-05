package dev.creoii.rotatablesculk.mixin;

import dev.creoii.rotatablesculk.world.feature.SculkPatchFeature;
import net.minecraft.block.*;
import net.minecraft.block.entity.SculkSpreadManager;
import net.minecraft.fluid.Fluids;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SculkBlock.class)
public abstract class SculkBlockMixin {
    @Shadow
    private static int getDecay(SculkSpreadManager spreadManager, BlockPos cursorPos, BlockPos catalystPos, int charge) {
        throw new IllegalStateException();
    }

    @Inject(method = "spread", at = @At("HEAD"), cancellable = true)
    private void gbw$overrideSculkSpread(SculkSpreadManager.Cursor cursor, WorldAccess world, BlockPos catalystPos, Random random, SculkSpreadManager spreadManager, boolean shouldConvertToBlock, CallbackInfoReturnable<Integer> cir) {
        int i = cursor.getCharge();
        if (i != 0 && random.nextInt(spreadManager.getSpreadChance()) == 0) {
            BlockPos cursorPos = cursor.getPos();
            boolean bl = cursorPos.isWithinDistance(catalystPos, spreadManager.getMaxDistance());
            if (!bl && shouldNotDecay(world, cursorPos)) {
                int j = spreadManager.getExtraBlockChance();
                for (Direction direction : SculkPatchFeature.getRandomizedDirections()) {
                    if (random.nextInt(j) < i) {
                        BlockPos offset = cursorPos.offset(direction);
                        BlockState blockState = getExtraBlockState(world, offset, random, spreadManager.isWorldGen(), direction);
                        if (world.getBlockState(offset).isAir() && blockState.isSideSolidFullSquare(world, offset, direction.getOpposite())) {
                            world.setBlockState(offset, blockState, 3);
                            world.playSound(null, cursorPos, blockState.getSoundGroup().getPlaceSound(), SoundCategory.BLOCKS, 1f, 1f);
                            break;
                        }
                    }
                }
                cir.setReturnValue(Math.max(0, i - j));
            } else cir.setReturnValue(random.nextInt(spreadManager.getDecayChance()) != 0 ? i : i - (bl ? 1 : getDecay(spreadManager, cursorPos, catalystPos, i)));
        } else {
            cir.setReturnValue(i);
        }
    }

    @Unique
    private BlockState getExtraBlockState(WorldAccess world, BlockPos pos, Random random, boolean allowShrieker, Direction direction) {
        BlockState blockState;
        if (random.nextInt(11) == 0) {
            blockState = Blocks.SCULK_SHRIEKER.getDefaultState().with(SculkShriekerBlock.CAN_SUMMON, allowShrieker);
        } else {
            blockState = Blocks.SCULK_SENSOR.getDefaultState();
        }

        blockState = blockState.with(Properties.FACING, direction);

        return blockState.contains(Properties.WATERLOGGED) && !world.getFluidState(pos).isEmpty() ? blockState.with(Properties.WATERLOGGED, true) : blockState;
    }

    @Unique
    private static boolean shouldNotDecay(WorldAccess world, BlockPos pos) {
        for (Direction direction : SculkPatchFeature.getRandomizedDirections()) {
            BlockState blockState = world.getBlockState(pos.offset(direction));
            if (blockState.isAir() || blockState.isOf(Blocks.WATER) && blockState.getFluidState().isOf(Fluids.WATER)) {
                int i = 0;

                for (BlockPos blockPos : BlockPos.iterate(pos.add(-4, 0, -4), pos.add(4, 2, 4))) {
                    BlockState blockState2 = world.getBlockState(blockPos);
                    if (blockState2.isOf(Blocks.SCULK_SENSOR) || blockState2.isOf(Blocks.SCULK_SHRIEKER)) {
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
