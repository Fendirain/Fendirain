package fendirain.fendirain.init;

import cpw.mods.fml.common.registry.GameRegistry;
import fendirain.fendirain.item.ItemFendiPiece;
import fendirain.fendirain.item.ItemFendirain;

public class ModItems {

    public static final ItemFendirain fendiPiece = new ItemFendiPiece();

    public static void init() {
        GameRegistry.registerItem(fendiPiece, "fendiPiece");
    }
}