package dev.creoii.rotatablesculk.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

public final class SculkRotationHelper {
    public static final VoxelShape DOWN_OUTLINE_SHAPE = Block.createColumnShape(16f, 8f, 16f);
    public static final VoxelShape EAST_OUTLINE_SHAPE = Block.createCuboidShape(0f, 0f, 0f, 8f, 16f, 16f);
    public static final VoxelShape WEST_OUTLINE_SHAPE = Block.createCuboidShape(8f, 0f, 0f, 16f, 16f, 16f);
    public static final VoxelShape SOUTH_OUTLINE_SHAPE = Block.createCuboidShape(0f, 0f, 0f, 16f, 16f, 8f);
    public static final VoxelShape NORTH_OUTLINE_SHAPE = Block.createCuboidShape(0f, 0f, 8f, 16f, 16f, 16f);

    private static final Box UP_BOX = SculkSensorBlock.OUTLINE_SHAPE.getBoundingBox().expand(.01d);
    private static final Box DOWN_BOX = DOWN_OUTLINE_SHAPE.getBoundingBox().expand(.01d);
    private static final Box EAST_BOX = EAST_OUTLINE_SHAPE.getBoundingBox().expand(.01d);
    private static final Box WEST_BOX = WEST_OUTLINE_SHAPE.getBoundingBox().expand(.01d);
    private static final Box SOUTH_BOX = SOUTH_OUTLINE_SHAPE.getBoundingBox().expand(.01d);
    private static final Box NORTH_BOX = NORTH_OUTLINE_SHAPE.getBoundingBox().expand(.01d);


    public static double[] getShriekParticleOffsets(BlockState state) {
        Direction facing = state.get(Properties.FACING);

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

    public static Box getBoxForDirection(Direction direction) {
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
