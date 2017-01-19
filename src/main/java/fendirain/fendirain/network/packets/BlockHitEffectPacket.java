package fendirain.fendirain.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class BlockHitEffectPacket implements IMessage {

    private Long blockPos;
    private EnumFacing enumFacing;

    public BlockHitEffectPacket() {
    }

    public BlockHitEffectPacket(Long blockPos, EnumFacing enumFacing) {
        this.blockPos = blockPos;
        this.enumFacing = enumFacing;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        blockPos = buf.readLong();
        enumFacing = EnumFacing.byName(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(blockPos);
        ByteBufUtils.writeUTF8String(buf, enumFacing.toString());
    }

    public static class Handler implements IMessageHandler<BlockHitEffectPacket, IMessage> {
        @Override
        public IMessage onMessage(BlockHitEffectPacket message, MessageContext ctx) {
            //Minecraft.getMinecraft().effectRenderer.addBlockHitEffects(BlockPos.fromLong(message.blockPos), message.enumFacing);
            //.getClientHandler().handleBlockBreakAnim(new SPacketBlockBreakAnim());
            Minecraft.getMinecraft().effectRenderer.addBlockHitEffects(BlockPos.fromLong(message.blockPos), EnumFacing.NORTH);
            Minecraft.getMinecraft().effectRenderer.addBlockHitEffects(BlockPos.fromLong(message.blockPos), EnumFacing.EAST);
            Minecraft.getMinecraft().effectRenderer.addBlockHitEffects(BlockPos.fromLong(message.blockPos), EnumFacing.SOUTH);
            Minecraft.getMinecraft().effectRenderer.addBlockHitEffects(BlockPos.fromLong(message.blockPos), EnumFacing.WEST);
            return null;
        }
    }
}
