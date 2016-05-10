package fendirain.fendirain.common.item;

import fendirain.fendirain.reference.Reference;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class ItemBlockFendirain extends ItemBlock {

    public ItemBlockFendirain(Block block) {
        super(block);
    }

    @Override
    public String getUnlocalizedName() {
        return String.format("tile.%s%s", Reference.MOD_ID + ":", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

    protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }
}
