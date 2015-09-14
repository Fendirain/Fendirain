package fendirain.fendirain.client.render.mobs;

import fendirain.fendirain.client.models.mobs.ModelFendinainMob;
import fendirain.fendirain.reference.Reference;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderFendinain extends RenderLiving {

    private final static ResourceLocation resourceLocation = new ResourceLocation(Reference.MOD_ID.toLowerCase() + ":" + "textures/mobs/fendinain.png");
    private final ModelFendinainMob model;

    public RenderFendinain(ModelFendinainMob model, float float1) {
        super(model, float1);
        this.model = model;
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity e) {
        return resourceLocation;
    }

    @Override
    public void renderEquippedItems(EntityLivingBase entity, float f) {
        ItemStack itemstack = entity.getHeldItem();
        if (itemstack != null && itemstack.getItem() != null && itemstack.getItem() instanceof ItemBlock && Block.getBlockFromItem(itemstack.getItem()) == Blocks.sapling) {
            GL11.glPushMatrix();
            this.model.leftArm2.postRender(-1.2F);
            GL11.glTranslatef(-0.0365F, 0.9722F, -0.21F);
            GL11.glScalef(0.25F, 0.25F, 0.25F);
            GL11.glRotatef(-80.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(40.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(2.0F, 0.0F, 0.0F, 1.0F);
            this.renderManager.itemRenderer.renderItem(entity, itemstack, 0);
            GL11.glPopMatrix();
        }
    }
}
