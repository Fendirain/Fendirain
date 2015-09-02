package fendirain.fendirain.common.entity.mob.EntityFenderium.AI;

import fendirain.fendirain.common.entity.mob.EntityFenderium.EntityFenderiumMob;
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
    private int[] baseStumpBlock = null;
    private BlockLocation currentlyBreaking = null;
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
                            baseStumpBlock = new int[3];
                            baseStumpBlock[0] = posX + x;
                            baseStumpBlock[1] = posY + y;
                            baseStumpBlock[2] = posZ + z;
                            alreadyExecuting = true;
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
        Block block = world.getBlock(posX + 1, posY, posZ);
        if (block instanceof BlockLog || block instanceof BlockLeaves) {
            BlockLocation blockLocation = new BlockLocation(block, posX + 1, posY, posZ);
            if (!searchedList.contains((posX + 1) + "." + posY + "." + posZ)) {
                searchedList.add((posX + 1) + "." + posY + "." + posZ);
                currentTreeBlocks.add(blockLocation);
                getAllConnectingTreeBlocks(world, posX + 1, posY, posZ);
            }
        }

        block = world.getBlock(posX - 1, posY, posZ);
        if (block instanceof BlockLog || block instanceof BlockLeaves) {
            BlockLocation blockLocation = new BlockLocation(block, posX - 1, posY, posZ);
            if (!searchedList.contains((posX - 1) + "." + posY + "." + posZ)) {
                searchedList.add((posX - 1) + "." + posY + "." + posZ);
                currentTreeBlocks.add(blockLocation);
                getAllConnectingTreeBlocks(world, posX - 1, posY, posZ);
            }
        }

        block = world.getBlock(posX, posY + 1, posZ);
        if (block instanceof BlockLog || block instanceof BlockLeaves) {
            BlockLocation blockLocation = new BlockLocation(block, posX, posY + 1, posZ);
            if (!searchedList.contains(posX + "." + (posY + 1) + "." + posZ)) {
                searchedList.add(posX + "." + (posY + 1) + "." + posZ);
                currentTreeBlocks.add(blockLocation);
                getAllConnectingTreeBlocks(world, posX, posY + 1, posZ);
            }
        }

        block = world.getBlock(posX, posY - 1, posZ);
        if (block instanceof BlockLog || block instanceof BlockLeaves) {
            BlockLocation blockLocation = new BlockLocation(block, posX, posY - 1, posZ);
            if (!searchedList.contains(posX + "." + (posY - 1) + "." + posZ)) {
                searchedList.add(posX + "." + (posY - 1) + "." + posZ);
                currentTreeBlocks.add(blockLocation);
                getAllConnectingTreeBlocks(world, posX, posY - 1, posZ);
            }
        }

        block = world.getBlock(posX, posY, posZ + 1);
        if (block instanceof BlockLog || block instanceof BlockLeaves) {
            BlockLocation blockLocation = new BlockLocation(block, posX, posY, posZ + 1);
            if (!searchedList.contains(posX + "." + posY + "." + (posZ + 1))) {
                searchedList.add(posX + "." + posY + "." + (posZ + 1));
                currentTreeBlocks.add(blockLocation);
                getAllConnectingTreeBlocks(world, posX, posY, posZ + 1);
            }
        }

        block = world.getBlock(posX, posY, posZ - 1);
        if (block instanceof BlockLog || block instanceof BlockLeaves) {
            BlockLocation blockLocation = new BlockLocation(block, posX, posY, posZ - 1);
            if (!searchedList.contains(posX + "." + posY + "." + (posZ - 1))) {
                searchedList.add(posX + "." + posY + "." + (posZ - 1));
                currentTreeBlocks.add(blockLocation);
                getAllConnectingTreeBlocks(world, posX, posY, posZ - 1);
            }
        }
    }

    @Override
    public boolean continueExecuting() {
        if (entity.isItemValidForBreaking(entity.worldObj.getBlock(baseStumpBlock[0], baseStumpBlock[1], baseStumpBlock[2]))) {
            return true;
        } else {
            baseStumpBlock = null;
            currentTreeBlocks = null;
            searchedList = null;
            alreadyExecuting = false;
            return false;
        }
    }

    @Override
    public void startExecuting() {
        pathFinder.tryMoveToXYZ(this.baseStumpBlock[0], this.baseStumpBlock[1], this.baseStumpBlock[2], this.moveSpeed);
    }

    @Override
    public void resetTask() {
        World world = entity.worldObj;
        if (baseStumpBlock != null) {
            if (entity.isItemValidForBreaking(world.getBlock(baseStumpBlock[0], baseStumpBlock[1], baseStumpBlock[2]))) {
                boolean keepChecking;
                int above = 1;
                do {
                    if (entity.isItemValidForBreaking(world.getBlock(baseStumpBlock[0], baseStumpBlock[1] + above, baseStumpBlock[2]))) {
                        above++;
                        keepChecking = true;
                    } else {
                        above--;
                        keepChecking = false;
                    }
                } while (keepChecking);
                world.destroyBlockInWorldPartially(entity.getEntityId(), baseStumpBlock[0], baseStumpBlock[1] + above, baseStumpBlock[2], -1);
            }
        }
    }

    private BlockLocation returnFurthestLog() {
        World world = entity.worldObj;
        int posX = baseStumpBlock[0], posY = baseStumpBlock[1], posZ = baseStumpBlock[2];
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
                if (result == null || blockLocation2.getPosY() > result.getPosY()) {
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
            if (entity.getDistance(baseStumpBlock[0], baseStumpBlock[1], baseStumpBlock[2]) < 2) {
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
                        if (currentTreeBlocks.contains(currentlyBreaking)) {
                            currentTreeBlocks.remove(currentlyBreaking);
                        }
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
        return false;
    }
}
