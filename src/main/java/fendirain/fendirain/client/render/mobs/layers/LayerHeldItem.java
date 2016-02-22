package fendirain.fendirain.client.render.mobs.layers;

import fendirain.fendirain.client.models.mobs.ModelFendinainMob;
import fendirain.fendirain.common.entity.mob.EntityFendinain.EntityFendinainMob;
import fendirain.fendirain.init.ModCompatibility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class LayerHeldItem implements LayerRenderer {
    private final RendererLivingEntity livingEntityRenderer;

    public LayerHeldItem(RendererLivingEntity livingEntityRendererIn) {
        this.livingEntityRenderer = livingEntityRendererIn;
    }

    @SuppressWarnings("deprecation")
    public void doRenderLayer(EntityLivingBase entityLivingBase, float f1, float f2, float partialTicks, float f3, float f4, float f5, float scale) {
        ItemStack itemstack = entityLivingBase.getHeldItem();
        if (itemstack != null && itemstack.getItem() != null) {
            if (entityLivingBase instanceof EntityFendinainMob) {
                if (itemstack.getItem() instanceof ItemBlock && ModCompatibility.saplings.contains(itemstack.getItem())) {
                    GL11.glPushMatrix();
                    ModelFendinainMob model = (ModelFendinainMob) this.livingEntityRenderer.getMainModel();
                    model.leftArm2.postRender(-1.2F);
                    GL11.glTranslatef(-0.11F, 1.18F, -0.17F);
                    GL11.glScalef(0.5F, 0.5F, 0.5F);
                    GL11.glRotatef(-75.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(0.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
                    Minecraft.getMinecraft().getItemRenderer().renderItem(entityLivingBase, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON);
                    GL11.glPopMatrix();
                }
            } /*else if (entityLivingBase instanceof EntityFenderiumMob) {
                // Nothing currently
            }*/
        }
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}
