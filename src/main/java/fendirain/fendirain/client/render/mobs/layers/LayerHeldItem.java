package fendirain.fendirain.client.render.mobs.layers;

import fendirain.fendirain.client.models.mobs.ModelFendinainMob;
import fendirain.fendirain.common.entity.mob.EntityFendinain.EntityFendinainMob;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
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

    public void doRenderLayer(EntityLivingBase entityLivingBase, float f1, float f2, float partialTicks, float f3, float f4, float f5, float scale) {
        ItemStack itemstack = entityLivingBase.getHeldItem();
        if (itemstack != null && itemstack.getItem() != null) {
            if (entityLivingBase instanceof EntityFendinainMob) {
                if (itemstack.getItem() instanceof ItemBlock && Block.getBlockFromItem(itemstack.getItem()) == Blocks.sapling) {
                    GL11.glPushMatrix();
                    ModelFendinainMob model = (ModelFendinainMob) this.livingEntityRenderer.getMainModel();
                    model.leftArm2.postRender(-1.2F);
                    GL11.glTranslatef(-0.0365F, 0.9722F, -0.21F);
                    GL11.glScalef(0.25F, 0.25F, 0.25F);
                    GL11.glRotatef(-80.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(40.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(2.0F, 0.0F, 0.0F, 1.0F);
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
