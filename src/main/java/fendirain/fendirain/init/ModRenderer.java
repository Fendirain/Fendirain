package fendirain.fendirain.init;

import fendirain.fendirain.client.models.blocks.ModelFendiBlock;
import fendirain.fendirain.client.render.RenderBlock;
import fendirain.fendirain.client.render.RenderTileEntityAsItem;
import fendirain.fendirain.common.entity.tile.TileFendiBlock;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ModRenderer {
    public static final TileEntitySpecialRenderer renderFendiBlock = new RenderBlock(new ModelFendiBlock(), "textures/models/modelFendiBlock.png");

    public static void init() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileFendiBlock.class, renderFendiBlock);
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.blockFendi), new RenderTileEntityAsItem(renderFendiBlock, ModTileEntities.TILE_FENDI_BLOCK));
    }
}
