package dev.creoii.rotatablesculk.world.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SculkBehaviour;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.SculkPatchConfiguration;

public class SculkPatchFeature extends Feature<SculkPatchConfiguration> {
    private static final List<Direction> DIRECTIONS = Lists.newArrayList(Direction.values());

    public SculkPatchFeature(Codec<SculkPatchConfiguration> codec) {
        super(codec);
    }

    public boolean place(FeaturePlaceContext<SculkPatchConfiguration> context) {
        WorldGenLevel world = context.level();
        BlockPos origin = context.origin();
        if (!canGenerate(world, origin)) {
            return false;
        } else {
            SculkPatchConfiguration sculkPatchFeatureConfig = context.config();
            RandomSource random = context.random();
            SculkSpreader sculkSpreadManager = SculkSpreader.createWorldGenSpreader();
            int i = sculkPatchFeatureConfig.spreadRounds() + sculkPatchFeatureConfig.growthRounds();

            for (int j = 0; j < i; ++j) {
                for (int k = 0; k < sculkPatchFeatureConfig.chargeCount(); ++k) {
                    sculkSpreadManager.addCursors(origin, sculkPatchFeatureConfig.amountPerCharge());
                }

                boolean bl = j < sculkPatchFeatureConfig.spreadRounds();

                for (int l = 0; l < sculkPatchFeatureConfig.spreadAttempts(); ++l) {
                    sculkSpreadManager.updateCursors(world, origin, random, bl);
                }

                sculkSpreadManager.clear();
            }

            BlockPos blockPos2 = origin.below();
            if (random.nextFloat() <= sculkPatchFeatureConfig.catalystChance() && world.getBlockState(blockPos2).isCollisionShapeFullBlock(world, blockPos2)) {
                world.setBlock(origin, Blocks.SCULK_CATALYST.defaultBlockState(), 3);
            }

            int k = sculkPatchFeatureConfig.extraRareGrowths().sample(random);

            if (k > 0)
                Collections.shuffle(DIRECTIONS);

            for (int l = 0; l < k; ++l) {
                BlockPos place = origin.offset(random.nextInt(5) - 2, 0, random.nextInt(5) - 2);
                for (Direction direction : DIRECTIONS) {
                    BlockPos offset = place.relative(direction);
                    BlockState state = world.getBlockState(offset);
                    if (world.getBlockState(place).isAir() && state.isFaceSturdy(world, offset, direction.getOpposite())) {
                        world.setBlock(place, Blocks.SCULK_SHRIEKER.defaultBlockState().setValue(SculkShriekerBlock.CAN_SUMMON, true).setValue(BlockStateProperties.FACING, direction.getOpposite()), 3);
                        break;
                    }
                }
            }

            return true;
        }
    }

    private boolean canGenerate(LevelAccessor world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock() instanceof SculkBehaviour) {
            return true;
        } else if (!blockState.isAir() && (!blockState.is(Blocks.WATER) || !blockState.getFluidState().isSource())) {
            return false;
        } else {
            return Direction.stream().map(pos::relative).anyMatch(pos2 -> world.getBlockState(pos2).isCollisionShapeFullBlock(world, pos2));
        }
    }

    public static List<Direction> getRandomizedDirections() {
        Collections.shuffle(DIRECTIONS);
        return DIRECTIONS;
    }
}
