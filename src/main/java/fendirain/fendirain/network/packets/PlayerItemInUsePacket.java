package fendirain.fendirain.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PlayerItemInUsePacket implements IMessage {
    ItemStack itemStack;
    int duration;

    public PlayerItemInUsePacket() {
    }

    public PlayerItemInUsePacket(ItemStack itemStack, int duration) {
        this.itemStack = itemStack;
        this.duration = duration;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        itemStack = ItemStack.loadItemStackFromNBT(ByteBufUtils.readTag(buf));
        duration = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, itemStack.writeToNBT(new NBTTagCompound()));
        buf.writeInt(duration);
    }

    public static class Handler implements IMessageHandler<PlayerItemInUsePacket, IMessage> {
        @Override
        public IMessage onMessage(PlayerItemInUsePacket message, MessageContext ctx) {
            Minecraft.getMinecraft().thePlayer.setItemInUse(message.itemStack, message.duration);
            return null;
        }
    }
}
