package fendirain.fendirain.proxy;

import fendirain.fendirain.client.models.mobs.ModelFenderiumMob;
import fendirain.fendirain.client.models.mobs.ModelFendinainMob;
import fendirain.fendirain.client.render.mobs.RenderFenderium;
import fendirain.fendirain.client.render.mobs.RenderFendinain;
import fendirain.fendirain.common.entity.mob.EntityFenderium.EntityFenderiumMob;
import fendirain.fendirain.common.entity.mob.EntityFendinain.EntityFendinainMob;
import fendirain.fendirain.init.ModItems;
import fendirain.fendirain.reference.ConfigValues;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {

    @Override
    public void registerRender() {
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        RenderingRegistry.registerEntityRenderingHandler(EntityFendinainMob.class, new RenderFendinain(renderManager, new ModelFendinainMob(), 0.0f));
        RenderingRegistry.registerEntityRenderingHandler(EntityFenderiumMob.class, new RenderFenderium(renderManager, new ModelFenderiumMob(), 0.0f));

        ItemModelMesher itemModelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        itemModelMesher.register(ModItems.fendiPiece, 0, new ModelResourceLocation("fendirain:fendiPiece", "inventory"));
        if (ConfigValues.isDebugSettingsEnabled) {
            itemModelMesher.register(ModItems.treeClearer, 0, new ModelResourceLocation("fendirain:treeClearer", "inventory"));
        }
    }

    @Override
    public void registerKeyBindings() {
        //ClientRegistry.registerKeyBinding(KeyBindings.[key]);
    }
}
