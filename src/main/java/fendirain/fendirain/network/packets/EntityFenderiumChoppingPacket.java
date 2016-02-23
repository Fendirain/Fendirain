package fendirain.fendirain.network.packets;

import fendirain.fendirain.common.entity.mob.EntityFenderium.EntityFenderiumMob;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class EntityFenderiumChoppingPacket implements IMessage {

    private int entityID;
    private boolean isChopping;

    public EntityFenderiumChoppingPacket() {
    }

    public EntityFenderiumChoppingPacket(int entityID, boolean isChopping) {
        this.entityID = entityID;
        this.isChopping = isChopping;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityID = buf.readInt();
        isChopping = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityID);
        buf.writeBoolean(isChopping);
    }

    public static class Handler implements IMessageHandler<EntityFenderiumChoppingPacket, IMessage> {
        @Override
        public IMessage onMessage(EntityFenderiumChoppingPacket message, MessageContext ctx) {
            EntityFenderiumMob entity = (EntityFenderiumMob) Minecraft.getMinecraft().theWorld.getEntityByID(message.entityID);
            entity.setIsChopping(message.isChopping);
            return null;
        }
    }
}
