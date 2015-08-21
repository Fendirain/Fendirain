package fendirain.fendirain.init;

import cpw.mods.fml.common.registry.GameRegistry;
import fendirain.fendirain.entity.tile.TileFendiBlock;

public class ModTileEntities {
    public static final TileFendiBlock TILE_FENDI_BLOCK = new TileFendiBlock();

    public static void init() {
        GameRegistry.registerTileEntity(TileFendiBlock.class, "fendiBlock");
    }
}
