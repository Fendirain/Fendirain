package fendirain.fendirain.init;

import fendirain.fendirain.common.item.ItemFenderiumAxe;
import fendirain.fendirain.common.item.ItemFendiPiece;
import fendirain.fendirain.common.item.ItemFendirain;
import fendirain.fendirain.common.item.ItemTreeClearer;
import fendirain.fendirain.reference.ConfigValues;
import net.minecraft.item.Item;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModItems {
    public static final ItemFendirain itemFendiPiece = new ItemFendiPiece();
    public static final ItemTreeClearer itemTreeClearer = new ItemTreeClearer();
    public static final ItemFenderiumAxe itemFenderiumAxe = new ItemFenderiumAxe();
    public static Item.ToolMaterial Fendi = EnumHelper.addToolMaterial("fendi", 3, 2000, 6.0f, 2.0f, 15);

    public static void init() {
        GameRegistry.registerItem(itemFendiPiece, "itemFendiPiece");
        if (ConfigValues.isDebugSettingsEnabled) {
            GameRegistry.registerItem(itemTreeClearer, "itemTreeClearer");
        }
        GameRegistry.registerItem(itemFenderiumAxe, "itemFenderiumAxe");
    }
}