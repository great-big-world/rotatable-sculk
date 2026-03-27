package dev.creoii.rotatablesculk.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.creoii.rotatablesculk.util.SculkRotationHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SculkShriekerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SculkShriekerBlock.class)
public abstract class SculkShriekerBlockMixin extends BaseEntityBlock {
    @Shadow @Final private static VoxelShape SHAPE_COLLISION;
    @Unique private static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    protected SculkShriekerBlockMixin(Properties settings) {
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

    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
    private void gbw$fixOutlineShapeForFacing(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        cir.setReturnValue(getShape(state.getValue(FACING)));
    }

    @Inject(method = "getOcclusionShape", at = @At("HEAD"), cancellable = true)
    private void gbw$fixCullingShapeForFacing(BlockState state, CallbackInfoReturnable<VoxelShape> cir) {
        cir.setReturnValue(getShape(state.getValue(FACING)));
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
        if (box.intersects(entity.getBoundingBox())) {
            if (world instanceof ServerLevel serverWorld) {
                ServerPlayer serverPlayerEntity = SculkShriekerBlockEntity.tryGetPlayer(entity);
                if (serverPlayerEntity != null) {
                    serverWorld.getBlockEntity(pos, BlockEntityType.SCULK_SHRIEKER).ifPresent((blockEntity) -> blockEntity.tryShriek(serverWorld, serverPlayerEntity));
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
            case UP -> SHAPE_COLLISION;
            case NORTH -> SculkRotationHelper.NORTH_OUTLINE_SHAPE;
            case SOUTH -> SculkRotationHelper.SOUTH_OUTLINE_SHAPE;
            case WEST -> SculkRotationHelper.WEST_OUTLINE_SHAPE;
            case EAST -> SculkRotationHelper.EAST_OUTLINE_SHAPE;
        };
    }
}
