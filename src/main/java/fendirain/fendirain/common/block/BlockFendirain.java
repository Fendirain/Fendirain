package fendirain.fendirain.common.block;

import fendirain.fendirain.creativetab.CreativeTabFendirain;
import fendirain.fendirain.reference.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockFendirain extends Block {

    public BlockFendirain() {
        super(Material.rock);
        this.setCreativeTab(CreativeTabFendirain.FENDIRAIN_TAB);
    }

    @Override
    public String getUnlocalizedName() {
        return String.format("tile.%s%s", Reference.MOD_ID + ":", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

    protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }
}
