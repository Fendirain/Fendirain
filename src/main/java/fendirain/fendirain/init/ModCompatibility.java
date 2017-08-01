package fendirain.fendirain.init;

import fendirain.fendirain.Fendirain;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;

import java.util.LinkedList;
import java.util.List;

public class ModCompatibility {
    public static List<Item> saplings = new LinkedList<>();

    public static void postInit() {
        Fendirain.logHelper.info(OreDictionary.getOreNames()); // TODO Remove- Temp
        // Add all sapling types in the OreDictionary.
        OreDictionary.getOres("treeSapling").stream().forEach(itemStack -> saplings.add(itemStack.getItem()));
    }
}
