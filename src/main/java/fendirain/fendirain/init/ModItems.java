package fendirain.fendirain.init;

import fendirain.fendirain.common.item.ItemFendiPiece;
import fendirain.fendirain.common.item.ItemFendirain;
import fendirain.fendirain.common.item.ItemTreeClearer;
import fendirain.fendirain.reference.ConfigValues;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModItems {

    public static final ItemFendirain fendiPiece = new ItemFendiPiece();
    public static final ItemTreeClearer treeClearer = new ItemTreeClearer();

    public static void init() {
        GameRegistry.registerItem(fendiPiece, "fendiPiece");
        if (ConfigValues.isDebugSettingsEnabled) {
            GameRegistry.registerItem(treeClearer, "treeClearer");
        }
    }
}