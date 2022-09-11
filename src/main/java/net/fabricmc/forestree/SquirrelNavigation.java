package net.fabricmc.forestree;

import net.minecraft.entity.ai.pathing.SpiderNavigation;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;

public class SquirrelNavigation extends SpiderNavigation {
    public SquirrelNavigation(MobEntity mobEntity, World world) {
        super(mobEntity, world);
    }
}
