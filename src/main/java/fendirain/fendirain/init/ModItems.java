package fendirain.fendirain.init;

import fendirain.fendirain.common.item.*;
import fendirain.fendirain.reference.ConfigValues;
import net.minecraft.item.Item;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModItems {
    public static final Item.ToolMaterial fendi = EnumHelper.addToolMaterial("fendi", 3, 2000, 6.0f, 2.0f, 15);
    public static final ItemFendirain itemFendiPiece = new ItemFendiPiece();
    public static final ItemPlantClearer itemPlantClearer = new ItemPlantClearer();
    public static final ItemFenderiumAxe itemFenderiumAxe = new ItemFenderiumAxe();
    public static final ItemDebug itemDebug = new ItemDebug();

    public static void init() {
        GameRegistry.registerItem(itemFendiPiece, "itemFendiPiece");
        if (ConfigValues.isDebugSettingsEnabled) {
            GameRegistry.registerItem(itemPlantClearer, "itemPlantClearer");
            GameRegistry.registerItem(itemDebug, "itemDebug");
        }
        GameRegistry.registerItem(itemFenderiumAxe, "itemFenderiumAxe");
    }

}
