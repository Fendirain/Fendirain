package fendirain.fendirain.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class BlockDestroyEffectPacket implements IMessage {
    private Long blockPos;

    public BlockDestroyEffectPacket() {
    }

    public BlockDestroyEffectPacket(Long blockPos) {
        this.blockPos = blockPos;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.blockPos = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(blockPos);
    }

    public static class Handler implements IMessageHandler<BlockDestroyEffectPacket, IMessage> {
        @Override
        public IMessage onMessage(BlockDestroyEffectPacket message, MessageContext ctx) {
            Minecraft.getMinecraft().effectRenderer.addBlockDestroyEffects(BlockPos.fromLong(message.blockPos), Minecraft.getMinecraft().theWorld.getBlockState(BlockPos.fromLong(message.blockPos)));
            return null;
        }
    }
}
