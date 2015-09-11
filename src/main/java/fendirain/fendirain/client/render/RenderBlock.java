package fendirain.fendirain.client.render;

import fendirain.fendirain.client.models.blocks.ModelFendirain;
import fendirain.fendirain.reference.Reference;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderBlock extends TileEntitySpecialRenderer {

    private final ResourceLocation texture;
    private final ModelFendirain model;

    public RenderBlock(ModelFendirain modelBase, String texture) {
        this.model = modelBase;
        this.texture = new ResourceLocation(Reference.MOD_ID.toLowerCase() + ":" + texture);
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float var2, int var3) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GL11.glRotatef(180, 0F, 0F, 1F);

        this.bindTexture(texture);

        GL11.glPushMatrix();
        this.model.render(0.0625F);

        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }
}
