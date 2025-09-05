package dev.creoii.rotatablesculk.mixin;

import net.minecraft.block.*;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CalibratedSculkSensorBlock.class)
public abstract class CalibratedSculkSensorBlockMixin extends BlockWithEntity {
    @Shadow @Final @Mutable
    public static EnumProperty<Direction> FACING;
    @Unique
    private static final VoxelShape OUTLINE_SHAPE = Block.createColumnShape(16f, 0f, 8f);

    protected CalibratedSculkSensorBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "appendProperties", at = @At(value = "INVOKE", target = "Lnet/minecraft/state/StateManager$Builder;add([Lnet/minecraft/state/property/Property;)Lnet/minecraft/state/StateManager$Builder;"), cancellable = true)
    private void gbw$cancelInvalidFacingProperty(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void gbw$initFacingPlacementState(CallbackInfo ci) {
        FACING = Properties.FACING;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE;
    }
}
