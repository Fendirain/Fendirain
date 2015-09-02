package fendirain.fendirain.init;

import cpw.mods.fml.common.registry.GameRegistry;
import fendirain.fendirain.common.item.ItemFendiPiece;
import fendirain.fendirain.common.item.ItemFendirain;
import fendirain.fendirain.common.item.ItemTreeClearer;

public class ModItems {

    public static final ItemFendirain fendiPiece = new ItemFendiPiece();
    public static final ItemTreeClearer leafClearer = new ItemTreeClearer();

    public static void init() {
        GameRegistry.registerItem(fendiPiece, "fendiPiece");
        GameRegistry.registerItem(leafClearer, "treeClearer");
    }
}