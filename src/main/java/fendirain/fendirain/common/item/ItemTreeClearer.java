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
    private boolean alreadyWorking;

    public ItemTreeClearer() {
        super();
        this.maxStackSize = 1;
        this.setUnlocalizedName("treeClearer");
        alreadyWorking = false;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        if (!world.isRemote && !alreadyWorking) {
            alreadyWorking = true;
            int range = 20;
            int posX = (int) entityPlayer.posX, posY = (int) entityPlayer.posY, posZ = (int) entityPlayer.posZ;
            for (int y = posY + range; y >= posY - range; y--) {
                for (int x = posX - range; x <= posX + range; x++) {
                    for (int z = posZ - range; z <= posZ + range; z++) {
                        Block block = world.getBlock(x, y, z);
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
        alreadyWorking = false;
        return itemStack;
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, World world) {
        return 0;
    }
}
