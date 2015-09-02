package fendirain.fendirain.client.render.mobs;

import fendirain.fendirain.client.models.mobs.ModelFenderiumMob;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class RenderFenderium extends RenderLiving {
    private ResourceLocation resourceLocation;
    private ModelFenderiumMob model;

    public RenderFenderium(ModelFenderiumMob model, float float1, ResourceLocation resourceLocation) {
        super(model, float1);
        this.resourceLocation = resourceLocation;
        this.model = model;
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity e) {
        return resourceLocation;
    }

    @Override
    public void renderEquippedItems(EntityLivingBase entity, float f) {
        super.renderEquippedItems(entity, f);
    }
}
