package fendirain.fendirain.network.packets;

import fendirain.fendirain.common.entity.mob.EntityFenderium.EntityFenderiumMob;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class EntityFenderiumChoppingPacket implements IMessage {

    private int entityID;
    private boolean isChopping;
    private long blockPos;
    private int currentProgress;

    public EntityFenderiumChoppingPacket() {
    }

    public EntityFenderiumChoppingPacket(int entityID, boolean isChopping, Long blockPos, int currentProgress) {
        this.entityID = entityID;
        this.isChopping = isChopping;
        this.blockPos = blockPos;
        this.currentProgress = currentProgress;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityID = buf.readInt();
        isChopping = buf.readBoolean();
        blockPos = buf.readLong();
        currentProgress = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityID);
        buf.writeBoolean(isChopping);
        buf.writeLong(blockPos);
        buf.writeInt(currentProgress);
    }

    public static class Handler implements IMessageHandler<EntityFenderiumChoppingPacket, IMessage> {
        @Override
        public IMessage onMessage(EntityFenderiumChoppingPacket message, MessageContext ctx) {
            EntityFenderiumMob entity = (EntityFenderiumMob) Minecraft.getMinecraft().theWorld.getEntityByID(message.entityID);
            if (entity != null) {
                entity.setIsChopping(message.isChopping);
                if (message.blockPos != -1) entity.setCurrentlyBreakingPos(BlockPos.fromLong(message.blockPos));
                else entity.setCurrentlyBreakingPos(null);
                entity.setCurrentlyBreakingProgress(message.currentProgress);
            }
            return null;
        }
    }
}
