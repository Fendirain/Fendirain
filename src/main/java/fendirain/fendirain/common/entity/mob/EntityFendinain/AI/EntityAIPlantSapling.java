package fendirain.fendirain.common.entity.mob.EntityFendinain.AI;

import fendirain.fendirain.common.entity.mob.EntityFendinain.EntityFendinainMob;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class EntityAIPlantSapling extends EntityAIBase {
    private final EntityFendinainMob entity;
    private final int minTimeToWaitToPlant, maxTimeToWaitToPlant;
    private int timeSinceLastPlacement = 0;

    public EntityAIPlantSapling(EntityFendinainMob entity, int minTimeToWaitToPlant, int maxTimeToWaitToPlant) {
        this.entity = entity;
        this.minTimeToWaitToPlant = minTimeToWaitToPlant;
        this.maxTimeToWaitToPlant = maxTimeToWaitToPlant;
    }

    @Override
    public boolean shouldExecute() {
        return timeSinceLastPlacement > minTimeToWaitToPlant && (timeSinceLastPlacement > maxTimeToWaitToPlant || new Random().nextInt(600) == 0) && entity.isItemToPlace();
    }

    @Override
    public void startExecuting() {
        ItemStack itemToPlace = entity.getItemToPlace();
        if (itemToPlace != null) {
            World world = entity.worldObj;
            int posX = (int) entity.posX, posY = (int) entity.posY, posZ = (int) entity.posZ;
            Block blockToPlaceOn = world.getBlockState(new BlockPos(posX, posY - 1, posZ)).getBlock();
            if (BlockSapling.getBlockFromItem(itemToPlace.getItem()).canPlaceBlockAt(world, new BlockPos(posX, posY, posZ)) && (blockToPlaceOn == Blocks.grass || blockToPlaceOn == Blocks.dirt || blockToPlaceOn == Blocks.farmland)) {
                if (world.setBlockState(new BlockPos(posX, posY, posZ), BlockSapling.getBlockFromItem(itemToPlace.getItem()).getStateFromMeta(itemToPlace.getItemDamage()), 3)) {
                    entity.removeItemFromInventory(itemToPlace, 1, true);
                    timeSinceLastPlacement = 0;
                }
            }
        }
    }

    public int getTimeSinceLastPlacement() {
        return timeSinceLastPlacement;
    }

    public void addToTimeSinceLastPlacement(int timeToAdd) {
        timeSinceLastPlacement += timeToAdd;
    }
}
