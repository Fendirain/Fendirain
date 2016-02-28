package fendirain.fendirain.client.gui;

import fendirain.fendirain.handler.ConfigurationHandler;
import fendirain.fendirain.reference.Reference;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

public class ModGuiConfig extends GuiConfig {

    public ModGuiConfig(GuiScreen guiScreen) {
        super(guiScreen, getConfigElements(), Reference.MOD_ID, false, false, "Fendirain's Configuration");
    }

    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> list = new ArrayList<>();
        list.add(categoryElement(ConfigurationHandler.CATEGORY_GENERAL, "General", "general", false, false));
        list.add(categoryElement(ConfigurationHandler.CATEGORY_WORLD, "World", "world", true, true));
        list.add(categoryElement(ConfigurationHandler.CATEGORY_DEVELOPER, "Developer", "developer", false, true));
        list.add(categoryElement(ConfigurationHandler.CATEGORY_MOB, "Mob", "mob", false, true));
        list.add(categoryElement(ConfigurationHandler.CATEGORY_ITEM, "Item", "item", false, false));
        return list;
    }

    private static IConfigElement categoryElement(String category, String name, String tooltip_key, boolean requiresWorldRestart, boolean requiresMcRestart) {
        //noinspection unchecked
        DummyConfigElement dummyConfigElement = new DummyConfigElement.DummyCategoryElement(name, tooltip_key, new ConfigElement(ConfigurationHandler.configuration.getCategory(category)).getChildElements());
        dummyConfigElement.setRequiresWorldRestart(requiresWorldRestart);
        dummyConfigElement.setRequiresMcRestart(requiresMcRestart);
        return dummyConfigElement;
    }
}
