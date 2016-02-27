package fendirain.fendirain.handler;

import fendirain.fendirain.reference.ConfigValues;
import fendirain.fendirain.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.naming.CannotProceedException;
import java.io.File;

public class ConfigurationHandler {

    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_DEVELOPER = "developer";
    public static final String CATEGORY_MOB = "mob";
    public static final String CATEGORY_ITEM = "item";
    public static Configuration configuration;

    public static void init(File configFile) {
        configuration = new Configuration(configFile);
        loadConfiguration();
    }

    private static void loadConfiguration() {
        ConfigValues.isGenerationEnabled = configuration.getBoolean("oreGeneration", CATEGORY_GENERAL, true, "Disable ore generation.");
        ConfigValues.isDebugSettingsEnabled = configuration.getBoolean("debug", CATEGORY_DEVELOPER, false, "Enables debug settings.");

        // EntityFendinainMob
        ConfigValues.fendinainMob_enableSpawning = configuration.getBoolean("Fendinain_Spawning", CATEGORY_MOB, true, "Enables spawning of the Fendinain.");
        ConfigValues.fendinainMob_minTimeToWaitToPlant = configuration.getInt("Fendinain_MinTimeBetweenPlanting", CATEGORY_MOB, 4800, 0, Integer.MAX_VALUE, "The minimum amount of time the Fendinain will wait to plant a sapling. [Must be lower than Fendinain_MaxTimeBetweenPlanting]");
        ConfigValues.fendinainMob_maxTimeToWaitToPlant = configuration.getInt("Fendinain_MaxTimeBetweenPlanting", CATEGORY_MOB, 12000, 0, Integer.MAX_VALUE, "The longest amount of time the Fendinain will wait to plant a sapling (Assuming it is able to plant one). [Must be higher than Fendinain_MinTimeBetweenPlanting]");

        // EntityFenderiumMob
        ConfigValues.fenderiumMob_enableSpawning = configuration.getBoolean("Fenderium_Spawning", CATEGORY_MOB, true, "Enables spawning of the Fenderium.");
        ConfigValues.fenderiumMob_breakSpeed = configuration.getInt("Fenderium_BreakSpeed", CATEGORY_MOB, 0, 0, Integer.MAX_VALUE, "The higher this setting, the faster the Fenderium will break logs (There is a limit to the speed, will do more testing for it later.");
        ConfigValues.fenderiumMob_waitPerTreeOrLog = configuration.getBoolean("Fenderium_WaitPerLogOrTree", CATEGORY_MOB, true, "If true, Wait time will be added per a log broken. If false, it will wait the same amount of time per a tree broken no matter the size.");
        ConfigValues.fenderiumMob_timePerBreak = configuration.getInt("Fenderium_TimePerBreak", CATEGORY_MOB, 30, 0, Integer.MAX_VALUE, "Amount of seconds to wait, either per a log or tree chopped, depending on the 'Fenderium_WaitPerLogOrTree' setting.");

        if (ConfigValues.fendinainMob_minTimeToWaitToPlant > ConfigValues.fendinainMob_maxTimeToWaitToPlant)
            Minecraft.getMinecraft().crashed(new CrashReport("fendinainMob_minTimeToWaitToPlant is not lower than or equal too fendinainMob_maxTimeToWaitToPlant, Please correct.", new CannotProceedException()));

        // FenderiumAxe
        ConfigValues.fenderiumAxe_dropItemPerLog = configuration.getBoolean("FenderiumAxe_DropItemPerLog", CATEGORY_ITEM, false, "If true, The Fenderium Axe right-click ability will cause logs to be dropped as broken instead as one ItemStack at the end.");

        if (configuration.hasChanged()) configuration.save();
    }

    @SubscribeEvent
    public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent onConfigChangedEvent) {
        if (onConfigChangedEvent.modID.equalsIgnoreCase(Reference.MOD_ID)) loadConfiguration();
    }
}
