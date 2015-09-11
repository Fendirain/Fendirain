package fendirain.fendirain.common.entity.mob.EntityFenderium.AI;

import fendirain.fendirain.common.entity.mob.EntityFenderium.EntityFenderiumMob;
import fendirain.fendirain.utility.helper.BlockLocation;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
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
                        BlockPos blockPos = new BlockPos(x, y, z);
                        if (entity.isItemValidForBreaking(world.getBlockState(blockPos).getBlock()) && isTree(world, blockPos, false)) {
                            BlockLocation blockLocation = new BlockLocation(world.getBlockState(blockPos).getBlock(), blockPos, world.getBlockState(blockPos).getBlock().getDamageValue(world, blockPos));
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
                isTree(world, closest.getBlockPos(), true);
                baseStumpBlock = closest;
                return true;
            }
        }
        return false;
    }

    private boolean isTree(World world, BlockPos blockPos, Boolean setTreeLeaf) {
        if (world.getBlockState(blockPos.down()).getBlock() != Blocks.air) {
            boolean isWood;
            int pos = 1;
            do {
                if (entity.isItemValidForBreaking(world.getBlockState(blockPos.up(pos)).getBlock())) {
                    pos++;
                    isWood = true;
                } else {
                    if (world.getBlockState(blockPos.up(pos)).getBlock() instanceof BlockLeaves) {
                        if (setTreeLeaf) {
                            treeLeaf = new BlockLocation(world.getBlockState(blockPos.up(pos)).getBlock(), blockPos.up(pos), world.getBlockState(blockPos.up(pos)).getBlock().getDamageValue(world, blockPos.up(pos)));
                        }
                        return true;
                    }
                    isWood = false;
                }
            } while (isWood);
        }
        return false;
    }

    private void getAllConnectingTreeBlocks(World world, BlockPos blockPos, boolean originalPass) {
        if (currentTreeBlocks == null) {
            currentTreeBlocks = new ArrayList<BlockLocation>();
        }
        if (tempBlocksList == null) {
            tempBlocksList = new ArrayList<BlockLocation>();
        }
        if (searchedList == null) {
            searchedList = new ArrayList<String>();
        }

        if (blockPos.getX() < baseStumpBlock.getBlockPos().getX() + sightRange && blockPos.getX() > baseStumpBlock.getBlockPos().getX() - sightRange && blockPos.getZ() < baseStumpBlock.getBlockPos().getZ() + sightRange && blockPos.getZ() > baseStumpBlock.getBlockPos().getZ() - sightRange) {
            checkBlocks(world, new BlockPos(blockPos.getX() + 1, blockPos.getY(), blockPos.getZ()));
            checkBlocks(world, new BlockPos(blockPos.getX() - 1, blockPos.getY(), blockPos.getZ()));
            checkBlocks(world, new BlockPos(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ()));
            checkBlocks(world, new BlockPos(blockPos.getX(), blockPos.getY() - 1, blockPos.getZ()));
            checkBlocks(world, new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ() + 1));
            checkBlocks(world, new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ() - 1));
            checkBlocks(world, new BlockPos(blockPos.getX() + 1, blockPos.getY() + 1, blockPos.getZ()));
            checkBlocks(world, new BlockPos(blockPos.getX() + 1, blockPos.getY() - 1, blockPos.getZ()));
            checkBlocks(world, new BlockPos(blockPos.getX() + 1, blockPos.getY(), blockPos.getZ() + 1));
            checkBlocks(world, new BlockPos(blockPos.getX() + 1, blockPos.getY(), blockPos.getZ() - 1));
            checkBlocks(world, new BlockPos(blockPos.getX() - 1, blockPos.getY() + 1, blockPos.getZ()));
            checkBlocks(world, new BlockPos(blockPos.getX() - 1, blockPos.getY() - 1, blockPos.getZ()));
            checkBlocks(world, new BlockPos(blockPos.getX() - 1, blockPos.getY(), blockPos.getZ() + 1));
            checkBlocks(world, new BlockPos(blockPos.getX() - 1, blockPos.getY(), blockPos.getZ() - 1));
            checkBlocks(world, new BlockPos(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ() + 1));
            checkBlocks(world, new BlockPos(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ() - 1));
            checkBlocks(world, new BlockPos(blockPos.getX(), blockPos.getY() - 1, blockPos.getZ() + 1));
            checkBlocks(world, new BlockPos(blockPos.getX(), blockPos.getY() - 1, blockPos.getZ() - 1));
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
                for (String coord : this.getBlockAreaCoords(baseStumpBlock.getBlockPos().getX(), baseStumpBlock.getBlockPos().getY(), baseStumpBlock.getBlockPos().getZ())) {
                    validCoords.add(coord);
                }
                ArrayList<BlockLocation> added = new ArrayList<BlockLocation>();
                for (BlockLocation block : logsOnly) {
                    if (validCoords.contains(block.getBlockPos().getX() + "." + block.getBlockPos().getY() + "." + block.getBlockPos().getZ())) {
                        validLogs.add(block);
                        added.add(block);
                        for (String coord : this.getBlockAreaCoords(block.getBlockPos().getX(), block.getBlockPos().getY(), block.getBlockPos().getZ())) {
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

    private void checkBlocks(World world, BlockPos blockPos) {
        BlockLocation block = new BlockLocation(world.getBlockState(blockPos).getBlock(), blockPos, world.getBlockState(blockPos).getBlock().getDamageValue(world, blockPos));
        if ((block.getBlock() == baseStumpBlock.getBlock() && block.getDamageValue() == baseStumpBlock.getDamageValue()) || (block.getBlock() == treeLeaf.getBlock() && block.getDamageValue() == treeLeaf.getDamageValue())) {
            if (!searchedList.contains(blockPos.getX() + "." + blockPos.getY() + "." + blockPos.getZ())) {
                searchedList.add(blockPos.getX() + "." + blockPos.getY() + "." + blockPos.getZ());
                tempBlocksList.add(block);
                getAllConnectingTreeBlocks(world, blockPos, false);
            }
        }
    }

    @Override
    public boolean continueExecuting() {
        if (baseStumpBlock != null && entity.isItemValidForBreaking(entity.worldObj.getBlockState(baseStumpBlock.getBlockPos()).getBlock()) && entity.isAnySpaceForItemPickup(new ItemStack(baseStumpBlock.getBlock(), 1, baseStumpBlock.getDamageValue()))) {
            return true;
        } else {
            baseStumpBlock = null;
            currentTreeBlocks = null;
            tempBlocksList = null;
            searchedList = null;
            alreadyExecuting = false;
            treeLeaf = null;
            for (Object potion : entity.getActivePotionEffects()) {
                PotionEffect potionEffect = (PotionEffect) potion;
                if (potionEffect.getPotionID() == 3) {
                    entity.removePotionEffect(3);
                    break;
                }
            }
            return false;
        }
    }

    @Override
    public void startExecuting() {
        if (!alreadyExecuting && currentTreeBlocks == null) {
            getAllConnectingTreeBlocks(entity.worldObj, baseStumpBlock.getBlockPos(), true);
            alreadyExecuting = true;
        }
        pathFinder.tryMoveToXYZ(this.baseStumpBlock.getBlockPos().getX(), this.baseStumpBlock.getBlockPos().getY(), this.baseStumpBlock.getBlockPos().getZ(), this.moveSpeed);
    }

    @Override
    public void resetTask() {
        World world = entity.worldObj;
        if (currentlyBreaking != null) {
            world.sendBlockBreakProgress(entity.getEntityId(), currentlyBreaking.getBlockPos(), -1);
        }
    }

    private BlockLocation returnFurthestLog() {
        if (!currentTreeBlocks.isEmpty()) {
            BlockLocation result = null;
            int dist = -1;
            ArrayList<BlockLocation> removeBlocks = new ArrayList<BlockLocation>();
            for (BlockLocation block : currentTreeBlocks) {
                if (entity.worldObj.getBlockState(block.getBlockPos()).getBlock() == baseStumpBlock.getBlock()) {
                    int blockDist = block.compareTo(baseStumpBlock);
                    if (dist == -1) {
                        dist = blockDist;
                        result = block;
                    } else if (blockDist > dist || (dist == blockDist && block.getBlockPos().getY() > result.getBlockPos().getY())) {
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
            if (entity.getDistance(baseStumpBlock.getBlockPos().getX(), baseStumpBlock.getBlockPos().getY(), baseStumpBlock.getBlockPos().getZ()) < 2) {
                World world = entity.worldObj;
                if (currentlyBreaking == null || world.getBlockState(currentlyBreaking.getBlockPos()).getBlock() != currentlyBreaking.getBlock()) {
                    if (currentlyBreaking != null) {
                        world.sendBlockBreakProgress(entity.getEntityId(), currentlyBreaking.getBlockPos(), -1);
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
                    world.sendBlockBreakProgress(entity.getEntityId(), currentlyBreaking.getBlockPos(), breakProgress);
                    if (this.currentProgress >= 240) {
                        ItemStack itemStack = new ItemStack(currentlyBreaking.getBlock(), 1, currentlyBreaking.getDamageValue());
                        if (entity.isAnySpaceForItemPickup(itemStack)) {
                            entity.putIntoInventory(new ItemStack(currentlyBreaking.getBlock(), 1, currentlyBreaking.getDamageValue()));
                            world.sendBlockBreakProgress(entity.getEntityId(), currentlyBreaking.getBlockPos(), -1);
                            world.setBlockToAir(new BlockPos(currentlyBreaking.getBlockPos()));
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
