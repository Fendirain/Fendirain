package fendirain.fendirain.client.render.mobs.layers;

import fendirain.fendirain.client.models.mobs.ModelFenderiumMob;
import fendirain.fendirain.client.models.mobs.ModelFendinainMob;
import fendirain.fendirain.common.entity.mob.EntityFenderium.EntityFenderiumMob;
import fendirain.fendirain.common.entity.mob.EntityFendinain.EntityFendinainMob;
import fendirain.fendirain.common.item.ItemFenderiumAxe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerHeldItem implements LayerRenderer<EntityLivingBase> {
    private final RenderLivingBase livingEntityRenderer;

    public LayerHeldItem(RenderLivingBase livingEntityRendererIn) {
        this.livingEntityRenderer = livingEntityRendererIn;
    }

    public void doRenderLayer(EntityLivingBase entityLivingBase, float f1, float f2, float partialTicks, float f3, float f4, float f5, float scale) {
        ItemStack itemStack = entityLivingBase.getHeldItem(EnumHand.MAIN_HAND);
        if (entityLivingBase instanceof EntityFendinainMob) {
            if (((EntityFendinainMob) entityLivingBase).isItemValidForEntity(itemStack.getItem())) {
                GlStateManager.pushMatrix();
                ModelFendinainMob model = (ModelFendinainMob) this.livingEntityRenderer.getMainModel();
                model.rightArm2.postRender(-1.2F);
                GlStateManager.translate(-.08F, 1.06F, -0.17F);
                // GlStateManager.translate(-.055F, 1.12F, -0.22F);
                GlStateManager.scale(0.5F, 0.5F, 0.5F);
                //GlStateManager.rotate(-66.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(190.0F, 1.0F, 0.0F, 1.0F);
                GlStateManager.rotate(-15.0F, 1.0F, 0.0F, 1.0F);
                GlStateManager.rotate(20.0F, 0.0F, 0.0F, 1.0F);
                //noinspection deprecation
                Minecraft.getMinecraft().getItemRenderer().renderItem(entityLivingBase, itemStack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND);
                GlStateManager.popMatrix();
            }
        } else if (entityLivingBase instanceof EntityFenderiumMob && itemStack.getItem() instanceof ItemFenderiumAxe) {
            GlStateManager.pushMatrix();
            ModelFenderiumMob model = (ModelFenderiumMob) this.livingEntityRenderer.getMainModel();
            model.leftArm2.postRender(0F);
            /*if (((EntityFenderiumMob) entityLivingBase).isCurrentlyChopping()) // TODO Fix arm animation - HA, Like that will happen
                GlStateManager.translate(.0F, -1.5F, .0F);*/
            //GlStateManager.translate(.27F, 1F, -.2F);
            GlStateManager.translate(.27F, 1F, -.2F);
            GlStateManager.scale(.45F, .45F, .45F);
            //GlStateManager.translate(0F, -2F, 0F);
            // GlStateManager.rotate(0F, 1.0F, 0.0F, 0.0F);
            //noinspection deprecation
            Minecraft.getMinecraft().getItemRenderer().renderItem(entityLivingBase, itemStack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND);
            GlStateManager.popMatrix();
        }
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}
