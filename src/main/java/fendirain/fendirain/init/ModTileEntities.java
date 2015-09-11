package fendirain.fendirain.init;

import fendirain.fendirain.common.entity.tile.TileFendiBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModTileEntities {
    public static final TileFendiBlock TILE_FENDI_BLOCK = new TileFendiBlock();

    public static void init() {
        GameRegistry.registerTileEntity(TileFendiBlock.class, "fendiBlock");
    }
}
