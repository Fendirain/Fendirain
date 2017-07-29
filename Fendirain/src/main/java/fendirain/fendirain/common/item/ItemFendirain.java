package fendirain.fendirain.common.item;

import fendirain.fendirain.creativetab.CreativeTabFendirain;
import fendirain.fendirain.reference.Reference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ItemFendirain extends Item {

    public ItemFendirain(String itemName) {
        super();
        this.setCreativeTab(CreativeTabFendirain.FENDIRAIN_TAB);
        if (shouldRegister()) {
            this.setRegistryName(Reference.MOD_ID, itemName);
            ForgeRegistries.ITEMS.register(this);
        }
    }

    protected boolean shouldRegister() {
        return true;
    }

    @Override
    public String getUnlocalizedName() {
        return String.format("item.%s%s", Reference.MOD_ID + ":", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        return String.format("item.%s%s", Reference.MOD_ID + ":", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

    protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }
}
