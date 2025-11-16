package dev.creoii.rotatablesculk.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.creoii.rotatablesculk.util.SculkRotationHelper;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SculkShriekerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
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

    @Inject(method = "onSteppedOn", at = @At("HEAD"), cancellable = true)
    private void gbw$fixOnSteppedOnForFacing(World world, BlockPos pos, BlockState state, Entity entity, CallbackInfo ci) {
        if (state.get(FACING) != Direction.UP)
            ci.cancel();
    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl) {
        if (state.get(FACING) == Direction.UP)
            return;
        Box box = SculkRotationHelper.getBoxForDirection(state.get(FACING)).offset(pos);
        if (box.intersects(entity.getBoundingBox())) {
            if (world instanceof ServerWorld serverWorld) {
                ServerPlayerEntity serverPlayerEntity = SculkShriekerBlockEntity.findResponsiblePlayerFromEntity(entity);
                if (serverPlayerEntity != null) {
                    serverWorld.getBlockEntity(pos, BlockEntityType.SCULK_SHRIEKER).ifPresent((blockEntity) -> blockEntity.shriek(serverWorld, serverPlayerEntity));
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
            case UP -> SHAPE;
            case NORTH -> SculkRotationHelper.NORTH_OUTLINE_SHAPE;
            case SOUTH -> SculkRotationHelper.SOUTH_OUTLINE_SHAPE;
            case WEST -> SculkRotationHelper.WEST_OUTLINE_SHAPE;
            case EAST -> SculkRotationHelper.EAST_OUTLINE_SHAPE;
        };
    }
}
