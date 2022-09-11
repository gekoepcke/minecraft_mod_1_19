package net.fabricmc.forestree;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

import java.util.EnumSet;

public class FleeOnTreeGoal extends MoveToTargetPosGoal {

    public FleeOnTreeGoal(PathAwareEntity mob, double speed, int range, int maxYDifference) {
        super(mob, speed, range, maxYDifference);
        this.targetPos = BlockPos.ORIGIN;
        this.lowestY = 0;
        this.setControls(EnumSet.of(Control.MOVE, Control.JUMP));
        //TESTING
        targetPos = new BlockPos(100, 70, 100);
    }

    private void findNearestTree(){
        /*
        Forestree.LOGGER.info("LOOKING FOR TREE");
        Forestree.LOGGER.info(String.valueOf(mob.getPos()));
        Forestree.LOGGER.info(String.valueOf(mob.getSyncedPos()));
        Forestree.LOGGER.info(String.valueOf(mob.getBlockPos()));
         */
    }

    @Override
    public boolean canStart() {
        findNearestTree();
        return !(targetPos.equals(mob.getBlockPos()));
    }

    @Override
    protected boolean isTargetPos(WorldView world, BlockPos pos) {
        boolean temp = targetPos.equals(mob.getBlockPos());
        Forestree.LOGGER.info(String.valueOf(temp));
        return (temp);
    }
}
