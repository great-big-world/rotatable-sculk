package dev.creoii.rotatablesculk.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SculkShriekerBlock.class)
public abstract class SculkShriekerBlockMixin extends BlockWithEntity {
    @Shadow @Final private static VoxelShape SHAPE;
    @Unique private static final VoxelShape DOWN_OUTLINE_SHAPE = Block.createColumnShape(16f, 8f, 16f);
    @Unique private static final VoxelShape EAST_OUTLINE_SHAPE = Block.createCuboidShape(0f, 0f, 0f, 8f, 16f, 16f);
    @Unique private static final VoxelShape WEST_OUTLINE_SHAPE = Block.createCuboidShape(8f, 0f, 0f, 16f, 16f, 16f);
    @Unique private static final VoxelShape SOUTH_OUTLINE_SHAPE = Block.createCuboidShape(0f, 0f, 0f, 16f, 16f, 8f);
    @Unique private static final VoxelShape NORTH_OUTLINE_SHAPE = Block.createCuboidShape(0f, 0f, 8f, 16f, 16f, 16f);
    @Unique private static final EnumProperty<Direction> FACING = Properties.FACING;

    protected SculkShriekerBlockMixin(Settings settings) {
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

    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
    private void gbw$fixOutlineShapeForFacing(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        cir.setReturnValue(getShape(state.get(FACING)));
    }

    @Inject(method = "getCullingShape", at = @At("HEAD"), cancellable = true)
    private void gbw$fixCullingShapeForFacing(BlockState state, CallbackInfoReturnable<VoxelShape> cir) {
        cir.setReturnValue(getShape(state.get(FACING)));
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
            case DOWN -> DOWN_OUTLINE_SHAPE;
            case UP -> SHAPE;
            case NORTH -> NORTH_OUTLINE_SHAPE;
            case SOUTH -> SOUTH_OUTLINE_SHAPE;
            case WEST -> WEST_OUTLINE_SHAPE;
            case EAST -> EAST_OUTLINE_SHAPE;
        };
    }
}
