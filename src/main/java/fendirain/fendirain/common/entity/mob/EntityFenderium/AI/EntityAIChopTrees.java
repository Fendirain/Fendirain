package fendirain.fendirain.common.entity.mob.EntityFenderium.AI;

import fendirain.fendirain.common.entity.mob.EntityFenderium.EntityFenderiumMob;
import fendirain.fendirain.utility.LogHelper;
import fendirain.fendirain.utility.helper.BlockLocation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EntityAIChopTrees extends EntityAIBase {
    private final int breakSpeedMultiplier;
    private EntityFenderiumMob entity;
    private PathNavigate pathFinder;
    private float moveSpeed;
    private int currentProgress;
    private BlockLocation baseStumpBlock = null;
    private BlockLocation currentlyBreaking = null;
    private int treeLeafType;
    private boolean alreadyExecuting = false;
    private ArrayList<BlockLocation> currentTreeBlocks;
    private ArrayList<String> searchedList;

    public EntityAIChopTrees(EntityFenderiumMob entity, float moveSpeed, int breakSpeedMultiplier) {
        this.entity = entity;
        this.moveSpeed = moveSpeed;
        this.breakSpeedMultiplier = breakSpeedMultiplier;
        this.pathFinder = entity.getNavigator();
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (!pathFinder.noPath() || alreadyExecuting) {
            return false;
        }
        if (!entity.worldObj.isRemote) {
            World world = entity.worldObj;
            int maxRange = entity.getMaxRange(), maxNegativeRange = -maxRange, posX = (int) entity.posX, posY = (int) entity.posY, posZ = (int) entity.posZ;
            for (int y = maxNegativeRange + 8; y <= maxRange - 8; y++) {
                for (int x = maxNegativeRange; x <= maxRange; x++) {
                    for (int z = maxNegativeRange; z <= maxRange; z++) {
                        if (entity.isItemValidForBreaking(world.getBlock(posX + x, posY + y, posZ + z)) && isTree(world, posX + x, posY + y, posZ + z)) {
                            Block block = world.getBlock(posX + x, posY + y, posZ + z);
                            baseStumpBlock = new BlockLocation(block, posX + x, posY + y, posZ + z, block.getDamageValue(world, posX + x, posY + y, posZ + z));
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isTree(World world, int posX, int posY, int posZ) {
        boolean isWood;
        int pos = 1;
        do {
            if (entity.isItemValidForBreaking(world.getBlock(posX, posY + pos, posZ))) {
                pos++;
                isWood = true;
            } else {
                if (world.getBlock(posX, posY + pos, posZ) instanceof BlockLeaves) {
                    treeLeafType = world.getBlock(posX, posY + pos, posZ).getDamageValue(world, posX, posY + pos, posZ);
                    return true;
                }
                isWood = false;
            }
        } while (isWood);
        return false;
    }

    private void getAllConnectingTreeBlocks(World world, int posX, int posY, int posZ) {
        if (currentTreeBlocks == null) {
            currentTreeBlocks = new ArrayList<BlockLocation>();
        }
        if (searchedList == null) {
            searchedList = new ArrayList<String>();
        }

        if (!(posX > baseStumpBlock.getPosX() + 8 && posX < baseStumpBlock.getPosX() - 8 && posZ > baseStumpBlock.getPosZ() + 8 && posZ < baseStumpBlock.getPosZ() - 8)) {
            checkBlocks(world, posX + 1, posY, posZ);
            checkBlocks(world, posX - 1, posY, posZ);
            checkBlocks(world, posX, posY + 1, posZ);
            checkBlocks(world, posX, posY - 1, posZ);
            checkBlocks(world, posX, posY, posZ + 1);
            checkBlocks(world, posX, posY, posZ - 1);
        }
    }

    private void checkBlocks(World world, int posX, int posY, int posZ) {
        Block block = world.getBlock(posX, posY, posZ);
        int damageValue = block.getDamageValue(world, posX, posY, posZ);
        if ((block == baseStumpBlock.getBlock() && block.getDamageValue(world, posX, posY, posZ) == baseStumpBlock.getDamageValue()) || (block instanceof BlockLeaves && treeLeafType == damageValue)) {
            BlockLocation blockLocation = new BlockLocation(block, posX, posY, posZ, block.getDamageValue(world, posX, posY, posZ));
            if (!searchedList.contains(posX + "." + posY + "." + posZ)) {
                searchedList.add(posX + "." + posY + "." + posZ);
                currentTreeBlocks.add(blockLocation);
                getAllConnectingTreeBlocks(world, posX, posY, posZ);
            }
        }
    }

    @Override
    public boolean continueExecuting() {
        if (entity.isItemValidForBreaking(entity.worldObj.getBlock(baseStumpBlock.getPosX(), baseStumpBlock.getPosY(), baseStumpBlock.getPosZ()))) {
            return true;
        } else {
            baseStumpBlock = null;
            currentTreeBlocks = null;
            searchedList = null;
            alreadyExecuting = false;
            treeLeafType = -1;
            return false;
        }
    }

    @Override
    public void startExecuting() {
        pathFinder.tryMoveToXYZ(this.baseStumpBlock.getPosX(), this.baseStumpBlock.getPosY(), this.baseStumpBlock.getPosZ(), this.moveSpeed);
    }

    @Override
    public void resetTask() {
        World world = entity.worldObj;
        if (baseStumpBlock != null) {
            if (entity.isItemValidForBreaking(world.getBlock(baseStumpBlock.getPosX(), baseStumpBlock.getPosY(), baseStumpBlock.getPosZ()))) {
                boolean keepChecking;
                int above = 1;
                do {
                    if (entity.isItemValidForBreaking(world.getBlock(baseStumpBlock.getPosX(), baseStumpBlock.getPosY() + above, baseStumpBlock.getPosZ()))) {
                        above++;
                        keepChecking = true;
                    } else {
                        above--;
                        keepChecking = false;
                    }
                } while (keepChecking);
                world.destroyBlockInWorldPartially(entity.getEntityId(), baseStumpBlock.getPosX(), baseStumpBlock.getPosY() + above, baseStumpBlock.getPosZ(), -1);
            }
        }
    }

    private BlockLocation returnFurthestLog() {
        World world = entity.worldObj;
        int posX = baseStumpBlock.getPosX(), posY = baseStumpBlock.getPosY(), posZ = baseStumpBlock.getPosZ();
        if (entity.isItemValidForBreaking(world.getBlock(posX, posY, posZ))) {
            if (currentTreeBlocks == null) {
                getAllConnectingTreeBlocks(world, posX, posY, posZ);
                if (currentTreeBlocks != null) {
                    ArrayList<BlockLocation> logsOnly = new ArrayList<BlockLocation>();
                    for (BlockLocation blockLocation : currentTreeBlocks) {
                        if (blockLocation.getBlock() instanceof BlockLog) {
                            logsOnly.add(blockLocation);
                        }
                    }
                    currentTreeBlocks = logsOnly;
                } else return null;
            }
            BlockLocation result = null;
            for (BlockLocation blockLocation2 : currentTreeBlocks) {
                if (result == null || blockLocation2.getPosY() > result.getPosY() && (currentTreeBlocks.size() == 1 || (blockLocation2.getPosX() != baseStumpBlock.getPosX() && blockLocation2.getPosY() != baseStumpBlock.getPosY() && blockLocation2.getPosZ() != baseStumpBlock.getPosZ()))) {
                    result = blockLocation2;
                }
            }
            return result;
        }
        return null;
    }

    @Override
    public void updateTask() {
        if (!entity.worldObj.isRemote) {
            if (entity.getDistance(baseStumpBlock.getPosX(), baseStumpBlock.getPosY(), baseStumpBlock.getPosZ()) < 2) {
                World world = entity.worldObj;
                if (currentlyBreaking == null) {
                    currentlyBreaking = this.returnFurthestLog();
                }
                if (currentlyBreaking != null) {
                    currentProgress = currentProgress + 1 + Math.abs(this.breakSpeedMultiplier);
                    int breakProgress = (int) ((float) this.currentProgress / 240.0F * 10.0F);
                    world.destroyBlockInWorldPartially(entity.getEntityId(), currentlyBreaking.getPosX(), currentlyBreaking.getPosY(), currentlyBreaking.getPosZ(), breakProgress);
                    if (this.currentProgress >= 240) {
                        world.destroyBlockInWorldPartially(entity.getEntityId(), currentlyBreaking.getPosX(), currentlyBreaking.getPosY(), currentlyBreaking.getPosZ(), -1);
                        world.setBlockToAir(currentlyBreaking.getPosX(), currentlyBreaking.getPosY(), currentlyBreaking.getPosZ());
                        world.playSoundAtEntity(entity, "dig.wood", 2, .5F);
                        if (currentTreeBlocks != null && currentTreeBlocks.contains(currentlyBreaking)) {
                            currentTreeBlocks.remove(currentlyBreaking);
                        }
                        LogHelper.info(baseStumpBlock.getDamageValue());
                        currentlyBreaking = null;
                        currentProgress = 0;
                    } else if (currentProgress % 4 == 0) {
                        world.playSoundAtEntity(entity, "dig.wood", 1, 1);
                    }
                }
            } else if (pathFinder.noPath()) {
                startExecuting();
            }
        }
    }


    public boolean isAlreadyExecuting() {
        return alreadyExecuting;
    }

    @Override
    public boolean isInterruptible() {
        return true;
    }
}
