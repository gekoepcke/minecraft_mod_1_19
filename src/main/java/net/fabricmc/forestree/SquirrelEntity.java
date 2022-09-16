package net.fabricmc.forestree;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SquirrelEntity extends AnimalEntity {
    private boolean isClimbingWall;



    private static final double MAX_HEALTH = 5.0;
    private static final double MOVEMENT_SPEED = 0.25;

    public SquirrelEntity(EntityType<? extends SquirrelEntity> entityType, World world) {
        super(entityType, world);
        isClimbingWall = false;
    }

    protected void initGoals() {

        Forestree.LOGGER.info("INIT GOALS");
        this.goalSelector.add(0, new SwimGoal(this));
        //this.goalSelector.add(1, new EscapeDangerGoal(this, 2.0));
        this.goalSelector.add(1, new FleeOnTreeGoal(this, 2.0, 10, 10));

    }

    public static DefaultAttributeContainer.Builder createSquirrelAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, MAX_HEALTH).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, MOVEMENT_SPEED);
    }

    protected EntityNavigation createNavigation(World world) {
        return new SquirrelNavigation(this, world);
    }

    public boolean isClimbingWall() {
        return isClimbingWall;
    }

    public boolean isClimbing() {
        return this.isClimbingWall();
    }


    public void setClimbingWall(boolean climbing) {
        this.isClimbingWall = climbing;
    }
    public void tick() {
        super.tick();
        if (!this.world.isClient) {
            this.setClimbingWall(this.horizontalCollision);
        }
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        //TODO: find out what this method is supposed to do(I suspect its to create offspring) and implement it
        return null;
    }
}
