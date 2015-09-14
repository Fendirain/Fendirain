package fendirain.fendirain.init;

import cpw.mods.fml.common.registry.GameRegistry;
import fendirain.fendirain.common.block.BlockFendi;
import fendirain.fendirain.common.block.BlockFendiOre;
import fendirain.fendirain.common.block.BlockFendirain;

public class ModBlocks {

    public static final BlockFendirain blockOreFendi = new BlockFendiOre();
    public static final BlockFendi blockFendi = new BlockFendi();

    public static void init() {
        GameRegistry.registerBlock(blockOreFendi, "blockOreFendi");
        GameRegistry.registerBlock(blockFendi, "blockFendi");
    }
}