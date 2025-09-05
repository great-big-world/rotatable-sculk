package dev.creoii.rotatablesculk.world.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SculkShriekerBlock;
import net.minecraft.block.SculkSpreadable;
import net.minecraft.block.entity.SculkSpreadManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SculkPatchFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Collections;
import java.util.List;

public class SculkPatchFeature extends Feature<SculkPatchFeatureConfig> {
    private static final List<Direction> DIRECTIONS = Lists.newArrayList(Direction.values());

    public SculkPatchFeature(Codec<SculkPatchFeatureConfig> codec) {
        super(codec);
    }

    public boolean generate(FeatureContext<SculkPatchFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        if (!canGenerate(world, origin)) {
            return false;
        } else {
            SculkPatchFeatureConfig sculkPatchFeatureConfig = context.getConfig();
            Random random = context.getRandom();
            SculkSpreadManager sculkSpreadManager = SculkSpreadManager.createWorldGen();
            int i = sculkPatchFeatureConfig.spreadRounds() + sculkPatchFeatureConfig.growthRounds();

            for (int j = 0; j < i; ++j) {
                for (int k = 0; k < sculkPatchFeatureConfig.chargeCount(); ++k) {
                    sculkSpreadManager.spread(origin, sculkPatchFeatureConfig.amountPerCharge());
                }

                boolean bl = j < sculkPatchFeatureConfig.spreadRounds();

                for (int l = 0; l < sculkPatchFeatureConfig.spreadAttempts(); ++l) {
                    sculkSpreadManager.tick(world, origin, random, bl);
                }

                sculkSpreadManager.clearCursors();
            }

            BlockPos blockPos2 = origin.down();
            if (random.nextFloat() <= sculkPatchFeatureConfig.catalystChance() && world.getBlockState(blockPos2).isFullCube(world, blockPos2)) {
                world.setBlockState(origin, Blocks.SCULK_CATALYST.getDefaultState(), 3);
            }

            int k = sculkPatchFeatureConfig.extraRareGrowths().get(random);

            if (k > 0)
                Collections.shuffle(DIRECTIONS);

            for (int l = 0; l < k; ++l) {
                BlockPos place = origin.add(random.nextInt(5) - 2, 0, random.nextInt(5) - 2);
                for (Direction direction : DIRECTIONS) {
                    BlockPos offset = place.offset(direction);
                    BlockState state = world.getBlockState(offset);
                    if (world.getBlockState(place).isAir() && state.isSideSolidFullSquare(world, offset, direction.getOpposite())) {
                        world.setBlockState(place, Blocks.SCULK_SHRIEKER.getDefaultState().with(SculkShriekerBlock.CAN_SUMMON, true).with(Properties.FACING, direction.getOpposite()), 3);
                        break;
                    }
                }
            }

            return true;
        }
    }

    private boolean canGenerate(WorldAccess world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock() instanceof SculkSpreadable) {
            return true;
        } else if (!blockState.isAir() && (!blockState.isOf(Blocks.WATER) || !blockState.getFluidState().isStill())) {
            return false;
        } else {
            return Direction.stream().map(pos::offset).anyMatch(pos2 -> world.getBlockState(pos2).isFullCube(world, pos2));
        }
    }

    public static List<Direction> getRandomizedDirections() {
        Collections.shuffle(DIRECTIONS);
        return DIRECTIONS;
    }
}
