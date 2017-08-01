package fendirain.fendirain.common.entity.mob.EntityFenderium.AI;

import fendirain.fendirain.common.entity.mob.EntityFenderium.EntityFenderiumMob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.world.World;

import java.util.Random;

// This whole class is temporary, I have another idea for the wood.
public class EntityAIThrowWoodAtPlayer extends EntityAIBase {

    private final EntityFenderiumMob entity;
    private final Random rand;
    private final float speed;
    private final PathNavigate pathFinder;
    private EntityPlayer targetEntity = null;
    private boolean alreadyExecuting = false;
    private int randPos = -1;

    public EntityAIThrowWoodAtPlayer(EntityFenderiumMob entity, Random rand, float speed) {
        this.entity = entity;
        this.rand = rand;
        this.speed = speed;
        this.pathFinder = entity.getNavigator();
    }

    @Override
    public boolean shouldExecute() {
        if (alreadyExecuting) return false;
        if (entity.world != null && entity.getPercentageOfInventoryFull() >= 50) {
            EntityPlayer player = entity.world.getClosestPlayerToEntity(entity, 8);
            if (player != null) {
                if (entity.getEntitySenses().canSee(player)) {
                    targetEntity = player;
                    randPos = rand.nextInt(4);
                    alreadyExecuting = true;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return entity.getPercentageOfInventoryFull() >= 50;
    }

    @Override
    public void startExecuting() {
        switch (randPos) {
            case 0:
                pathFinder.tryMoveToXYZ(targetEntity.posX + rand.nextInt(3) + 2, targetEntity.posY, targetEntity.posZ, this.speed);
                break;
            case 1:
                pathFinder.tryMoveToXYZ(targetEntity.posX - (rand.nextInt(3) + 2), targetEntity.posY, targetEntity.posZ, this.speed);
                break;
            case 2:
                pathFinder.tryMoveToXYZ(targetEntity.posX, targetEntity.posY, targetEntity.posZ + (rand.nextInt(3) + 2), this.speed);
                break;
            case 3:
                pathFinder.tryMoveToXYZ(targetEntity.posX, targetEntity.posY, targetEntity.posZ - (rand.nextInt(3) + 2), this.speed);
                break;
        }
    }

    @Override
    public void updateTask() {
        World world = entity.world;
        if (!world.isRemote) {
            if (entity.getDistanceToEntity(targetEntity) <= 4) {
                ItemStack[] itemStacks = entity.getEntityInventory();
                for (ItemStack itemStack : itemStacks) {
                    if (itemStack != null && itemStack.getCount() > 0) {
                        EntityItem entityItem = entity.entityDropItem(itemStack, 1);
                        entityItem.setPickupDelay(80);
                    }
                }
                entity.clearInventory();
                /*switch (rand.nextInt(4)) {
                    case 0:
                        pathFinder.tryMoveToXYZ(targetEntity.posX + (rand.nextInt(6) + 2), targetEntity.posY, targetEntity.posZ + (rand.nextInt(6) + 2), this.speed);
                        break;
                    case 1:
                        pathFinder.tryMoveToXYZ(targetEntity.posX - (rand.nextInt(6) + 2), targetEntity.posY, targetEntity.posZ + (rand.nextInt(6) + 2), this.speed);
                        break;
                    case 2:
                        pathFinder.tryMoveToXYZ(targetEntity.posX + (rand.nextInt(6) + 2), targetEntity.posY, targetEntity.posZ - (rand.nextInt(6) + 2), this.speed);
                        break;
                    case 3:
                        pathFinder.tryMoveToXYZ(targetEntity.posX - (rand.nextInt(6) + 2), targetEntity.posY, targetEntity.posZ - (rand.nextInt(6) + 2), this.speed);
                        break;
                }*/
            }
        }
        if (targetEntity != null && pathFinder.noPath() && entity.getDistanceToEntity(targetEntity) > 4 && shouldContinueExecuting())
            startExecuting();
    }

    @Override
    public void resetTask() {
        alreadyExecuting = false;
        targetEntity = null;
        randPos = -1;
    }
}
