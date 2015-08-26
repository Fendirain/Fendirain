package fendirain.fendirain;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import fendirain.fendirain.client.handler.KeyInputEventHandler;
import fendirain.fendirain.handler.ConfigurationHandler;
import fendirain.fendirain.init.*;
import fendirain.fendirain.proxy.IProxy;
import fendirain.fendirain.reference.Reference;
import fendirain.fendirain.utility.LogHelper;

@Mod(name = Reference.MOD_NAME, modid = Reference.MOD_ID, version = Reference.VERSION, guiFactory = Reference.GUI_FACTORY_CLASS)
public class Fendirain {

    @Mod.Instance(Reference.MOD_ID)
    public static Fendirain instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static IProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent preInitializationEvent) {
        // Network Handling, Configuration, Initialize items, blocks, and entities.
        ConfigurationHandler.init(preInitializationEvent.getSuggestedConfigurationFile());
        FMLCommonHandler.instance().bus().register(new ConfigurationHandler());

        // Initialize all keyBindings
        proxy.registerKeyBindings();

        // Initialize all mod items
        ModItems.init();
        // Initialize all mod blocks
        ModBlocks.init();
        // Initialize all mod tile entities
        ModTileEntities.init();
        // Initialize all mod entities
        ModEntities.init();
        // Initialize all mod renders;
        ModRenderer.init();
        proxy.registerRender();
        // Initialize all mod world generators
        ModWorldGenerator.init();

        LogHelper.info("Pre-Initialization Complete");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent initializationEvent) {
        // Register Gui's, Tile Entity's, Crafting recipes, other event handlers.

        // Register KeyInputEventHandler
        FMLCommonHandler.instance().bus().register(new KeyInputEventHandler());

        // Initialize all mod recipes
        ModRecipes.init();

        LogHelper.info("Initialization Complete");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent postInitializationEvent) {
        // Finish up initialization, Run things after other mods initialization.

        LogHelper.info("Post-Initialization Complete");
    }
}