package fendirain.fendirain.client.render.mobs;

import fendirain.fendirain.client.models.mobs.ModelFendinainMob;
import fendirain.fendirain.client.render.mobs.layers.LayerHeldItem;
import fendirain.fendirain.common.entity.mob.EntityFendinain.EntityFendinainMob;
import fendirain.fendirain.reference.Reference;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderFendinain extends RendererLivingEntity<EntityFendinainMob> {
    private final static ResourceLocation resourceLocation = new ResourceLocation(Reference.MOD_ID, "textures/mobs/fendinain.png");

    public RenderFendinain(RenderManager renderManager) {
        super(renderManager, new ModelFendinainMob(), 0.21f);
        this.addLayer(new LayerHeldItem(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityFendinainMob entity) {
        return resourceLocation;
    }

    protected boolean canRenderName(EntityFendinainMob entity) {
        return super.canRenderName(entity) && (entity.getAlwaysRenderNameTagForRender() || entity.hasCustomName() && entity == this.renderManager.pointedEntity);
    }
}
