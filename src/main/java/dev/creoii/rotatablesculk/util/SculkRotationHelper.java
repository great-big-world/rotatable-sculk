package dev.creoii.rotatablesculk.util;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class SculkRotationHelper {
    public static final VoxelShape SHAPE = Block.box(0d, 0d, 0d, 16d, 8d, 16d);
    public static final VoxelShape DOWN_OUTLINE_SHAPE = Block.box(0d, 8d, 0d, 16d, 16d, 16d);
    public static final VoxelShape EAST_OUTLINE_SHAPE = Block.box(0f, 0f, 0f, 8f, 16f, 16f);
    public static final VoxelShape WEST_OUTLINE_SHAPE = Block.box(8f, 0f, 0f, 16f, 16f, 16f);
    public static final VoxelShape SOUTH_OUTLINE_SHAPE = Block.box(0f, 0f, 0f, 16f, 16f, 8f);
    public static final VoxelShape NORTH_OUTLINE_SHAPE = Block.box(0f, 0f, 8f, 16f, 16f, 16f);

    private static final AABB UP_BOX = SHAPE.bounds().inflate(.01d);
    private static final AABB DOWN_BOX = DOWN_OUTLINE_SHAPE.bounds().inflate(.01d);
    private static final AABB EAST_BOX = EAST_OUTLINE_SHAPE.bounds().inflate(.01d);
    private static final AABB WEST_BOX = WEST_OUTLINE_SHAPE.bounds().inflate(.01d);
    private static final AABB SOUTH_BOX = SOUTH_OUTLINE_SHAPE.bounds().inflate(.01d);
    private static final AABB NORTH_BOX = NORTH_OUTLINE_SHAPE.bounds().inflate(.01d);

    public static double[] getShriekParticleOffsets(BlockState state) {
        Direction facing = state.getValue(BlockStateProperties.FACING);

        double[] offsets = new double[3];

        switch (facing.getAxis()) {
            case X -> {
                offsets[1] = .5d;
                offsets[2] = .5d;
                offsets[0] = facing == Direction.WEST ? 0d : .5d;
            }
            case Y -> {
                offsets[0] = .5d;
                offsets[2] = .5d;
                offsets[1] = facing == Direction.DOWN ? 0d : .5d;
            }
            case Z -> {
                offsets[0] = .5d;
                offsets[1] = .5d;
                offsets[2] = facing == Direction.NORTH ? 0d : .5d;
            }
        }

        return offsets;
    }

    public static double[] getShriekParticleVelocities(Direction facing) {
        double[] velocities = new double[]{0d, 0d, 0d};

        switch (facing.getAxis()) {
            case X -> velocities[0] = facing == Direction.WEST ? -.1d : .1d;
            case Y -> velocities[1] = facing == Direction.DOWN ? -.1d : .1d;
            case Z -> velocities[2] = facing == Direction.NORTH ? -.1d : .1d;
        }

        return velocities;
    }

    public static AABB getBoxForDirection(Direction direction) {
        return switch (direction) {
            case DOWN -> DOWN_BOX;
            case NORTH -> NORTH_BOX;
            case SOUTH -> SOUTH_BOX;
            case WEST -> WEST_BOX;
            case EAST -> EAST_BOX;
            case UP -> UP_BOX;
        };
    }
}
