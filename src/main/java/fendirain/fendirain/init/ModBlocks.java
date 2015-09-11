package fendirain.fendirain.init;

import fendirain.fendirain.common.block.BlockFendi;
import fendirain.fendirain.common.block.BlockFendirain;
import fendirain.fendirain.common.block.BlockOreFendi;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModBlocks {

    public static final BlockFendirain blockOreFendi = new BlockOreFendi();
    public static final BlockFendi blockFendi = new BlockFendi();

    public static void init() {
        GameRegistry.registerBlock(blockFendi, "blockFendi");
        GameRegistry.registerBlock(blockOreFendi, "blockOreFendi");
    }
}