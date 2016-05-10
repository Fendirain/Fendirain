package fendirain.fendirain.init;

import fendirain.fendirain.common.block.BlockFendi;
import fendirain.fendirain.common.block.BlockFendirain;
import fendirain.fendirain.common.block.BlockOreFendi;

public class ModBlocks {

    public static BlockFendirain blockOreFendi;
    public static BlockFendi blockFendi;

    public static void init() {
        blockOreFendi = new BlockOreFendi();
        blockFendi = new BlockFendi();
    }
}
