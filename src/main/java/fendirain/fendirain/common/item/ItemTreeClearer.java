package fendirain.fendirain.common.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;


public class ItemTreeClearer extends ItemFendirain {

    public ItemTreeClearer() {
        super();
        this.maxStackSize = 1;
        this.setUnlocalizedName("treeClearer");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        if (!world.isRemote) {
            Block block;
            int posX = (int) entityPlayer.posX, posY = (int) entityPlayer.posY, posZ = (int) entityPlayer.posZ;
            for (int y = posY + 20; y >= posY - 20; y--) {
                for (int x = posX - 20; x <= posX + 20; x++) {
                    for (int z = posZ - 20; z <= posZ + 20; z++) {
                        block = world.getBlock(x, y, z);
                        if (block instanceof BlockLeaves || block instanceof BlockLog) {
                            world.setBlockToAir(x, y, z);
                        }
                    }
                }
            }
            @SuppressWarnings("unchecked")
            List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, entityPlayer.boundingBox);
            for (EntityItem item : items) {
                if ((int) item.getDistanceToEntity(entityPlayer) <= 20 && item.getEntityItem().getItem() instanceof ItemBlock && Block.getBlockFromItem(item.getEntityItem().getItem()) == Blocks.sapling) {
                    item.setDead();
                }
            }
        }
        return itemStack;
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, World world) {
        return 0;
    }
}
