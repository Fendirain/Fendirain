package fendirain.fendirain;

import fendirain.fendirain.client.handler.KeyInputEventHandler;
import fendirain.fendirain.handler.ConfigurationHandler;
import fendirain.fendirain.init.*;
import fendirain.fendirain.network.PacketHandler;
import fendirain.fendirain.proxy.IProxy;
import fendirain.fendirain.reference.Reference;
import fendirain.fendirain.utility.helper.LogHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(name = Reference.MOD_NAME, modid = Reference.MOD_ID, version = Reference.VERSION, guiFactory = Reference.GUI_FACTORY_CLASS)
public class Fendirain {

    @Mod.Instance(Reference.MOD_ID)
    public static Fendirain instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static IProxy proxy;

    public static LogHelper logHelper;


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent preInitializationEvent) {
        // Network Handling, Configuration, Initialize items, blocks, and entities.
        logHelper = new LogHelper(preInitializationEvent.getModLog());

        ConfigurationHandler.init(preInitializationEvent.getSuggestedConfigurationFile());

        MinecraftForge.EVENT_BUS.register(new ConfigurationHandler());

        PacketHandler.register();

        proxy.registerKeyBindings();
        ModItems.init();
        ModBlocks.init();
        ModTileEntities.init();
        ModWorldGenerator.init();
        ModEntities.init();
        proxy.registerRenderPreInit();
        logHelper.info("Pre-Initialization Complete");
    }

    @Mod.EventHandler
    public void init(@SuppressWarnings("UnusedParameters") FMLInitializationEvent initializationEvent) {
        // Register Gui's, Tile Entity's, Crafting recipes, other event handlers.
        proxy.registerRenderInit();
        MinecraftForge.EVENT_BUS.register(new KeyInputEventHandler());
        ModRecipes.init();
        logHelper.info("Initialization Complete");
    }

    @SuppressWarnings("UnusedParameters")
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent postInitializationEvent) {
        // Finish up initialization, Run things after other mods initialization.
        proxy.registerEvents();
        ModCompatibility.postInit();
        logHelper.info("Post-Initialization Complete");
    }
}
