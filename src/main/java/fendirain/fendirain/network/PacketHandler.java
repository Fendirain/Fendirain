package fendirain.fendirain.network;

import fendirain.fendirain.network.packets.BlockHitEffectPacket;
import fendirain.fendirain.network.packets.DestroyItemPacket;
import fendirain.fendirain.network.packets.EntityFenderiumChoppingPacket;
import fendirain.fendirain.network.packets.FenderiumAxeProgressPacket;
import fendirain.fendirain.reference.Reference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
    public static final SimpleNetworkWrapper simpleNetworkWrapper = new SimpleNetworkWrapper(Reference.MOD_ID);

    public static void register() {
        simpleNetworkWrapper.registerMessage(BlockHitEffectPacket.Handler.class, BlockHitEffectPacket.class, 0, Side.CLIENT);
        simpleNetworkWrapper.registerMessage(EntityFenderiumChoppingPacket.Handler.class, EntityFenderiumChoppingPacket.class, 2, Side.CLIENT);
        simpleNetworkWrapper.registerMessage(DestroyItemPacket.Handler.class, DestroyItemPacket.class, 3, Side.CLIENT);
        simpleNetworkWrapper.registerMessage(FenderiumAxeProgressPacket.Handler.class, FenderiumAxeProgressPacket.class, 4, Side.CLIENT);
    }

    public static void sendToAllAround(IMessage message, Entity entity, int range) {
        sendToAllAround(message, new NetworkRegistry.TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, range));
    }

    private static void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint targetPoint) {
        simpleNetworkWrapper.sendToAllAround(message, targetPoint);
    }

    public static void sendTo(IMessage message, EntityPlayerMP player) {
        simpleNetworkWrapper.sendTo(message, player);
    }
}
