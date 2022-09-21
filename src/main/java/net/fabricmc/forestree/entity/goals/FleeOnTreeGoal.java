package net.fabricmc.forestree.entity.goals;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;

import java.util.EnumSet;

public class FleeOnTreeGoal extends MoveToTargetPosGoal {

    private static final Block[] Wood = new Block[]{Blocks.ACACIA_LOG, Blocks.BIRCH_LOG, Blocks.JUNGLE_LOG, Blocks.OAK_LOG, Blocks.DARK_OAK_LOG, Blocks.MANGROVE_LOG, Blocks.SPRUCE_LOG};


    public FleeOnTreeGoal(PathAwareEntity mob, double speed, int range, int maxYDifference) {
        super(mob, speed, range, maxYDifference);
        this.targetPos = BlockPos.ORIGIN;
        this.lowestY = 0;
        this.setControls(EnumSet.of(Control.MOVE, Control.JUMP));
    }

    @Override
    public boolean canStart() {
        //ob angefangen werden kann sich zu bewegen. targetpos muss drin gesetzt werden
        if (this.mob.getAttacker() != null) {
            return this.findTargetPos();
        }
        return false;
    }

    @Override
    protected boolean findTargetPos() {
        return super.findTargetPos();
    }

    private boolean isLeaf(BlockPos pos, Chunk chunk) {
        BlockState blockState = chunk.getBlockState(pos);
        return blockState.getMaterial().equals(Material.LEAVES);
    }

    private boolean isLog(BlockPos pos, Chunk chunk) {
        BlockState blockState = chunk.getBlockState(pos);
        for (Block block : Wood) {
            if (blockState.isOf(block)) {
                return true;
            }
        }
        return false;
    }

    protected void startMovingToTarget() {
        this.mob.getNavigation().startMovingTo((float) this.targetPos.getX(), this.targetPos.getY(), (float) this.targetPos.getZ(), this.speed);
    }

    private boolean isTree(WorldView world, BlockPos pos) {
        Chunk chunk = world.getChunk(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()), ChunkStatus.FULL, false);
        if (chunk == null) {
            return false;
        } else {
            return isLeaf(pos, chunk) && isLog(pos.down(), chunk) && isLog(pos.down().down(), chunk);
        }
    }

    @Override
    protected boolean isTargetPos(WorldView world, BlockPos pos) {
        return isTree(world, pos);
    }
}
