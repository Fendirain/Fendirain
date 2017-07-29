package fendirain.fendirain.init;

import fendirain.fendirain.common.item.*;
import net.minecraft.item.Item;
import net.minecraftforge.common.util.EnumHelper;

public class ModItems {
    public static final Item.ToolMaterial fendi = EnumHelper.addToolMaterial("fendi", 3, 2000, 6.0f, 2.0f, 15);
    public static ItemFendirain itemFendiPiece;
    public static ItemPlantClearer itemPlantClearer;
    public static ItemFenderiumAxe itemFenderiumAxe;
    public static ItemDebug itemDebug;

    public static void init() {
        itemFendiPiece = new ItemFendiPiece();
        itemPlantClearer = new ItemPlantClearer();
        itemFenderiumAxe = new ItemFenderiumAxe();
        itemDebug = new ItemDebug();
    }

}
