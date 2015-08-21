package fendirain.fendirain.handler;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import fendirain.fendirain.reference.ConfigValues;
import fendirain.fendirain.reference.Reference;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ConfigurationHandler {

    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_DEVELOPER = "developer";
    public static Configuration configuration;

    public static void init(File configFile) {
        // Create the configuration object from the given configuration file
        if (configuration == null) {
            configuration = new Configuration(configFile);
            loadConfiguration();
        }
    }

    private static void loadConfiguration() {
        // Read in properties from configuration file - Most likely put it into a reference class.
        ConfigValues.isGenerationEnabled = configuration.getBoolean("oreGeneration", CATEGORY_GENERAL, true, "Disable ore generation.");
        ConfigValues.isDebugSettingsEnabled = configuration.getBoolean("debug", CATEGORY_DEVELOPER, false, "Enables debug settings");

        // Save the configuration file
        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    @SubscribeEvent
    public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent onConfigChangedEvent) {
        if (onConfigChangedEvent.modID.equalsIgnoreCase(Reference.MOD_ID)) {
            // Re-sync configs
            loadConfiguration();
        }
    }
}
