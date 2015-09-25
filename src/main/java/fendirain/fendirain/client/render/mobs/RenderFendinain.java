package fendirain.fendirain.client.render.mobs;

import fendirain.fendirain.client.models.mobs.ModelFendinainMob;
import fendirain.fendirain.client.render.mobs.layers.LayerHeldItem;
import fendirain.fendirain.reference.Reference;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderFendinain extends RendererLivingEntity {
    private final static ResourceLocation resourceLocation = new ResourceLocation(Reference.MOD_ID + ":" + "textures/mobs/fendinain.png");

    public RenderFendinain(RenderManager renderManager, ModelFendinainMob model, float f1) {
        super(renderManager, model, f1);
        this.addLayer(new LayerHeldItem(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity e) {
        return resourceLocation;
    }
}
