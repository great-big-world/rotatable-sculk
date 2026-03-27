package dev.creoii.rotatablesculk.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.creoii.rotatablesculk.util.SculkRotationHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SculkSensorBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SculkSensorBlock.class)
public abstract class SculkSensorBlockMixin extends BaseEntityBlock {
    @Shadow
    public static boolean canActivate(BlockState state) {
        throw new IllegalStateException();
    }

    @Unique private static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    protected SculkSensorBlockMixin(Properties settings) {
        super(settings);
    }

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;setValue(Lnet/minecraft/world/level/block/state/properties/Property;Ljava/lang/Comparable;)Ljava/lang/Object;", ordinal = 0))
    private <S, T extends Comparable<T>, V extends T> S gbw$initFacingDefaultState(BlockState instance, Property<T> property, Comparable<V> comparable, Operation<S> original) {
        return original.call(instance.setValue(FACING, Direction.UP), property, comparable);
    }

    @ModifyReturnValue(method = "getStateForPlacement", at = @At("RETURN"))
    private BlockState gbw$fixFacingPlacementState(BlockState original, @Local(argsOnly = true) BlockPlaceContext ctx) {
        return original.setValue(FACING, ctx.getClickedFace());
    }

    @Inject(method = "createBlockStateDefinition", at = @At("TAIL"))
    private void gbw$appendFacingProperty(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(FACING);
    }

    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    private void gbw$fixOutlineShapeForFacing(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        cir.setReturnValue(getShape(state.getValue(FACING)));
    }

    @WrapOperation(method = "updateNeighbours", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;below()Lnet/minecraft/core/BlockPos;"))
    private static BlockPos gbw$fixUpdateNeighborsForFacing(BlockPos instance, Operation<BlockPos> original, @Local(argsOnly = true) BlockState state) {
        return instance.relative(state.getValue(FACING).getOpposite());
    }

    @Inject(method = "stepOn", at = @At("HEAD"), cancellable = true)
    private void gbw$fixOnSteppedOnForFacing(Level world, BlockPos pos, BlockState state, Entity entity, CallbackInfo ci) {
        if (state.getValue(FACING) != Direction.UP)
            ci.cancel();
    }

    @Override
    protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity, InsideBlockEffectApplier insideBlockEffectApplier) {
        if (state.getValue(FACING) == Direction.UP)
            return;
        AABB box = SculkRotationHelper.getBoxForDirection(state.getValue(FACING)).move(pos);
        if (box.intersects(entity.getBoundingBox()) && !world.isClientSide() && canActivate(state) && entity.getType() != EntityType.WARDEN) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof SculkSensorBlockEntity sculkSensorBlockEntity) {
                if (world instanceof ServerLevel serverWorld) {
                    if (sculkSensorBlockEntity.getVibrationUser().canReceiveVibration(serverWorld, pos, GameEvent.STEP, GameEvent.Context.of(state))) {
                        sculkSensorBlockEntity.getListener().forceScheduleVibration(serverWorld, GameEvent.STEP, GameEvent.Context.of(entity), entity.position());
                    }
                }
            }
        }
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Unique
    private static VoxelShape getShape(Direction facing) {
        return switch (facing) {
            case DOWN -> SculkRotationHelper.DOWN_OUTLINE_SHAPE;
            case UP -> SculkRotationHelper.SHAPE;
            case NORTH -> SculkRotationHelper.NORTH_OUTLINE_SHAPE;
            case SOUTH -> SculkRotationHelper.SOUTH_OUTLINE_SHAPE;
            case WEST -> SculkRotationHelper.WEST_OUTLINE_SHAPE;
            case EAST -> SculkRotationHelper.EAST_OUTLINE_SHAPE;
        };
    }
}
