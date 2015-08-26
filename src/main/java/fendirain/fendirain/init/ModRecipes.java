package fendirain.fendirain.init;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;

public class ModRecipes {

    public static void init() {
        // Add new shaped ore recipe.
        GameRegistry.addRecipe(new ItemStack(ModBlocks.blockFendi), "fff", "f nf", "fff", 'f', ModItems.fendiPiece);
        //GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.mapleLeaf), " s ", "sss", " s ", 's', "stickWood"));

        //GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.redstoneInfusedFendi), ModItems.fendi, Items.redstone, Items.redstone, Items.redstone, Items.redstone));

        // Add new shapeless ore recipe.
        //GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModBlocks.flag), new ItemStack(ModItems.mapleLeaf), new ItemStack(ModItems.mapleLeaf)));
    }
}
