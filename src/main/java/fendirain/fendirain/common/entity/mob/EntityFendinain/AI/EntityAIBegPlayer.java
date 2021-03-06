package fendirain.fendirain.common.entity.mob.EntityFendinain.AI;

import fendirain.fendirain.common.entity.mob.EntityFendinain.EntityFendinainMob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.EnumHand;

import java.util.Random;

public class EntityAIBegPlayer extends EntityAIBase {
    private final EntityFendinainMob entity;
    private final PathNavigate pathFinder;
    private final Random rand;
    private final double speed;
    private EntityPlayer targetEntity = null;
    private int randInt = 0;

    public EntityAIBegPlayer(EntityFendinainMob entity, double speed, Random rand) {
        this.entity = entity;
        this.pathFinder = entity.getNavigator();
        this.speed = speed;
        this.rand = rand;
    }

    @Override
    public boolean shouldExecute() {
        if (!pathFinder.noPath()) return false;
        if (entity.world != null && entity.getPercentageOfInventoryFull() < 10) {
            EntityPlayer player = entity.world.getClosestPlayerToEntity(entity, 8);
            if (player != null) { //TODO Allow Left hand
                if (entity.getEntitySenses().canSee(player) && entity.isItemValidForEntity(player.getHeldItem(EnumHand.MAIN_HAND).getItem()) && entity.isAnySpaceForItemPickup(player.getHeldItem(EnumHand.MAIN_HAND)) && !player.isInWater()) {
                    targetEntity = player;
                    randInt = rand.nextInt(4);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (entity.world != null) {
            if (targetEntity != null) { //TODO Allow Left hand
                if (entity.isItemValidForEntity(targetEntity.getHeldItem(EnumHand.MAIN_HAND).getItem()) && entity.isAnySpaceForItemPickup(targetEntity.getHeldItem(EnumHand.MAIN_HAND)) && !targetEntity.isInWater() && !(entity.getDistanceToEntity(targetEntity) > 8))
                    return true;
            }
        }
        if (entity.world != null && targetEntity != null) {
            switch (randInt) {
                case 0:
                    pathFinder.tryMoveToXYZ(targetEntity.posX + (rand.nextInt(12) + 8), targetEntity.posY, targetEntity.posZ + (rand.nextInt(12) + 8), this.speed);
                    break;
                case 1:
                    pathFinder.tryMoveToXYZ(targetEntity.posX - (rand.nextInt(12) + 8), targetEntity.posY, targetEntity.posZ + (rand.nextInt(12) + 8), this.speed);
                    break;
                case 2:
                    pathFinder.tryMoveToXYZ(targetEntity.posX + (rand.nextInt(12) + 8), targetEntity.posY, targetEntity.posZ - (rand.nextInt(12) + 8), this.speed);
                    break;
                case 3:
                    pathFinder.tryMoveToXYZ(targetEntity.posX - (rand.nextInt(12) + 8), targetEntity.posY, targetEntity.posZ - (rand.nextInt(12) + 8), this.speed);
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
            while (newInt == -1 || newInt == randInt) newInt = rand.nextInt(4);
            randInt = newInt;
            startExecuting();
        }
        if (targetEntity != null && entity.getDistanceToEntity(targetEntity) > 2 && shouldContinueExecuting())
            startExecuting();
    }

    @Override
    public boolean isInterruptible() {
        return false;
    }
}
