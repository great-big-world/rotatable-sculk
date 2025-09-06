package dev.creoii.rotatablesculk.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.creoii.rotatablesculk.util.SculkRotationHelper;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SculkSensorBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SculkSensorBlock.class)
public abstract class SculkSensorBlockMixin extends BlockWithEntity {
    @Shadow
    public static boolean isInactive(BlockState state) {
        throw new IllegalStateException();
    }

    @Unique private static final EnumProperty<Direction> FACING = Properties.FACING;

    protected SculkSensorBlockMixin(Settings settings) {
        super(settings);
    }

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;with(Lnet/minecraft/state/property/Property;Ljava/lang/Comparable;)Ljava/lang/Object;", ordinal = 0))
    private <S, T extends Comparable<T>, V extends T> S gbw$initFacingDefaultState(BlockState instance, Property<T> property, Comparable<V> comparable, Operation<S> original) {
        return original.call(instance.with(FACING, Direction.UP), property, comparable);
    }

    @ModifyReturnValue(method = "getPlacementState", at = @At("RETURN"))
    private BlockState gbw$fixFacingPlacementState(BlockState original, @Local(argsOnly = true) ItemPlacementContext ctx) {
        return original.with(FACING, ctx.getSide());
    }

    @Inject(method = "appendProperties", at = @At("TAIL"))
    private void gbw$appendFacingProperty(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(FACING);
    }

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void gbw$fixOutlineShapeForFacing(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        cir.setReturnValue(getShape(state.get(FACING)));
    }

    @WrapOperation(method = "updateNeighbors", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;down()Lnet/minecraft/util/math/BlockPos;"))
    private static BlockPos gbw$fixUpdateNeighborsForFacing(BlockPos instance, Operation<BlockPos> original, @Local(argsOnly = true) BlockState state) {
        return instance.offset(state.get(FACING).getOpposite());
    }

    @Inject(method = "onSteppedOn", at = @At("HEAD"), cancellable = true)
    private void gbw$fixOnSteppedOnForFacing(World world, BlockPos pos, BlockState state, Entity entity, CallbackInfo ci) {
        if (state.get(FACING) != Direction.UP)
            ci.cancel();
    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler) {
        if (state.get(FACING) == Direction.UP)
            return;
        Box box = SculkRotationHelper.getBoxForDirection(state.get(FACING)).offset(pos);
        if (box.intersects(entity.getBoundingBox())) {
            if (!world.isClient() && isInactive(state) && entity.getType() != EntityType.WARDEN) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof SculkSensorBlockEntity sculkSensorBlockEntity) {
                    if (world instanceof ServerWorld serverWorld) {
                        if (sculkSensorBlockEntity.getVibrationCallback().accepts(serverWorld, pos, GameEvent.STEP, GameEvent.Emitter.of(state))) {
                            sculkSensorBlockEntity.getEventListener().forceListen(serverWorld, GameEvent.STEP, GameEvent.Emitter.of(entity), entity.getPos());
                        }
                    }
                }
            }
        }
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Unique
    private static VoxelShape getShape(Direction facing) {
        return switch (facing) {
            case DOWN -> SculkRotationHelper.DOWN_OUTLINE_SHAPE;
            case UP -> SculkSensorBlock.OUTLINE_SHAPE;
            case NORTH -> SculkRotationHelper.NORTH_OUTLINE_SHAPE;
            case SOUTH -> SculkRotationHelper.SOUTH_OUTLINE_SHAPE;
            case WEST -> SculkRotationHelper.WEST_OUTLINE_SHAPE;
            case EAST -> SculkRotationHelper.EAST_OUTLINE_SHAPE;
        };
    }
}
