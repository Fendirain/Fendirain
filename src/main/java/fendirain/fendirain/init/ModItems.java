package fendirain.fendirain.init;

import cpw.mods.fml.common.registry.GameRegistry;
import fendirain.fendirain.item.ItemFendi;
import fendirain.fendirain.item.ItemFendirain;
import fendirain.fendirain.item.ItemRedstoneInfusedFendi;

public class ModItems {

    public static final ItemFendirain fendi = new ItemFendi();
    public static final ItemRedstoneInfusedFendi redstoneInfusedFendi = new ItemRedstoneInfusedFendi();

    public static void init() {
        GameRegistry.registerItem(fendi, "fendi");
        GameRegistry.registerItem(redstoneInfusedFendi, "redstoneInfusedFendi");
    }
}