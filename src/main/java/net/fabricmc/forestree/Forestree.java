package net.fabricmc.forestree;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Forestree implements ModInitializer {

	public static final String MOD_ID = "forestree";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final EntityType<SquirrelEntity> SQUIRREL = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier(MOD_ID, "squirrel"),
			FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, SquirrelEntity::new).dimensions(EntityDimensions.fixed(0.75f, 0.75f)).build()
	);

	@Override
	public void onInitialize() {
		FabricDefaultAttributeRegistry.register(SQUIRREL, SquirrelEntity.createSquirrelAttributes());


		LOGGER.info("Mod running");
	}
}
