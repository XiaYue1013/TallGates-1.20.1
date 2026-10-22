package se.gory_moon.tallgates.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class BlockTallGate extends FenceGateBlock {
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    // Hitbox / selection shape (16 pixels high for both lower and upper)
    protected static final VoxelShape Z_SHAPE = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
    protected static final VoxelShape X_SHAPE = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);

    // Collision shape when closed:
    // Lower half collision is full 1.0 block (16 pixels) high
    protected static final VoxelShape LOWER_Z_COLLISION = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
    protected static final VoxelShape LOWER_X_COLLISION = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);
    // Upper half collision is 1.5 blocks (24 pixels) high so entities cannot jump over
    protected static final VoxelShape UPPER_Z_COLLISION = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 24.0D, 10.0D);
    protected static final VoxelShape UPPER_X_COLLISION = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 24.0D, 16.0D);

    protected final SoundEvent openSound;
    protected final SoundEvent closeSound;
    protected final boolean flammable;

    public BlockTallGate(BlockBehaviour.Properties properties, WoodType woodType) {
        this(properties, woodType.fenceGateOpen(), woodType.fenceGateClose(), woodType != WoodType.CRIMSON && woodType != WoodType.WARPED);
    }

    public BlockTallGate(BlockBehaviour.Properties properties, SoundEvent openSound, SoundEvent closeSound, boolean flammable) {
        super(properties, openSound, closeSound);
        this.openSound = openSound;
        this.closeSound = closeSound;
        this.flammable = flammable;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPEN, Boolean.FALSE)
                .setValue(POWERED, Boolean.FALSE)
                .setValue(IN_WALL, Boolean.FALSE)
                .setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(FACING).getAxis() == Direction.Axis.X ? X_SHAPE : Z_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(OPEN)) {
            return Shapes.empty();
        }
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return state.getValue(FACING).getAxis() == Direction.Axis.X ? LOWER_X_COLLISION : LOWER_Z_COLLISION;
        } else {
            return state.getValue(FACING).getAxis() == Direction.Axis.X ? UPPER_X_COLLISION : UPPER_Z_COLLISION;
        }
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
        if (state.getValue(OPEN)) {
            return Shapes.empty();
        }
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return state.getValue(FACING).getAxis() == Direction.Axis.X ? LOWER_X_COLLISION : LOWER_Z_COLLISION;
        } else {
            return state.getValue(FACING).getAxis() == Direction.Axis.X ? UPPER_X_COLLISION : UPPER_Z_COLLISION;
        }
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
        switch (type) {
            case LAND:
            case AIR:
                return state.getValue(OPEN);
            case WATER:
            default:
                return false;
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (pos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(pos.above()).canBeReplaced(context)) {
            boolean powered = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above());
            Direction direction = context.getHorizontalDirection();
            Direction.Axis axis = direction.getAxis();
            boolean inWall = (axis == Direction.Axis.Z && (isWall(level.getBlockState(pos.west())) || isWall(level.getBlockState(pos.east()))))
                    || (axis == Direction.Axis.X && (isWall(level.getBlockState(pos.north())) || isWall(level.getBlockState(pos.south()))));
            return this.defaultBlockState()
                    .setValue(FACING, direction)
                    .setValue(OPEN, powered)
                    .setValue(POWERED, powered)
                    .setValue(IN_WALL, inWall)
                    .setValue(HALF, DoubleBlockHalf.LOWER);
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        BlockPos posAbove = pos.above();
        Direction.Axis axis = state.getValue(FACING).getAxis();
        boolean inWallUpper = (axis == Direction.Axis.Z && (isWall(level.getBlockState(posAbove.west())) || isWall(level.getBlockState(posAbove.east()))))
                || (axis == Direction.Axis.X && (isWall(level.getBlockState(posAbove.north())) || isWall(level.getBlockState(posAbove.south()))));
        level.setBlock(posAbove, state.setValue(HALF, DoubleBlockHalf.UPPER).setValue(IN_WALL, inWallUpper), 3);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        DoubleBlockHalf half = state.getValue(HALF);
        if (half == DoubleBlockHalf.LOWER) {
            BlockPos belowPos = pos.below();
            BlockState belowState = level.getBlockState(belowPos);
            return belowState.isFaceSturdy(level, belowPos, Direction.UP)
                    || belowState.is(BlockTags.FENCES)
                    || belowState.is(BlockTags.WALLS);
        } else {
            BlockState belowState = level.getBlockState(pos.below());
            return belowState.is(this) && belowState.getValue(HALF) == DoubleBlockHalf.LOWER;
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockHalf half = state.getValue(HALF);
        if (direction.getAxis() == Direction.Axis.Y && (half == DoubleBlockHalf.LOWER) == (direction == Direction.UP)) {
            if (neighborState.is(this) && neighborState.getValue(HALF) != half) {
                return state.setValue(FACING, neighborState.getValue(FACING))
                        .setValue(OPEN, neighborState.getValue(OPEN));
            } else {
                return Blocks.AIR.defaultBlockState();
            }
        }
        if (half == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        Direction.Axis axis = direction.getAxis();
        if (state.getValue(FACING).getClockWise().getAxis() == axis) {
            boolean inWall = isWall(neighborState) || isWall(level.getBlockState(pos.relative(direction.getOpposite())));
            state = state.setValue(IN_WALL, inWall);
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && player.isCreative()) {
            preventCreativeDropFromBottomPart(level, pos, state, player);
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    protected static void preventCreativeDropFromBottomPart(Level level, BlockPos pos, BlockState state, Player player) {
        DoubleBlockHalf half = state.getValue(HALF);
        if (half == DoubleBlockHalf.UPPER) {
            BlockPos belowPos = pos.below();
            BlockState belowState = level.getBlockState(belowPos);
            if (belowState.is(state.getBlock()) && belowState.getValue(HALF) == DoubleBlockHalf.LOWER) {
                BlockState airState = belowState.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                level.setBlock(belowPos, airState, 35);
                level.levelEvent(player, 2001, belowPos, Block.getId(belowState));
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        DoubleBlockHalf half = state.getValue(HALF);
        BlockPos lowerPos = half == DoubleBlockHalf.LOWER ? pos : pos.below();
        BlockPos upperPos = half == DoubleBlockHalf.LOWER ? pos.above() : pos;

        BlockState lowerState = level.getBlockState(lowerPos);
        BlockState upperState = level.getBlockState(upperPos);

        if (!lowerState.is(this) || !upperState.is(this)) {
            return InteractionResult.PASS;
        }

        boolean newOpen = !lowerState.getValue(OPEN);
        Direction newFacing = lowerState.getValue(FACING);

        if (newOpen) {
            Direction playerDirection = player.getDirection();
            if (newFacing == playerDirection.getOpposite()) {
                newFacing = playerDirection;
            }
        }

        BlockState newLowerState = lowerState.setValue(OPEN, newOpen).setValue(FACING, newFacing);
        BlockState newUpperState = upperState.setValue(OPEN, newOpen).setValue(FACING, newFacing);

        level.setBlock(lowerPos, newLowerState, 10);
        level.setBlock(upperPos, newUpperState, 10);

        SoundEvent sound = newOpen ? this.openSound : this.closeSound;
        level.playSound(player, pos, sound, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
        level.gameEvent(player, newOpen ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (level.isClientSide) {
            return;
        }

        DoubleBlockHalf half = state.getValue(HALF);
        BlockPos lowerPos = half == DoubleBlockHalf.LOWER ? pos : pos.below();
        BlockPos upperPos = half == DoubleBlockHalf.LOWER ? pos.above() : pos;

        BlockState lowerState = level.getBlockState(lowerPos);
        BlockState upperState = level.getBlockState(upperPos);

        if (!lowerState.is(this) || !upperState.is(this)) {
            return;
        }

        boolean isPowered = level.hasNeighborSignal(lowerPos) || level.hasNeighborSignal(upperPos);

        if (state.getValue(POWERED) != isPowered) {
            boolean wasOpen = state.getValue(OPEN);
            BlockState newLower = lowerState.setValue(POWERED, isPowered).setValue(OPEN, isPowered);
            BlockState newUpper = upperState.setValue(POWERED, isPowered).setValue(OPEN, isPowered);

            level.setBlock(lowerPos, newLower, 2);
            level.setBlock(upperPos, newUpper, 2);

            if (wasOpen != isPowered) {
                SoundEvent sound = isPowered ? this.openSound : this.closeSound;
                level.playSound((Player) null, pos, sound, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
                level.gameEvent((Entity) null, isPowered ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
            }
        }
    }

    private boolean isWall(BlockState state) {
        return state.is(BlockTags.WALLS);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return mirror == Mirror.NONE ? state : state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        return flammable ? 20 : 0;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        return flammable ? 5 : 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN, POWERED, IN_WALL, HALF);
    }
}
