package fendirain.fendirain.proxy;

import fendirain.fendirain.client.render.mobs.RenderFenderium;
import fendirain.fendirain.client.render.mobs.RenderFendinain;
import fendirain.fendirain.common.entity.mob.EntityFenderium.EntityFenderiumMob;
import fendirain.fendirain.common.entity.mob.EntityFendinain.EntityFendinainMob;
import fendirain.fendirain.init.ModBlocks;
import fendirain.fendirain.init.ModItems;
import fendirain.fendirain.reference.ConfigValues;
import fendirain.fendirain.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {

    @Override
    public void registerRender() {
        RenderingRegistry.registerEntityRenderingHandler(EntityFendinainMob.class, RenderFendinain::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityFenderiumMob.class, RenderFenderium::new);
    }

    @Override
    public void registerModelMesher() {
        ItemModelMesher itemModelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        itemModelMesher.register(ModItems.itemFendiPiece, 0, new ModelResourceLocation(Reference.MOD_PREFIX + "itemFendiPiece", "inventory"));
        itemModelMesher.register(ModItems.itemFenderiumAxe, 0, new ModelResourceLocation(Reference.MOD_PREFIX + "itemFenderiumAxe", "inventory"));
        itemModelMesher.register(Item.getItemFromBlock(ModBlocks.blockFendi), 0, new ModelResourceLocation(Reference.MOD_PREFIX + "blockFendi", "inventory"));
        itemModelMesher.register(Item.getItemFromBlock(ModBlocks.blockOreFendi), 0, new ModelResourceLocation(Reference.MOD_PREFIX + "blockOreFendi", "inventory"));
        if (ConfigValues.isDebugSettingsEnabled) {
            itemModelMesher.register(ModItems.itemTreeClearer, 0, new ModelResourceLocation(Reference.MOD_PREFIX + "itemTreeClearer", "inventory"));
        }
    }

    @Override
    public void registerKeyBindings() {
        //ClientRegistry.registerKeyBinding(KeyBindings.[key]);
    }
}
