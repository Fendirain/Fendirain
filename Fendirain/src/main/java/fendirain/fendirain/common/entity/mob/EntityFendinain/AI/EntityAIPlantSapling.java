package fendirain.fendirain.common.entity.mob.EntityFendinain.AI;

import fendirain.fendirain.common.entity.mob.EntityFendinain.EntityFendinainMob;
import fendirain.fendirain.reference.ConfigValues;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
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
        return (!ConfigValues.isMobGriefingGameRuleFollowed || this.entity.world.getGameRules().getBoolean("mobGriefing")) && timeSinceLastPlacement > minTimeToWaitToPlant && (timeSinceLastPlacement > maxTimeToWaitToPlant || new Random().nextInt(600) == 0) && entity.isItemToPlace();
    }

    @Override
    public void startExecuting() {
        ItemStack itemToPlace = entity.getItemToPlace();
        if (itemToPlace != null && entity.isItemValidForEntity(itemToPlace.getItem())) {
            World world = entity.world;
            double posX = entity.posX, posY = entity.posY, posZ = entity.posZ;
            Block blockToPlaceOn = world.getBlockState(new BlockPos(posX, posY - 1, posZ)).getBlock();
            if (BlockSapling.getBlockFromItem(itemToPlace.getItem()).canPlaceBlockAt(world, new BlockPos(posX, posY, posZ)) && (blockToPlaceOn == Blocks.GRASS || blockToPlaceOn == Blocks.DIRT || blockToPlaceOn == Blocks.FARMLAND)) {
                if (world.setBlockState(new BlockPos(posX, posY, posZ), BlockSapling.getBlockFromItem(itemToPlace.getItem()).getDefaultState(), 3)) {
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
