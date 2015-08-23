package fendirain.fendirain.client.render;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class RenderTileEntityAsItem implements IItemRenderer {
    TileEntitySpecialRenderer render;

    private TileEntity entity;

    public RenderTileEntityAsItem(TileEntitySpecialRenderer render, TileEntity entity) {
        this.render = render;
        this.entity = entity;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        switch (type) {
            case ENTITY:
                GL11.glTranslatef(0.0F, 0.0F, 0.0F);
                break;
            case EQUIPPED:
                GL11.glTranslatef(0.0F, 0.0F, 0.0F);
                break;
            case EQUIPPED_FIRST_PERSON:
                GL11.glTranslatef(0.0F, 0.2F, 0.0F);
                break;
            case INVENTORY:
                GL11.glTranslatef(0.0F, -0.1F, 0.0F);
                break;
            default:
                GL11.glTranslatef(0.0F, 0.0F, 0.0F);
                break;
        }
        TileEntityRendererDispatcher.instance.renderTileEntityAt(entity, 0.0D, 0.0D, 0.0D, 0.0F);
        GL11.glPopMatrix();
    }
}
