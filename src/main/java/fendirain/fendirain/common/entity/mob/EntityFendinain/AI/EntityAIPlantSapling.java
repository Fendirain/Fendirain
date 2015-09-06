package fendirain.fendirain.common.entity.mob.EntityFendinain.AI;

import fendirain.fendirain.common.entity.mob.EntityFendinain.EntityFendinainMob;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Random;

public class EntityAIPlantSapling extends EntityAIBase {
    private int timeSinceLastPlacement = 0;
    private EntityFendinainMob entity;
    private int minTimeToWaitToPlant, maxTimeToWaitToPlant;

    public EntityAIPlantSapling(EntityFendinainMob entity, int minTimeToWaitToPlant, int maxTimeToWaitToPlant) {
        this.entity = entity;
        this.minTimeToWaitToPlant = minTimeToWaitToPlant;
        this.maxTimeToWaitToPlant = maxTimeToWaitToPlant;
    }

    @Override
    public boolean shouldExecute() {
        return timeSinceLastPlacement > minTimeToWaitToPlant && (timeSinceLastPlacement > maxTimeToWaitToPlant || new Random().nextInt(80) == 0) && entity.isItemToPlace();
    }

    public boolean shouldExecuteIgnoreMin() {
        return timeSinceLastPlacement > maxTimeToWaitToPlant || new Random().nextInt(10) == 0;
    }

    @Override
    public void startExecuting() {
        ItemStack itemToPlace = entity.getItemToPlace();
        if (itemToPlace != null) {
            World world = entity.worldObj;
            int posX = (int) entity.posX, posY = (int) entity.posY, posZ = (int) entity.posZ;
            Block blockToPlaceOn = world.getBlock(posX, posY - 1, posZ);
            if (BlockSapling.getBlockFromItem(itemToPlace.getItem()).canPlaceBlockAt(world, posX, posY, posZ) && (blockToPlaceOn == Blocks.grass || blockToPlaceOn == Blocks.dirt || blockToPlaceOn == Blocks.farmland)) {
                world.setBlock(posX, posY, posZ, BlockSapling.getBlockFromItem(itemToPlace.getItem()), itemToPlace.getItemDamage(), 3);
                entity.removeItemFromInventory(itemToPlace, 1, true);
                timeSinceLastPlacement = 0;
            }
        }
    }

    public void addToTimeSinceLastPlacement(int timeToAdd) {
        timeSinceLastPlacement += timeToAdd;
    }
}
