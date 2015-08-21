package fendirain.fendirain.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderMob extends RenderLiving {

    private ResourceLocation resourceLocation;

    public RenderMob(ModelBase modelBase, float float1, ResourceLocation resourceLocation) {
        super(modelBase, float1);
        this.resourceLocation = resourceLocation;
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity e) {
        return resourceLocation;
    }
}
