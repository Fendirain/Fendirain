package fendirain.fendirain.init;

import cpw.mods.fml.client.registry.ClientRegistry;
import fendirain.fendirain.client.models.ModelFendiBlock;
import fendirain.fendirain.client.render.RenderBlock;
import fendirain.fendirain.entity.tile.TileFendiBlock;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class ModRenderer {
    public static final TileEntitySpecialRenderer renderFendiBlock = new RenderBlock(new ModelFendiBlock(), "textures/models/modelFendiBlock.png");

    public static void init() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileFendiBlock.class, renderFendiBlock);
    }
}
