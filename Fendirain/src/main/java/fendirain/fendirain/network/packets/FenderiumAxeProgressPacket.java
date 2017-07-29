package fendirain.fendirain.network.packets;

import fendirain.fendirain.common.item.ItemFenderiumAxe;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class FenderiumAxeProgressPacket implements IMessage {
    private float progress;

    public FenderiumAxeProgressPacket() {

    }

    public FenderiumAxeProgressPacket(float progress) {
        this.progress = progress;
    }


    @Override
    public void fromBytes(ByteBuf buf) {
        this.progress = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(progress);
    }

    public static class Handler implements IMessageHandler<FenderiumAxeProgressPacket, IMessage> {
        @Override
        public IMessage onMessage(FenderiumAxeProgressPacket message, MessageContext ctx) {
            ItemStack itemStack = Minecraft.getMinecraft().player.getHeldItemMainhand();
            if (itemStack.getItem() instanceof ItemFenderiumAxe)
                ((ItemFenderiumAxe) itemStack.getItem()).setAxeProgress(message.progress);
            return null;
        }
    }
}
