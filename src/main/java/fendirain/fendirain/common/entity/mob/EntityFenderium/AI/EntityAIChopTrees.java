package fendirain.fendirain.common.entity.mob.EntityFenderium.AI;

import fendirain.fendirain.common.entity.mob.EntityFenderium.EntityFenderiumMob;
import fendirain.fendirain.reference.ConfigValues;
import fendirain.fendirain.utility.helper.BlockLocation;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class EntityAIChopTrees extends EntityAIBase {
    private final int maxLogRange, timePer, sightRange;
    private final boolean doTimePerLog;
    private final Random rand;
    private final EntityFenderiumMob entity;
    private final PathNavigate pathFinder;
    private final float moveSpeed;
    private int currentProgress;
    private BlockLocation baseStumpBlock;
    private BlockLocation currentlyBreaking;
    private BlockLocation treeLeaf;
    private boolean alreadyExecuting;
    private ArrayList<BlockLocation> currentTreeBlocks;
    private ArrayList<BlockLocation> tempBlocksList;
    private ArrayList<String> searchedList;
    private int timeToWaitUntilNextRun;

    public EntityAIChopTrees(EntityFenderiumMob entity, Random rand, int sightRange, float moveSpeed, boolean doTimePerLog, int timePer) {
        this.maxLogRange = 3;
        this.entity = entity;
        this.rand = rand;
        this.sightRange = sightRange;
        this.moveSpeed = moveSpeed;
        this.doTimePerLog = doTimePerLog;
        this.timePer = timePer;
        this.pathFinder = entity.getNavigator();
        baseStumpBlock = null;
        currentlyBreaking = null;
        treeLeaf = null;
        alreadyExecuting = false;
        timeToWaitUntilNextRun = -3000;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (!pathFinder.noPath() || alreadyExecuting || timeToWaitUntilNextRun > 0) {
            return false;
        }
        if (!entity.worldObj.isRemote && (timeToWaitUntilNextRun <= -6000 || rand.nextInt(1000) == 1)) {
            World world = entity.worldObj;
            int range = entity.getMaxRange();
            BlockLocation closest = null;
            double currentDist = -1.0;
            for (int y = (int) entity.posY - range; y <= (int) entity.posY + range; y++) {
                for (int x = (int) entity.posX - range; x <= (int) entity.posX + range; x++) {
                    for (int z = (int) entity.posZ - range; z <= (int) entity.posZ + range; z++) {
                        if (entity.isItemValidForBreaking(world.getBlock(x, y, z)) && isTree(world, x, y, z, false)) {
                            BlockLocation blockLocation = new BlockLocation(world.getBlock(x, y, z), x, y, z, world.getBlock(x, y, z).getDamageValue(world, x, y, z));
                            double dist = entity.getDistance(x, y, z);
                            if (closest == null || currentDist == -1 || dist < currentDist) {
                                currentDist = dist;
                                closest = blockLocation;
                            }
                        }
                    }
                }
            }
            if (closest != null) {
                isTree(world, closest.getPosX(), closest.getPosY(), closest.getPosZ(), true);
                baseStumpBlock = closest;
                return true;
            }
        }
        return false;
    }

    private boolean isTree(World world, int posX, int posY, int posZ, Boolean setTreeLeaf) {
        if (world.getBlock(posX, posY - 1, posZ) != Blocks.air) {
            boolean isWood;
            int pos = 1;
            do {
                if (entity.isItemValidForBreaking(world.getBlock(posX, posY + pos, posZ))) {
                    pos++;
                    isWood = true;
                } else {
                    if (world.getBlock(posX, posY + pos, posZ) instanceof BlockLeaves) {
                        if (setTreeLeaf) {
                            treeLeaf = new BlockLocation(world.getBlock(posX, posY + pos, posZ), posX, posY + pos, posZ, world.getBlock(posX, posY + pos, posZ).getDamageValue(world, posX, posY + pos, posZ));
                        }
                        return true;
                    }
                    isWood = false;
                }
            } while (isWood);
        }
        return false;
    }

    private void getAllConnectingTreeBlocks(World world, int posX, int posY, int posZ, boolean originalPass) {
        if (currentTreeBlocks == null) {
            currentTreeBlocks = new ArrayList<BlockLocation>();
        }
        if (tempBlocksList == null) {
            tempBlocksList = new ArrayList<BlockLocation>();
        }
        if (searchedList == null) {
            searchedList = new ArrayList<String>();
        }

        if (posX < baseStumpBlock.getPosX() + sightRange && posX > baseStumpBlock.getPosX() - sightRange && posZ < baseStumpBlock.getPosZ() + sightRange && posZ > baseStumpBlock.getPosZ() - sightRange) {
            checkBlocks(world, posX + 1, posY, posZ);
            checkBlocks(world, posX - 1, posY, posZ);
            checkBlocks(world, posX, posY + 1, posZ);
            checkBlocks(world, posX, posY - 1, posZ);
            checkBlocks(world, posX, posY, posZ + 1);
            checkBlocks(world, posX, posY, posZ - 1);
            checkBlocks(world, posX + 1, posY + 1, posZ);
            checkBlocks(world, posX + 1, posY - 1, posZ);
            checkBlocks(world, posX + 1, posY, posZ + 1);
            checkBlocks(world, posX + 1, posY, posZ - 1);
            checkBlocks(world, posX - 1, posY + 1, posZ);
            checkBlocks(world, posX - 1, posY - 1, posZ);
            checkBlocks(world, posX - 1, posY, posZ + 1);
            checkBlocks(world, posX - 1, posY, posZ - 1);
            checkBlocks(world, posX, posY + 1, posZ + 1);
            checkBlocks(world, posX, posY + 1, posZ - 1);
            checkBlocks(world, posX, posY - 1, posZ + 1);
            checkBlocks(world, posX, posY - 1, posZ - 1);
        }

        if (originalPass) {
            if (tempBlocksList != null) {
                ArrayList<BlockLocation> logsOnly = new ArrayList<BlockLocation>();
                for (BlockLocation blockLocation : tempBlocksList) {
                    if (blockLocation.getBlock() instanceof BlockLog) {
                        logsOnly.add(blockLocation);
                    }
                }
                Collections.sort(logsOnly);
                ArrayList<BlockLocation> validLogs = new ArrayList<BlockLocation>();
                validLogs.add(baseStumpBlock);
                logsOnly.remove(baseStumpBlock);
                ArrayList<String> validCoords = new ArrayList<String>();
                for (String coord : this.getBlockAreaCoords(baseStumpBlock.getPosX(), baseStumpBlock.getPosY(), baseStumpBlock.getPosZ())) {
                    validCoords.add(coord);
                }
                ArrayList<BlockLocation> added = new ArrayList<BlockLocation>();
                for (BlockLocation block : logsOnly) {
                    if (validCoords.contains(block.getPosX() + "." + block.getPosY() + "." + block.getPosZ())) {
                        validLogs.add(block);
                        added.add(block);
                        for (String coord : this.getBlockAreaCoords(block.getPosX(), block.getPosY(), block.getPosZ())) {
                            if (!validCoords.contains(coord)) {
                                validCoords.add(coord);
                            }
                        }
                    }
                }
                for (BlockLocation remove : added) {
                    if (logsOnly.contains(remove)) {
                        logsOnly.remove(remove);
                    }
                }

                currentTreeBlocks = validLogs;
            }
        }
    }

    private ArrayList<String> getBlockAreaCoords(int posX, int posY, int posZ) {
        ArrayList<String> result = new ArrayList<String>();
        for (int y = posY - maxLogRange; y <= posY + maxLogRange; y++) {
            for (int x = posX - maxLogRange; x <= posX + maxLogRange; x++) {
                for (int z = posZ - maxLogRange; z <= posZ + maxLogRange; z++) {
                    result.add(x + "." + y + "." + z);
                }
            }
        }
        return result;
    }

    private void checkBlocks(World world, int posX, int posY, int posZ) {
        BlockLocation block = new BlockLocation(world.getBlock(posX, posY, posZ), posX, posY, posZ, world.getBlock(posX, posY, posZ).getDamageValue(world, posX, posY, posZ));
        if ((block.getBlock() == baseStumpBlock.getBlock() && block.getDamageValue() == baseStumpBlock.getDamageValue()) || (block.getBlock() == treeLeaf.getBlock() && block.getDamageValue() == treeLeaf.getDamageValue())) {
            if (!searchedList.contains(posX + "." + posY + "." + posZ)) {
                searchedList.add(posX + "." + posY + "." + posZ);
                tempBlocksList.add(block);
                getAllConnectingTreeBlocks(world, posX, posY, posZ, false);
            }
        }
    }

    @Override
    public boolean continueExecuting() {
        if (baseStumpBlock != null && entity.isItemValidForBreaking(entity.worldObj.getBlock(baseStumpBlock.getPosX(), baseStumpBlock.getPosY(), baseStumpBlock.getPosZ())) && entity.isAnySpaceForItemPickup(new ItemStack(baseStumpBlock.getBlock(), 1, baseStumpBlock.getDamageValue()))) {
            return true;
        } else {
            baseStumpBlock = null;
            currentTreeBlocks = null;
            tempBlocksList = null;
            searchedList = null;
            alreadyExecuting = false;
            treeLeaf = null;
            if (ConfigValues.isDebugSettingsEnabled) {
                for (Object potion : entity.getActivePotionEffects()) {
                    PotionEffect potionEffect = (PotionEffect) potion;
                    if (potionEffect.getPotionID() == 3) {
                        entity.removePotionEffect(3);
                        break;
                    }
                }
            }
            return false;
        }
    }

    @Override
    public void startExecuting() {
        if (!alreadyExecuting && currentTreeBlocks == null) {
            getAllConnectingTreeBlocks(entity.worldObj, baseStumpBlock.getPosX(), baseStumpBlock.getPosY(), baseStumpBlock.getPosZ(), true);
            alreadyExecuting = true;
        }
        pathFinder.tryMoveToXYZ(this.baseStumpBlock.getPosX(), this.baseStumpBlock.getPosY(), this.baseStumpBlock.getPosZ(), this.moveSpeed);
    }

    @Override
    public void resetTask() {
        World world = entity.worldObj;
        if (currentlyBreaking != null) {
            world.destroyBlockInWorldPartially(entity.getEntityId(), currentlyBreaking.getPosX(), currentlyBreaking.getPosY(), currentlyBreaking.getPosZ(), -1);
        }
    }

    private BlockLocation returnFurthestLog() {
        if (!currentTreeBlocks.isEmpty()) {
            BlockLocation result = null;
            int dist = -1;
            ArrayList<BlockLocation> removeBlocks = new ArrayList<BlockLocation>();
            for (BlockLocation block : currentTreeBlocks) {
                if (entity.worldObj.getBlock(block.getPosX(), block.getPosY(), block.getPosZ()) == baseStumpBlock.getBlock()) {
                    int blockDist = block.compareTo(baseStumpBlock);
                    if (dist == -1) {
                        dist = blockDist;
                        result = block;
                    } else if (blockDist > dist || (dist == blockDist && block.getPosY() > result.getPosY())) {
                        dist = blockDist;
                        result = block;
                    }
                } else removeBlocks.add(block);
            }
            for (BlockLocation remove : removeBlocks) {
                if (currentTreeBlocks.contains(remove)) {
                    currentTreeBlocks.remove(remove);
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
                if (currentlyBreaking == null || world.getBlock(currentlyBreaking.getPosX(), currentlyBreaking.getPosY(), currentlyBreaking.getPosZ()) != currentlyBreaking.getBlock()) {
                    if (currentlyBreaking != null) {
                        world.destroyBlockInWorldPartially(entity.getEntityId(), currentlyBreaking.getPosX(), currentlyBreaking.getPosY(), currentlyBreaking.getPosZ(), -1);
                        if (currentTreeBlocks != null && currentTreeBlocks.contains(currentlyBreaking)) {
                            currentTreeBlocks.remove(currentlyBreaking);
                        }
                        currentlyBreaking = null;
                        currentProgress = 0;
                    }
                    currentlyBreaking = this.returnFurthestLog();
                }
                if (currentlyBreaking != null) {
                    currentProgress = currentProgress + 1 + Math.abs(entity.getBreakSpeed());
                    int breakProgress = (int) ((float) this.currentProgress / 240.0F * 10.0F);
                    world.destroyBlockInWorldPartially(entity.getEntityId(), currentlyBreaking.getPosX(), currentlyBreaking.getPosY(), currentlyBreaking.getPosZ(), breakProgress);
                    if (this.currentProgress >= 240) {
                        ItemStack itemStack = new ItemStack(currentlyBreaking.getBlock(), 1, currentlyBreaking.getDamageValue());
                        if (entity.isAnySpaceForItemPickup(itemStack)) {
                            entity.putIntoInventory(new ItemStack(currentlyBreaking.getBlock(), 1, currentlyBreaking.getDamageValue()));
                            world.destroyBlockInWorldPartially(entity.getEntityId(), currentlyBreaking.getPosX(), currentlyBreaking.getPosY(), currentlyBreaking.getPosZ(), -1);
                            world.setBlockToAir(currentlyBreaking.getPosX(), currentlyBreaking.getPosY(), currentlyBreaking.getPosZ());
                            world.playSoundAtEntity(entity, "dig.wood", 2, .5F);
                            if (currentTreeBlocks != null && currentTreeBlocks.contains(currentlyBreaking)) {
                                currentTreeBlocks.remove(currentlyBreaking);
                            }
                            currentlyBreaking = null;
                            if (doTimePerLog) {
                                if (timeToWaitUntilNextRun < 0) {
                                    timeToWaitUntilNextRun = 0;
                                }
                                timeToWaitUntilNextRun += timePer;
                            } else if (timeToWaitUntilNextRun != timePer) {
                                timeToWaitUntilNextRun = timePer;
                            }
                            currentProgress = 0;
                        }
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

    public int getTimeToWaitUntilNextRun() {
        return timeToWaitUntilNextRun;
    }

    public void setTimeToWaitUntilNextRun(int timeToWaitUntilNextRun) {
        this.timeToWaitUntilNextRun = timeToWaitUntilNextRun;
    }
}
