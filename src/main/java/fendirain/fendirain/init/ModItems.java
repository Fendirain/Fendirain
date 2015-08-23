package fendirain.fendirain.init;

import cpw.mods.fml.common.registry.GameRegistry;
import fendirain.fendirain.item.ItemFendi;
import fendirain.fendirain.item.ItemFendirain;

public class ModItems {

    public static final ItemFendirain fendi = new ItemFendi();

    public static void init() {
        GameRegistry.registerItem(fendi, "fendi");
    }
}