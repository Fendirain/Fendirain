package fendirain.fendirain.client.render.mobs;

import fendirain.fendirain.client.models.mobs.ModelFenderiumMob;
import fendirain.fendirain.reference.Reference;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class RenderFenderium extends RenderLiving {

    private final static ResourceLocation resourceLocation = new ResourceLocation(Reference.MOD_ID.toLowerCase() + ":" + "textures/mobs/fenderium.png");
    private final ModelFenderiumMob model;

    public RenderFenderium(ModelFenderiumMob model, float float1) {
        super(model, float1);
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
