package dev.creoii.rotatablesculk.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CalibratedSculkSensorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CalibratedSculkSensorBlock.class)
public abstract class CalibratedSculkSensorBlockMixin extends BaseEntityBlock {
    @Shadow @Final @Mutable
    public static EnumProperty<Direction> FACING;
    @Unique
    private static final VoxelShape OUTLINE_SHAPE = Block.column(16f, 0f, 8f);

    protected CalibratedSculkSensorBlockMixin(Properties settings) {
        super(settings);
    }

    @Inject(method = "createBlockStateDefinition", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/StateDefinition$Builder;add([Lnet/minecraft/world/level/block/state/properties/Property;)Lnet/minecraft/world/level/block/state/StateDefinition$Builder;"), cancellable = true)
    private void gbw$cancelInvalidFacingProperty(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void gbw$initFacingPlacementState(CallbackInfo ci) {
        FACING = BlockStateProperties.FACING;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return OUTLINE_SHAPE;
    }
}
