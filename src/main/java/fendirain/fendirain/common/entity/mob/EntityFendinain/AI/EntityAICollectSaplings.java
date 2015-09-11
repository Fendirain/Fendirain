package fendirain.fendirain.common.entity.mob.EntityFendinain.AI;

import fendirain.fendirain.common.entity.mob.EntityFendinain.EntityFendinainMob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.AxisAlignedBB;

import java.util.List;

public class EntityAICollectSaplings extends EntityAIBase {

    private final EntityFendinainMob entity;
    private final PathNavigate pathFinder;
    private final double speed;
    private EntityItem targetItem = null;

    public EntityAICollectSaplings(EntityFendinainMob entity, double speed) {
        this.entity = entity;
        this.pathFinder = entity.getNavigator();
        this.speed = speed;
    }

    @Override
    public boolean shouldExecute() {
        if (!pathFinder.noPath()) {
            return false;
        }
        if (entity.worldObj != null) {
            @SuppressWarnings("unchecked")
            List<EntityItem> items = entity.worldObj.getEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.fromBounds(entity.posX - 1, entity.posY - 1, entity.posZ - 1, entity.posX + 1, entity.posY + 1, entity.posZ + 1).expand(10.0, 10.0, 10.0));
            EntityItem closest = null;
            double closestDistance = Double.MAX_VALUE;
            for (EntityItem item : items) {
                if (!item.isDead && item.onGround) {
                    double dist = item.getDistanceToEntity(entity);
                    if (dist < closestDistance && entity.getEntitySenses().canSee(item) && (entity.isValidForPickup(item.getEntityItem().getItem()) && entity.isAnySpaceForItemPickup(item.getEntityItem())) && !item.isInWater()) {
                        closest = item;
                        closestDistance = dist;
                    }
                }
            }
            if (closest != null) {
                targetItem = closest;
                return true;
            }
        }
        return false;
    }

    @Override
    public void startExecuting() {
        if (targetItem != null) {
            pathFinder.tryMoveToXYZ(targetItem.posX, targetItem.posY, targetItem.posZ, this.speed);
        }
    }

    @Override
    public void updateTask() {
        if (!entity.worldObj.isRemote) {
            if (targetItem != null && entity.getDistanceToEntity(targetItem) < 1.0) {
                ItemStack itemStack = targetItem.getEntityItem();
                int beforePickupSize = itemStack.stackSize;
                entity.putIntoInventory(itemStack);
                if (beforePickupSize != itemStack.stackSize) {
                    if (itemStack.stackSize == 0) {
                        targetItem.setDead();
                    }
                }
            }
        }
    }


}
