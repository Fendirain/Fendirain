package fendirain.fendirain.common.entity.mob.EntityFendinain.AI;

import fendirain.fendirain.common.entity.mob.EntityFendinain.EntityFendinainMob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigate;

import java.util.Random;

public class EntityAIBegPlayer extends EntityAIBase {
    private EntityFendinainMob entity;
    private PathNavigate pathFinder;
    private EntityPlayer targetEntity = null;
    private Random rand;
    private double speed;
    private int randInt = 0;

    public EntityAIBegPlayer(EntityFendinainMob entity, double speed, Random rand) {
        this.entity = entity;
        this.pathFinder = entity.getNavigator();
        this.speed = speed;
        this.rand = rand;
    }

    @Override
    public boolean shouldExecute() {
        if (!pathFinder.noPath()) {
            return false;
        }
        if (entity.worldObj != null) {
            EntityPlayer player = entity.worldObj.getClosestPlayerToEntity(entity, 8);
            if (player != null && player.getHeldItem() != null) {
                if (entity.isValidForPickup(player.getHeldItem().getItem()) && entity.isAnySpaceForItemPickup(player.getHeldItem()) && !player.isInWater()) {
                    targetEntity = player;
                    randInt = rand.nextInt(4);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean continueExecuting() {
        if (entity.worldObj != null) {
            if (targetEntity != null && targetEntity.getHeldItem() != null) {
                if (entity.isValidForPickup(targetEntity.getHeldItem().getItem()) && entity.isAnySpaceForItemPickup(targetEntity.getHeldItem()) && !targetEntity.isInWater() && !(entity.getDistanceToEntity(targetEntity) > 8)) {
                    return true;
                }
            }
        }
        if (entity.worldObj != null && targetEntity != null) {
            switch (randInt) {
                case 0:
                    pathFinder.tryMoveToXYZ(targetEntity.posX + (rand.nextInt(12) + 8), targetEntity.posY, targetEntity.posZ + (rand.nextInt(8) + 8), this.speed);
                    break;
                case 1:
                    pathFinder.tryMoveToXYZ(targetEntity.posX - (rand.nextInt(12) + 8), targetEntity.posY, targetEntity.posZ + (rand.nextInt(8) + 8), this.speed);
                    break;
                case 2:
                    pathFinder.tryMoveToXYZ(targetEntity.posX + (rand.nextInt(12) + 8), targetEntity.posY, targetEntity.posZ - (rand.nextInt(8) + 8), this.speed);
                    break;
                case 3:
                    pathFinder.tryMoveToXYZ(targetEntity.posX - (rand.nextInt(12) + 8), targetEntity.posY, targetEntity.posZ - (rand.nextInt(8) + 8), this.speed);
                    break;
            }
        }
        targetEntity = null;
        return false;
    }

    @Override
    public void startExecuting() {
        if (targetEntity != null) {
            switch (randInt) {
                case 0:
                    pathFinder.tryMoveToXYZ(targetEntity.posX + (rand.nextDouble() + rand.nextDouble()), targetEntity.posY, targetEntity.posZ + rand.nextDouble(), this.speed - 0.2F);
                    break;
                case 1:
                    pathFinder.tryMoveToXYZ(targetEntity.posX - (rand.nextDouble() + rand.nextDouble()), targetEntity.posY, targetEntity.posZ - rand.nextDouble(), this.speed - 0.2F);
                    break;
                case 2:
                    pathFinder.tryMoveToXYZ(targetEntity.posX + rand.nextDouble(), targetEntity.posY, targetEntity.posZ + (rand.nextDouble() + rand.nextDouble()), this.speed - 0.2F);
                    break;
                case 3:
                    pathFinder.tryMoveToXYZ(targetEntity.posX - rand.nextDouble(), targetEntity.posY, targetEntity.posZ - (rand.nextDouble() + rand.nextDouble()), this.speed - 0.2F);
                    break;
            }
            this.entity.getLookHelper().setLookPosition(this.targetEntity.posX, this.targetEntity.posY + (double) this.targetEntity.getEyeHeight(), this.targetEntity.posZ, 10.0F, (float) this.entity.getVerticalFaceSpeed());
        }
    }

    @Override
    public void updateTask() {
        if (rand.nextInt(200) == 0) {
            int newInt = -1;
            while (newInt == -1 || newInt == randInt) {
                newInt = rand.nextInt(4);
            }
            randInt = newInt;
            startExecuting();
        }
        if (targetEntity != null && entity.getDistanceToEntity(targetEntity) > 2 && continueExecuting()) {
            startExecuting();
        }
    }

    @Override
    public boolean isInterruptible() {
        return false;
    }
}
