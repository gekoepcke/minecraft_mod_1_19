package net.fabricmc.forestree;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ForestreeClient implements ClientModInitializer {

    public static final EntityModelLayer MODEL_SQUIRREL_LAYER = new EntityModelLayer(new Identifier(Forestree.MOD_ID, "squirrel"), "main");


    @Override
    public void onInitializeClient() {

        EntityRendererRegistry.register(Forestree.SQUIRREL, SquirrelEntityRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(MODEL_SQUIRREL_LAYER, SquirrelEntityModel::getTexturedModelData);
    }
}
