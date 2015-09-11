package fendirain.fendirain.init;

import fendirain.fendirain.worldgen.FendiOreGenerator;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModWorldGenerator {

    public final static FendiOreGenerator fendiOreGenerator = new FendiOreGenerator();

    public static void init() {
        GameRegistry.registerWorldGenerator(fendiOreGenerator, 0);
    }
}