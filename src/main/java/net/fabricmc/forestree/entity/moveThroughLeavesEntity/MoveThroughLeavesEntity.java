package net.fabricmc.forestree.entity.moveThroughLeavesEntity;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MoveThroughLeavesEntity extends PathAwareEntity {
    // einzige geänderte methode ist: adjustMovementForCollisions(@Nullable Entity entity, Vec3d movement, Box entityBoundingBox, World world, List<VoxelShape> collisions)
    // rest ist kopiert aufgrund von privaten und statischen methoden die nicht geerbt werden können

    private float nextStepSoundDistance;
    private int lastChimeAge;
    private float lastChimeIntensity;


    protected MoveThroughLeavesEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }


    private void playAmethystChimeSound(BlockState state) {  // copied private method from Entity
        if (state.isIn(BlockTags.CRYSTAL_SOUND_BLOCKS) && this.age >= this.lastChimeAge + 20) {
            this.lastChimeIntensity *= (float) Math.pow(0.997, this.age - this.lastChimeAge);
            this.lastChimeIntensity = Math.min(1.0F, this.lastChimeIntensity + 0.07F);
            float f = 0.5F + this.lastChimeIntensity * this.random.nextFloat() * 1.2F;
            float g = 0.1F + this.lastChimeIntensity * 1.2F;
            this.playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, g, f);
            this.lastChimeAge = this.age;
        }

    }

    private Vec3d adjustMovementForCollisions(Vec3d movement) {  // copied private method from Entity
        Box box = this.getBoundingBox();
        List<VoxelShape> list = this.world.getEntityCollisions(this, box.stretch(movement));
        Vec3d vec3d = movement.lengthSquared() == 0.0 ? movement : adjustMovementForCollisions(this, movement, box, this.world, list);
        boolean bl = movement.x != vec3d.x;
        boolean bl2 = movement.y != vec3d.y;
        boolean bl3 = movement.z != vec3d.z;
        boolean bl4 = this.onGround || bl2 && movement.y < 0.0;
        if (this.stepHeight > 0.0F && bl4 && (bl || bl3)) {
            Vec3d vec3d2 = adjustMovementForCollisions(this, new Vec3d(movement.x, this.stepHeight, movement.z), box, this.world, list);
            Vec3d vec3d3 = adjustMovementForCollisions(this, new Vec3d(0.0, this.stepHeight, 0.0), box.stretch(movement.x, 0.0, movement.z), this.world, list);
            if (vec3d3.y < (double) this.stepHeight) {
                Vec3d vec3d4 = adjustMovementForCollisions(this, new Vec3d(movement.x, 0.0, movement.z), box.offset(vec3d3), this.world, list).add(vec3d3);
                if (vec3d4.horizontalLengthSquared() > vec3d2.horizontalLengthSquared()) {
                    vec3d2 = vec3d4;
                }
            }

            if (vec3d2.horizontalLengthSquared() > vec3d.horizontalLengthSquared()) {
                return vec3d2.add(adjustMovementForCollisions(this, new Vec3d(0.0, -vec3d2.y + movement.y, 0.0), box.offset(vec3d2), this.world, list));
            }
        }

        return vec3d;
    }

    public static Vec3d adjustMovementForCollisions(@Nullable Entity entity, Vec3d movement, Box entityBoundingBox, World world, List<VoxelShape> collisions) {
        ImmutableList.Builder<VoxelShape> builder = ImmutableList.builderWithExpectedSize(collisions.size() + 1);
        if (!collisions.isEmpty()) {
            builder.addAll(collisions);
        }

        WorldBorder worldBorder = world.getWorldBorder();
        boolean bl = entity != null && worldBorder.canCollide(entity, entityBoundingBox.stretch(movement));
        if (bl) {
            builder.add(worldBorder.asVoxelShape());
        }

        // replaced: builder.addAll(world.getBlockCollisions(entity, entityBoundingBox.stretch(movement)));
        builder.addAll(new MoveThroughLeavesBlockCollisionSpliterator(world, entity, entityBoundingBox.stretch(movement)));

        return adjustMovementForCollisions(movement, entityBoundingBox, builder.build());
    }

    private static Vec3d adjustMovementForCollisions(Vec3d movement, Box entityBoundingBox, List<VoxelShape> collisions) {
        if (collisions.isEmpty()) {
            return movement;
        } else {
            double d = movement.x;
            double e = movement.y;
            double f = movement.z;
            if (e != 0.0) {
                e = VoxelShapes.calculateMaxOffset(Direction.Axis.Y, entityBoundingBox, collisions, e);
                if (e != 0.0) {
                    entityBoundingBox = entityBoundingBox.offset(0.0, e, 0.0);
                }
            }

            boolean bl = Math.abs(d) < Math.abs(f);
            if (bl && f != 0.0) {
                f = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, entityBoundingBox, collisions, f);
                if (f != 0.0) {
                    entityBoundingBox = entityBoundingBox.offset(0.0, 0.0, f);
                }
            }

            if (d != 0.0) {
                d = VoxelShapes.calculateMaxOffset(Direction.Axis.X, entityBoundingBox, collisions, d);
                if (!bl && d != 0.0) {
                    entityBoundingBox = entityBoundingBox.offset(d, 0.0, 0.0);
                }
            }

            if (!bl && f != 0.0) {
                f = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, entityBoundingBox, collisions, f);
            }

            return new Vec3d(d, e, f);
        }
    }


    @Override
    public void move(MovementType movementType, Vec3d movement) {
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
                    this.speed += (float) (vec3d.length() * 0.6);
                    boolean bl3 = blockState.isIn(BlockTags.CLIMBABLE) || blockState.isOf(Blocks.POWDER_SNOW);
                    if (!bl3) {
                        f = 0.0;
                    }

                    this.horizontalSpeed += (float) vec3d.horizontalLength() * 0.6F;
                    this.distanceTraveled += (float) Math.sqrt(e * e + f * f + g * g) * 0.6F;
                    if (this.distanceTraveled > this.nextStepSoundDistance && !blockState.isAir()) {
                        this.nextStepSoundDistance = this.calculateNextStepSoundDistance();
                        if (this.isTouchingWater()) {
                            if (moveEffect.playsSounds()) {
                                Entity entity = this.hasPassengers() && this.getPrimaryPassenger() != null ? this.getPrimaryPassenger() : this;
                                float h = entity == this ? 0.35F : 0.4F;
                                Vec3d vec3d3 = entity.getVelocity();
                                float i = Math.min(1.0F, (float) Math.sqrt(vec3d3.x * vec3d3.x * 0.20000000298023224 + vec3d3.y * vec3d3.y + vec3d3.z * vec3d3.z * 0.20000000298023224) * h);
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
                                this.world.emitGameEvent(GameEvent.STEP, this.getPos(), GameEvent.Emitter.of(this, this.getSteppingBlockState()));
                            }
                        }
                    } else if (blockState.isAir()) {
                        this.addAirTravelEffects();
                    }
                }

                this.tryCheckBlockCollision();
                float j = this.getVelocityMultiplier();
                this.setVelocity(this.getVelocity().multiply(j, 1.0, j));
                if (this.world.getStatesInBoxIfLoaded(this.getBoundingBox().contract(1.0E-6)).noneMatch((state) -> state.isIn(BlockTags.FIRE) || state.isOf(Blocks.LAVA))) {
                    if (this.getFireTicks() <= 0) {
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

}
