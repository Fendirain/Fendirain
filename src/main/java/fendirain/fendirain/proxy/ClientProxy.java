package fendirain.fendirain.proxy;

import fendirain.fendirain.client.render.mobs.RenderFenderium;
import fendirain.fendirain.client.render.mobs.RenderFendinain;
import fendirain.fendirain.common.entity.mob.EntityFenderium.EntityFenderiumMob;
import fendirain.fendirain.common.entity.mob.EntityFendinain.EntityFendinainMob;
import fendirain.fendirain.init.ModBlocks;
import fendirain.fendirain.init.ModItems;
import fendirain.fendirain.init.ModRenderer;
import fendirain.fendirain.reference.ConfigValues;
import fendirain.fendirain.reference.Reference;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {

    @Override
    public void registerRenderPreInit() {
        RenderingRegistry.registerEntityRenderingHandler(EntityFendinainMob.class, RenderFendinain::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityFenderiumMob.class, RenderFenderium::new);

        ModelLoader.setCustomModelResourceLocation(ModItems.itemFendiPiece, 0, new ModelResourceLocation(Reference.MOD_PREFIX + "itemFendiPiece", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.itemFenderiumAxe, 0, new ModelResourceLocation(Reference.MOD_PREFIX + "itemFenderiumAxe", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.blockFendi), 0, new ModelResourceLocation(Reference.MOD_PREFIX + "blockFendi", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.blockOreFendi), 0, new ModelResourceLocation(Reference.MOD_PREFIX + "blockOreFendi", "inventory"));
        if (ConfigValues.isDebugSettingsEnabled)
            ModelLoader.setCustomModelResourceLocation(ModItems.itemPlantClearer, 0, new ModelResourceLocation(Reference.MOD_PREFIX + "itemPlantClearer", "inventory"));
    }

    @Override
    public void registerRenderInit() {
        ModRenderer.init();
    }

    @Override
    public void registerKeyBindings() {
        //ClientRegistry.registerKeyBinding(KeyBindings.[key]);
    }
}
