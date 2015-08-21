package fendirain.fendirain.init;

import cpw.mods.fml.common.registry.GameRegistry;
import fendirain.fendirain.block.BlockFendi;
import fendirain.fendirain.block.BlockFendiOre;
import fendirain.fendirain.block.BlockFendirain;

public class ModBlocks {

    public static final BlockFendirain fendiOre = new BlockFendiOre();
    public static final BlockFendi blockFendi = new BlockFendi();

    public static void init() {
        GameRegistry.registerBlock(fendiOre, "fendiOre");
        GameRegistry.registerBlock(blockFendi, "fendiBlock");
    }
}