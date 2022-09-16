package net.fabricmc.forestree;

import net.minecraft.block.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
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
        this.goalSelector.add(1, new FleeOnTreeGoal(this, 2.0, 10, 10));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1, 0));

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

    @Override
    public void tickMovement() {
        /*breeding + happy particles
        item pickup

        */
        super.tickMovement();
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        //TODO: find out what this method is supposed to do(I suspect its to create offspring) and implement it
        return null;
    }

    @Override
    public void move(MovementType movementType, Vec3d movement) {
        this.noClip = this.world.getBlockState(this.getBlockPos().up()).getMaterial() == Material.LEAVES;
        super.move(movementType, movement);
    }

    /*    public void move(MovementType movementType, Vec3d movement) {
        if (this.noClip) {
            this.setPosition(this.getX() + movement.x, this.getY() + movement.y, this.getZ() + movement.z);
        } else {
            this.wasOnFire = this.isOnFire();
            if (movementType == MovementType.PISTON) {
                movement = this.adjustMovementForPiston(movement);
                if (movement.equals(Vec3d.ZERO)) {
                    return;
                }
            }

            this.world.getProfiler().push("move");
            if (this.movementMultiplier.lengthSquared() > 1.0E-7) {
                movement = movement.multiply(this.movementMultiplier);
                this.movementMultiplier = Vec3d.ZERO;
                this.setVelocity(Vec3d.ZERO);
            }

            movement = this.adjustMovementForSneaking(movement, movementType);
            Vec3d vec3d = this.adjustMovementForCollisions(movement);
            double d = vec3d.lengthSquared();
            if (d > 1.0E-7) {
                if (this.fallDistance != 0.0F && d >= 1.0) {
                    BlockHitResult blockHitResult = this.world.raycast(new RaycastContext(this.getPos(), this.getPos().add(vec3d), RaycastContext.ShapeType.FALLDAMAGE_RESETTING, RaycastContext.FluidHandling.WATER, this));
                    if (blockHitResult.getType() != HitResult.Type.MISS) {
                        this.onLanding();
                    }
                }

                this.setPosition(this.getX() + vec3d.x, this.getY() + vec3d.y, this.getZ() + vec3d.z);
            }

            this.world.getProfiler().pop();
            this.world.getProfiler().push("rest");
            boolean bl = !MathHelper.approximatelyEquals(movement.x, vec3d.x);
            boolean bl2 = !MathHelper.approximatelyEquals(movement.z, vec3d.z);
            this.horizontalCollision = bl || bl2;
            this.verticalCollision = movement.y != vec3d.y;
            this.field_36331 = this.verticalCollision && movement.y < 0.0;
            if (this.horizontalCollision) {
                this.collidedSoftly = this.hasCollidedSoftly(vec3d);
            } else {
                this.collidedSoftly = false;
            }

            this.onGround = this.verticalCollision && movement.y < 0.0;
            BlockPos blockPos = this.getLandingPos();
            BlockState blockState = this.world.getBlockState(blockPos);
            this.fall(vec3d.y, this.onGround, blockState, blockPos);
            if (this.isRemoved()) {
                this.world.getProfiler().pop();
            } else {
                if (this.horizontalCollision) {
                    Vec3d vec3d2 = this.getVelocity();
                    this.setVelocity(bl ? 0.0 : vec3d2.x, vec3d2.y, bl2 ? 0.0 : vec3d2.z);
                }

                Block block = blockState.getBlock();
                if (movement.y != vec3d.y) {
                    block.onEntityLand(this.world, this);
                }

                if (this.onGround) {
                    block.onSteppedOn(this.world, blockPos, blockState, this);
                }

                MoveEffect moveEffect = this.getMoveEffect();
                if (moveEffect.hasAny() && !this.hasVehicle()) {
                    double e = vec3d.x;
                    double f = vec3d.y;
                    double g = vec3d.z;
                    this.speed += (float)(vec3d.length() * 0.6);
                    boolean bl3 = blockState.isIn(BlockTags.CLIMBABLE) || blockState.isOf(Blocks.POWDER_SNOW);
                    if (!bl3) {
                        f = 0.0;
                    }

                    this.horizontalSpeed += (float)vec3d.horizontalLength() * 0.6F;
                    this.distanceTraveled += (float)Math.sqrt(e * e + f * f + g * g) * 0.6F;
                    if (this.distanceTraveled > this.nextStepSoundDistance && !blockState.isAir()) {
                        this.nextStepSoundDistance = this.calculateNextStepSoundDistance();
                        if (this.isTouchingWater()) {
                            if (moveEffect.playsSounds()) {
                                Entity entity = this.hasPassengers() && this.getPrimaryPassenger() != null ? this.getPrimaryPassenger() : this;
                                float h = entity == this ? 0.35F : 0.4F;
                                Vec3d vec3d3 = entity.getVelocity();
                                float i = Math.min(1.0F, (float)Math.sqrt(vec3d3.x * vec3d3.x * 0.20000000298023224 + vec3d3.y * vec3d3.y + vec3d3.z * vec3d3.z * 0.20000000298023224) * h);
                                this.playSwimSound(i);
                            }

                            if (moveEffect.emitsGameEvents()) {
                                this.emitGameEvent(GameEvent.SWIM);
                            }
                        } else {
                            if (moveEffect.playsSounds()) {
                                this.playAmethystChimeSound(blockState);
                                this.playStepSound(blockPos, blockState);
                            }

                            if (moveEffect.emitsGameEvents() && (this.onGround || movement.y == 0.0 || this.inPowderSnow || bl3)) {
                                this.world.emitGameEvent(GameEvent.STEP, this.pos, GameEvent.Emitter.of(this, this.getSteppingBlockState()));
                            }
                        }
                    } else if (blockState.isAir()) {
                        this.addAirTravelEffects();
                    }
                }

                this.tryCheckBlockCollision();
                float j = this.getVelocityMultiplier();
                this.setVelocity(this.getVelocity().multiply((double)j, 1.0, (double)j));
                if (this.world.getStatesInBoxIfLoaded(this.getBoundingBox().contract(1.0E-6)).noneMatch((state) -> {
                    return state.isIn(BlockTags.FIRE) || state.isOf(Blocks.LAVA);
                })) {
                    if (this.fireTicks <= 0) {
                        this.setFireTicks(-this.getBurningDuration());
                    }

                    if (this.wasOnFire && (this.inPowderSnow || this.isWet())) {
                        this.playExtinguishSound();
                    }
                }

                if (this.isOnFire() && (this.inPowderSnow || this.isWet())) {
                    this.setFireTicks(-this.getBurningDuration());
                }

                this.world.getProfiler().pop();
            }
        }
    }
*/

}
