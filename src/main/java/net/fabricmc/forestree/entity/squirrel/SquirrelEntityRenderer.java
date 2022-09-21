package net.fabricmc.forestree.entity.squirrel;

import net.fabricmc.forestree.ForestreeClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class SquirrelEntityRenderer extends MobEntityRenderer<SquirrelEntity, SquirrelEntityModel> {

    public SquirrelEntityRenderer(EntityRendererFactory.Context context){
        super(context, new SquirrelEntityModel(context.getPart(ForestreeClient.MODEL_SQUIRREL_LAYER)), 0.5f);
    }

    @Override
    public Identifier getTexture(SquirrelEntity entity) {
        return new Identifier("forestree", "textures/entity/squirrel/squirrel.png");
    }

}
