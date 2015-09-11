package fendirain.fendirain.init;

import fendirain.fendirain.common.block.BlockFendi;
import fendirain.fendirain.common.block.BlockFendiOre;
import fendirain.fendirain.common.block.BlockFendirain;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModBlocks {

    public static final BlockFendirain fendiOre = new BlockFendiOre();
    public static final BlockFendi blockFendi = new BlockFendi();

    public static void init() {
        GameRegistry.registerBlock(fendiOre, "fendiOre");
        GameRegistry.registerBlock(blockFendi, "fendiBlock");
    }
}