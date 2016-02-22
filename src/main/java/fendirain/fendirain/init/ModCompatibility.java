package fendirain.fendirain.init;

import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;

import java.util.LinkedList;
import java.util.List;

public class ModCompatibility {
    public static List<Item> saplings = new LinkedList<>();

    public static void postInit() {
        // Add all sapling types in the OreDictionary.
        OreDictionary.getOres("treeSapling").stream().forEach(itemStack -> saplings.add(itemStack.getItem()));
    }
}
