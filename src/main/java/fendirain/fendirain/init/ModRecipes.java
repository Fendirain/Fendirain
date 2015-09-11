package fendirain.fendirain.init;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModRecipes {

    public static void init() {
        // Add new shaped ore recipe.
        GameRegistry.addRecipe(new ItemStack(ModBlocks.blockFendi), "fff", "f f", "fff", 'f', ModItems.fendiPiece);
        //GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.mapleLeaf), " s ", "sss", " s ", 's', "stickWood"));

        //GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.redstoneInfusedFendi), ModItems.fendi, Items.redstone, Items.redstone, Items.redstone, Items.redstone));

        // Add new shapeless ore recipe.
        //GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModBlocks.flag), new ItemStack(ModItems.mapleLeaf), new ItemStack(ModItems.mapleLeaf)));
    }
}
