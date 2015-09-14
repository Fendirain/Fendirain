package fendirain.fendirain.init;

import cpw.mods.fml.common.registry.GameRegistry;
import fendirain.fendirain.common.block.BlockFendi;
import fendirain.fendirain.common.block.BlockFendirain;
import fendirain.fendirain.common.block.BlockOreFendi;

public class ModBlocks {

    public static final BlockFendirain blockOreFendi = new BlockOreFendi();
    public static final BlockFendi blockFendi = new BlockFendi();

    public static void init() {
        GameRegistry.registerBlock(blockFendi, "blockFendi");
        GameRegistry.registerBlock(blockOreFendi, "blockOreFendi");
    }
}