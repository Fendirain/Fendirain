package fendirain.fendirain.client.gui;

import cpw.mods.fml.client.config.DummyConfigElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import fendirain.fendirain.handler.ConfigurationHandler;
import fendirain.fendirain.reference.Reference;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;

import java.util.ArrayList;
import java.util.List;

public class ModGuiConfig extends GuiConfig {

    public ModGuiConfig(GuiScreen guiScreen) {
        super(guiScreen, getConfigElements(), Reference.MOD_ID, false, true, "Fendirain's Configuration");
    }

    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> list = new ArrayList<IConfigElement>();
        list.add(categoryElement(ConfigurationHandler.CATEGORY_GENERAL, "General", "General"));
        list.add(categoryElement(ConfigurationHandler.CATEGORY_DEVELOPER, "Developer", "Developer"));
        list.add(categoryElement(ConfigurationHandler.CATEGORY_MOB, "Mob", "Mob"));
        return list;
    }

    private static IConfigElement categoryElement(String category, String name, String tooltip_key) {
        //noinspection unchecked
        return new DummyConfigElement.DummyCategoryElement(name, tooltip_key, new ConfigElement(ConfigurationHandler.configuration.getCategory(category)).getChildElements());
    }
}
