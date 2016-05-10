package fendirain.fendirain.client.render.mobs;

import fendirain.fendirain.client.models.mobs.ModelFenderiumMob;
import fendirain.fendirain.client.render.mobs.layers.LayerHeldItem;
import fendirain.fendirain.common.entity.mob.EntityFenderium.EntityFenderiumMob;
import fendirain.fendirain.reference.Reference;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderFenderium extends RenderLivingBase<EntityFenderiumMob> {
    private final static ResourceLocation resourceLocation = new ResourceLocation(Reference.MOD_ID, "textures/mobs/fenderium.png");

    public RenderFenderium(RenderManager renderManager) {
        super(renderManager, new ModelFenderiumMob(), 0.35f);
        //noinspection unchecked
        this.addLayer(new LayerHeldItem(this));
    }

    @Override
    public ResourceLocation getEntityTexture(EntityFenderiumMob entity) {
        return resourceLocation;
    }

    public boolean canRenderName(EntityFenderiumMob entity) {
        return super.canRenderName(entity) && (entity.getAlwaysRenderNameTagForRender() || entity.hasCustomName() && entity == this.renderManager.pointedEntity);
    }

    @Override
    public void transformHeldFull3DItemLayer() {
        GlStateManager.translate(0.0F, 0F, 0.0F);
    }
}
