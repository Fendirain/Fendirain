package fendirain.fendirain.proxy;

import cpw.mods.fml.client.registry.RenderingRegistry;
import fendirain.fendirain.client.models.mobs.ModelFenderiumMob;
import fendirain.fendirain.client.models.mobs.ModelFendinainMob;
import fendirain.fendirain.client.render.mobs.RenderFenderium;
import fendirain.fendirain.client.render.mobs.RenderFendinain;
import fendirain.fendirain.common.entity.mob.EntityFenderium.EntityFenderiumMob;
import fendirain.fendirain.common.entity.mob.EntityFendinain.EntityFendinainMob;

public class ClientProxy extends CommonProxy {

    @Override
    public void registerRender() {
        RenderingRegistry.registerEntityRenderingHandler(EntityFendinainMob.class, new RenderFendinain(new ModelFendinainMob(), 0.0f));
        RenderingRegistry.registerEntityRenderingHandler(EntityFenderiumMob.class, new RenderFenderium(new ModelFenderiumMob(), 0.0f));
    }

    @Override
    public void registerKeyBindings() {
        //ClientRegistry.registerKeyBinding(KeyBindings.[key]);
    }
}
