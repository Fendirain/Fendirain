package fendirain.fendirain.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class DestroyItemPacket implements IMessage {

    ItemStack itemStack;

    public DestroyItemPacket() {
    }

    public DestroyItemPacket(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        itemStack = new ItemStack(ByteBufUtils.readTag(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, itemStack.writeToNBT(new NBTTagCompound()));
    }

    public static class Handler implements IMessageHandler<DestroyItemPacket, IMessage> {
        @Override
        public IMessage onMessage(DestroyItemPacket message, MessageContext ctx) {
            Minecraft.getMinecraft().player.renderBrokenItemStack(message.itemStack);
            return null;
        }
    }
}
