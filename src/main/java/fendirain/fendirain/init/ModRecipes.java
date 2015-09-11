package fendirain.fendirain.init;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModRecipes {

    public static void init() {
        GameRegistry.addRecipe(new ItemStack(ModBlocks.blockFendi), "fff", "f f", "fff", 'f', ModItems.fendiPiece);
    }
}
