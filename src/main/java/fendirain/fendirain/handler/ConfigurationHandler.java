package fendirain.fendirain.handler;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import fendirain.fendirain.reference.ConfigValues;
import fendirain.fendirain.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraftforge.common.config.Configuration;

import javax.naming.CannotProceedException;
import java.io.File;

public class ConfigurationHandler {

    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_DEVELOPER = "developer";
    public static final String CATEGORY_MOB = "mob";
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
        ConfigValues.isGenerationEnabled = configuration.getBoolean("oreGeneration", CATEGORY_GENERAL, true, "Disable ore generation");
        ConfigValues.isDebugSettingsEnabled = configuration.getBoolean("debug", CATEGORY_DEVELOPER, false, "Enables debug settings");
        ConfigValues.fendinainMob_minTimeToWaitToPlant = configuration.getInt("Fendinain_MinTimeBetweenPlanting", CATEGORY_MOB, 4800, 0, Integer.MAX_VALUE, "The minimum amount of time the fendinain will wait to plant a sapling [Must be lower than Fendinain_MaxTimeBetweenPlanting]");
        ConfigValues.fendinainMob_maxTimeToWaitToPlant = configuration.getInt("Fendinain_MaxTimeBetweenPlanting", CATEGORY_MOB, 12000, 0, Integer.MAX_VALUE, "The longest amount of time the fendinain will wait to plant a sapling (Assuming it is able to plant one) [Must be higher than Fendinain_MinTimeBetweenPlanting]");

        if (ConfigValues.fendinainMob_minTimeToWaitToPlant > ConfigValues.fendinainMob_maxTimeToWaitToPlant) {
            Minecraft.getMinecraft().crashed(new CrashReport("fendinainMob_minTimeToWaitToPlant is not lower than or equal too fendinainMob_maxTimeToWaitToPlant, Please correct.", new CannotProceedException()));
        }

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
