package fendirain.fendirain.init;

import fendirain.fendirain.common.item.ItemFenderiumAxe;
import fendirain.fendirain.common.item.ItemFendiPiece;
import fendirain.fendirain.common.item.ItemFendirain;
import fendirain.fendirain.common.item.ItemPlantClearer;
import fendirain.fendirain.reference.ConfigValues;
import net.minecraft.item.Item;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModItems {
    public static final Item.ToolMaterial fendi = EnumHelper.addToolMaterial("fendi", 3, 2000, 6.0f, 2.0f, 15);
    // public static final EnumAction axeCharge = EnumHelper.addAction("axeCharge");
    public static final ItemFendirain itemFendiPiece = new ItemFendiPiece();
    public static final ItemPlantClearer itemPlantClearer = new ItemPlantClearer();
    public static final ItemFenderiumAxe itemFenderiumAxe = new ItemFenderiumAxe();

    public static void init() {
        GameRegistry.registerItem(itemFendiPiece, "itemFendiPiece");
        if (ConfigValues.isDebugSettingsEnabled) GameRegistry.registerItem(itemPlantClearer, "itemPlantClearer");
        GameRegistry.registerItem(itemFenderiumAxe, "itemFenderiumAxe");
    }

}
