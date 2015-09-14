package fendirain.fendirain.creativetab;

import fendirain.fendirain.init.ModItems;
import fendirain.fendirain.reference.Reference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CreativeTabFendirain {

    public static final CreativeTabs FENDIRAIN_TAB = new CreativeTabs(Reference.MOD_ID.toLowerCase()) {
        @Override
        public Item getTabIconItem() {
            // Creative Tab Picture -- // TODO Change
            return ModItems.fendiPiece;
        }
    };
}
