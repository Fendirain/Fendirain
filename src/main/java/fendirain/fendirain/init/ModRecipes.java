package fendirain.fendirain.init;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;

public class ModRecipes {

    public static void init() {
        GameRegistry.addRecipe(new ItemStack(ModBlocks.blockFendi), "fff", "f f", "fff", 'f', ModItems.fendiPiece);
    }
}
