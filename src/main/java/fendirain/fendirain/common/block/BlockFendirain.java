package fendirain.fendirain.common.block;

import fendirain.fendirain.common.item.ItemBlockFendirain;
import fendirain.fendirain.creativetab.CreativeTabFendirain;
import fendirain.fendirain.reference.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class BlockFendirain extends Block {

    public BlockFendirain(String blockName) {
        super(Material.ROCK);
        this.setCreativeTab(CreativeTabFendirain.FENDIRAIN_TAB);
        if (shouldRegister()) {
            this.setRegistryName(Reference.MOD_ID, blockName);
            ForgeRegistries.BLOCKS.register(this);
        }
        registerItemForm();
    }

    protected boolean shouldRegister() {
        return true;
    }

    private void registerItemForm() {
        ItemBlockFendirain itemBlockFendirain = new ItemBlockFendirain(this);
        itemBlockFendirain.setRegistryName(this.getRegistryName());
        ForgeRegistries.ITEMS.register(itemBlockFendirain);
    }

    @Override
    public String getUnlocalizedName() {
        return String.format("tile.%s%s", Reference.MOD_ID + ":", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

    protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }
}
