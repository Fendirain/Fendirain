package fendirain.fendirain.init;

import cpw.mods.fml.common.registry.GameRegistry;
import fendirain.fendirain.worldgen.FendiOreGenerator;

public class ModWorldGenerator {

    public static FendiOreGenerator fendiOreGenerator = new FendiOreGenerator();

    public static void init() {
        GameRegistry.registerWorldGenerator(fendiOreGenerator, 0);
    }
}