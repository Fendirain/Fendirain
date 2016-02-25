package fendirain.fendirain.common.item;

import fendirain.fendirain.init.ModCompatibility;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.List;


public class ItemPlantClearer extends ItemFendirain {
    private boolean alreadyWorking;

    public ItemPlantClearer() {
        super();
        this.maxStackSize = 1;
        this.setUnlocalizedName("itemPlantClearer");
        alreadyWorking = false;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        if (!world.isRemote && !alreadyWorking) {
            if (!entityPlayer.isSneaking()) {
                alreadyWorking = true;
                int range = 20;
                int posX = (int) entityPlayer.posX, posY = (int) entityPlayer.posY, posZ = (int) entityPlayer.posZ;
                for (int y = posY + range; y >= posY - range; y--) {
                    for (int x = posX - range; x <= posX + range; x++) {
                        for (int z = posZ - range; z <= posZ + range; z++) {
                            Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
                            if (block instanceof BlockLeaves || block instanceof BlockLog || block instanceof BlockTallGrass || block instanceof BlockFlower || block instanceof BlockDoublePlant || (Item.getItemFromBlock(block) != null && ModCompatibility.saplings.contains(Item.getItemFromBlock(block))))
                                world.setBlockToAir(new BlockPos(x, y, z));
                        }
                    }
                }
                @SuppressWarnings("unchecked")
                List<EntityItem> items = entityPlayer.worldObj.getEntitiesWithinAABB(EntityItem.class, entityPlayer.getEntityBoundingBox().expand(range, range, range));
                items.stream().filter(item -> (int) item.getDistanceToEntity(entityPlayer) <= range && ModCompatibility.saplings.contains(item.getEntityItem().getItem()) || (item.getEntityItem().getItem() instanceof ItemBlock && Block.getBlockFromItem(item.getEntityItem().getItem()) instanceof BlockLog) || item.getEntityItem().getItem() instanceof ItemSeeds || item.getEntityItem().getItem() == Items.apple).forEach(Entity::setDead);
            } else {
                alreadyWorking = true;
                int range = 20;
                int posX = (int) entityPlayer.posX, posY = (int) entityPlayer.posY, posZ = (int) entityPlayer.posZ;
                for (int y = posY + range; y >= posY - range; y--) {
                    for (int x = posX - range; x <= posX + range; x++) {
                        for (int z = posZ - range; z <= posZ + range; z++) {
                            Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
                            if (block instanceof BlockLeaves)
                                world.setBlockToAir(new BlockPos(x, y, z));
                        }
                    }
                }
                List<EntityItem> items = entityPlayer.worldObj.getEntitiesWithinAABB(EntityItem.class, entityPlayer.getEntityBoundingBox().expand(range, range, range));
                items.stream().filter(item -> (int) item.getDistanceToEntity(entityPlayer) <= range && ModCompatibility.saplings.contains(item.getEntityItem().getItem()) || (item.getEntityItem().getItem() instanceof ItemBlock && Block.getBlockFromItem(item.getEntityItem().getItem()) instanceof BlockLog) || item.getEntityItem().getItem() == Items.apple).forEach(Entity::setDead);
            }
        }
        alreadyWorking = false;
        return itemStack;
    }

    public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
        return false;
    }

   /* @Override
    public int getEntityLifespan(ItemStack itemStack, World world) {
        return 0;
    }*/
}
