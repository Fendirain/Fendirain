package fendirain.fendirain.creativetab;

import fendirain.fendirain.init.ModItems;
import fendirain.fendirain.reference.Reference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class CreativeTabFendirain {

    public static final CreativeTabs FENDIRAIN_TAB = new CreativeTabs(Reference.MOD_ID) {
        @Override
        public ItemStack getTabIconItem() {
            // Creative Tab Picture -- // TODO Change
            return new ItemStack(ModItems.itemFendiPiece);
        }
    };
}
