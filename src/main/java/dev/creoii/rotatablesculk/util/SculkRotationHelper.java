package dev.creoii.rotatablesculk.util;

import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

public final class SculkRotationHelper {
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
}
