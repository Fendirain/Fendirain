package fendirain.fendirain.common.block;

import fendirain.fendirain.common.item.ItemBlockFendirain;
import fendirain.fendirain.creativetab.CreativeTabFendirain;
import fendirain.fendirain.reference.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockFendirain extends Block {

    public BlockFendirain(String blockName) {
        super(Material.ROCK);
        this.setCreativeTab(CreativeTabFendirain.FENDIRAIN_TAB);
        if (shouldRegister()) GameRegistry.register(this, new ResourceLocation(Reference.MOD_PREFIX + blockName));
        registerItemForm();
    }

    protected boolean shouldRegister() {
        return true;
    }

    private void registerItemForm() {
        GameRegistry.register(new ItemBlockFendirain(this), getRegistryName());
    }

    @Override
    public String getUnlocalizedName() {
        return String.format("tile.%s%s", Reference.MOD_ID + ":", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

    protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }
}
