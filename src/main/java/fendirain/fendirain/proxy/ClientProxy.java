package fendirain.fendirain.proxy;

import fendirain.fendirain.client.render.mobs.RenderFenderium;
import fendirain.fendirain.client.render.mobs.RenderFendinain;
import fendirain.fendirain.common.entity.mob.EntityFenderium.EntityFenderiumMob;
import fendirain.fendirain.common.entity.mob.EntityFendinain.EntityFendinainMob;
import fendirain.fendirain.event.RenderEvent;
import fendirain.fendirain.init.ModBlocks;
import fendirain.fendirain.init.ModItems;
import fendirain.fendirain.init.ModRenderer;
import fendirain.fendirain.reference.ConfigValues;
import fendirain.fendirain.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {

    @Override
    public void registerRenderPreInit() {
        RenderingRegistry.registerEntityRenderingHandler(EntityFendinainMob.class, RenderFendinain::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityFenderiumMob.class, RenderFenderium::new);

        this.setCustomModelResourceLocation(ModItems.itemFendiPiece);
        this.setCustomModelResourceLocation(ModItems.itemFenderiumAxe, "itemFenderiumAxe_Charging_1");
        this.setCustomModelResourceLocation(ModItems.itemFenderiumAxe, "itemFenderiumAxe_Charging_2");
        this.setCustomModelResourceLocation(ModItems.itemFenderiumAxe, "itemFenderiumAxe_Charging_3");
        this.setCustomModelResourceLocation(ModItems.itemFenderiumAxe, "itemFenderiumAxe_Charging_4");
        this.setCustomModelResourceLocation(ModItems.itemFenderiumAxe, "itemFenderiumAxe_Charging_5");
        this.setCustomModelResourceLocation(ModItems.itemFenderiumAxe, "itemFenderiumAxe_Charging_6");
        this.setCustomModelResourceLocation(ModItems.itemFenderiumAxe, "itemFenderiumAxe_Charging_7");
        this.setCustomModelResourceLocation(ModItems.itemFenderiumAxe, "itemFenderiumAxe_Charging_8");
        this.setCustomModelResourceLocation(ModItems.itemFenderiumAxe, "itemFenderiumAxe_Charging_9");
        this.setCustomModelResourceLocation(ModItems.itemFenderiumAxe, "itemFenderiumAxe_Default");
        this.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.blockFendi));
        this.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.blockOreFendi));
        if (ConfigValues.isDebugSettingsEnabled) {
            this.setCustomModelResourceLocation(ModItems.itemPlantClearer);
            this.setCustomModelResourceLocation(ModItems.itemDebug);
        }
    }

    private void setCustomModelResourceLocation(Item item) {
        ResourceLocation location = item.getRegistryName();
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(location, "inventory"));
    }

    private void setCustomModelResourceLocation(Item item, String location) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Reference.MOD_PREFIX + location, "inventory"));
    }

    @Override
    public void registerRenderInit() {
        ModRenderer.init();
    }

    @Override
    public void registerKeyBindings() {
        //ClientRegistry.registerKeyBinding(KeyBindings.[key]);
    }

    @Override
    public void registerEvents() {
        RenderEvent renderEvent = new RenderEvent();
        MinecraftForge.EVENT_BUS.register(renderEvent);
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(renderEvent);
    }
}
