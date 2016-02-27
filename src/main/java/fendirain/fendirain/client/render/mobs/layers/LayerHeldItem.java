package fendirain.fendirain.client.render.mobs.layers;

import fendirain.fendirain.client.models.mobs.ModelFenderiumMob;
import fendirain.fendirain.client.models.mobs.ModelFendinainMob;
import fendirain.fendirain.common.entity.mob.EntityFenderium.EntityFenderiumMob;
import fendirain.fendirain.common.entity.mob.EntityFendinain.EntityFendinainMob;
import fendirain.fendirain.common.item.ItemFenderiumAxe;
import fendirain.fendirain.init.ModCompatibility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerHeldItem implements LayerRenderer {
    private final RendererLivingEntity livingEntityRenderer;

    public LayerHeldItem(RendererLivingEntity livingEntityRendererIn) {
        this.livingEntityRenderer = livingEntityRendererIn;
    }

    public void doRenderLayer(EntityLivingBase entityLivingBase, float f1, float f2, float partialTicks, float f3, float f4, float f5, float scale) {
        ItemStack itemStack = entityLivingBase.getHeldItem();
        if (itemStack != null && itemStack.getItem() != null) {
            if (entityLivingBase instanceof EntityFendinainMob) {
                if (itemStack.getItem() instanceof ItemBlock && ModCompatibility.saplings.contains(itemStack.getItem())) {
                    GlStateManager.pushMatrix();
                    ModelFendinainMob model = (ModelFendinainMob) this.livingEntityRenderer.getMainModel();
                    model.leftArm2.postRender(-1.2F);
                    GlStateManager.translate(-0.11F, 1.18F, -0.17F);
                    GlStateManager.scale(0.5F, 0.5F, 0.5F);
                    GlStateManager.rotate(-75.0F, 1.0F, 0.0F, 0.0F);
                    GlStateManager.rotate(0.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
                    //noinspection deprecation
                    Minecraft.getMinecraft().getItemRenderer().renderItem(entityLivingBase, itemStack, ItemCameraTransforms.TransformType.THIRD_PERSON);
                    GlStateManager.popMatrix();
                }
            } else if (entityLivingBase instanceof EntityFenderiumMob && itemStack.getItem() instanceof ItemFenderiumAxe) {
                GlStateManager.pushMatrix();
                ModelFenderiumMob model = (ModelFenderiumMob) this.livingEntityRenderer.getMainModel();
                model.rightArm1.postRender(0F);
                GlStateManager.translate(.275F, 1.1F, .0F);
                GlStateManager.scale(.65F, .45F, .65F);
                GlStateManager.rotate(80F, 1.0F, 0.0F, 0.0F);
                //noinspection deprecation
                Minecraft.getMinecraft().getItemRenderer().renderItem(entityLivingBase, itemStack, ItemCameraTransforms.TransformType.THIRD_PERSON);
                GlStateManager.popMatrix();
            }
        }
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}
