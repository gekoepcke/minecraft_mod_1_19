package net.fabricmc.forestree;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;

import java.util.EnumSet;

public class FleeOnTreeGoal extends MoveToTargetPosGoal {

    private static final Block[] Leaves = new Block[]{Blocks.ACACIA_LEAVES, Blocks.AZALEA_LEAVES,
            Blocks.BIRCH_LEAVES, Blocks.OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.MANGROVE_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.FLOWERING_AZALEA_LEAVES};
    private static final Block[] Wood = new Block[]{Blocks.ACACIA_LOG, Blocks.BIRCH_LOG, Blocks.JUNGLE_LOG, Blocks.OAK_LOG, Blocks.DARK_OAK_LOG, Blocks.MANGROVE_LOG, Blocks.SPRUCE_LOG};


    public FleeOnTreeGoal(PathAwareEntity mob, double speed, int range, int maxYDifference) {
        super(mob, speed, range, maxYDifference);
        this.targetPos = BlockPos.ORIGIN;
        this.lowestY = 0;
        this.setControls(EnumSet.of(Control.MOVE, Control.JUMP));
        //TESTING
        //targetPos = new BlockPos(100, 70, 100);
    }

    private void findNearestTree(){
        /*
        Forestree.LOGGER.info("LOOKING FOR TREE");
        Forestree.LOGGER.info(String.valueOf(mob.getPos()));
        Forestree.LOGGER.info(String.valueOf(mob.getSyncedPos()));
        Forestree.LOGGER.info(String.valueOf(mob.getBlockPos()));
         */

        Forestree.LOGGER.info(String.valueOf(mob));
    }

    @Override
    public void tick(){
        if(!isTree(mob.world, targetPos)){
            findTargetPos();
        }
        super.tick();
    }

    @Override
    public boolean canStart() {
        findNearestTree();
        return !(targetPos.equals(mob.getBlockPos()));
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

    private boolean isTree(WorldView world, BlockPos pos){
        Chunk chunk = world.getChunk(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()), ChunkStatus.FULL, false);
        if (chunk == null) {
            return false;
        } else {
            Forestree.LOGGER.info(String.valueOf(pos));
            return isLeaf(pos, chunk) && isLog(pos.down(), chunk) && isLog(pos.down().down(), chunk);
        }
    }

    @Override
    protected boolean isTargetPos(WorldView world, BlockPos pos) {
        /*
        boolean temp = targetPos.equals(mob.getBlockPos());
        Forestree.LOGGER.info(String.valueOf(temp));
        return (temp);
         */
        return isTree(world, pos);
    }
}
