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
        return String.format("tile.%s%s", Reference.MOD_ID.toLowerCase() + ":", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

/*    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(String.format("%s", getUnwrappedUnlocalizedName(this.getUnlocalizedName())));
    }*/

    protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }
}
