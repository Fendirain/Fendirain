package fendirain.fendirain.proxy;

import cpw.mods.fml.client.registry.RenderingRegistry;
import fendirain.fendirain.client.models.ModelFendinainMob;
import fendirain.fendirain.client.render.RenderMob;
import fendirain.fendirain.entity.mob.EntityFendinainMob;
import fendirain.fendirain.init.ModEntities;

public class ClientProxy extends CommonProxy {

    @Override
    public void registerRender() {
        RenderingRegistry.registerEntityRenderingHandler(EntityFendinainMob.class, new RenderMob(new ModelFendinainMob(), 0.2f, ModEntities.fendinainMobTexture));
    }

    @Override
    public void registerKeyBindings() {
        //ClientRegistry.registerKeyBinding(KeyBindings.[key]);
    }
}
