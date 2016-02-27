package fendirain.fendirain.network;

import fendirain.fendirain.network.packets.BlockHitEffectPacket;
import fendirain.fendirain.network.packets.DestroyItemPacket;
import fendirain.fendirain.network.packets.EntityFenderiumChoppingPacket;
import fendirain.fendirain.reference.Reference;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
    public static final SimpleNetworkWrapper simpleNetworkWrapper = new SimpleNetworkWrapper(Reference.MOD_ID);

    public static void register() {
        simpleNetworkWrapper.registerMessage(BlockHitEffectPacket.Handler.class, BlockHitEffectPacket.class, 0, Side.CLIENT);
        simpleNetworkWrapper.registerMessage(EntityFenderiumChoppingPacket.Handler.class, EntityFenderiumChoppingPacket.class, 2, Side.CLIENT);
        simpleNetworkWrapper.registerMessage(DestroyItemPacket.Handler.class, DestroyItemPacket.class, 3, Side.CLIENT);
    }
}
