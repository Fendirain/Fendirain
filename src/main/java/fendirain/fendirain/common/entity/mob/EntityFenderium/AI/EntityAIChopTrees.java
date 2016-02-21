package fendirain.fendirain.common.entity.mob.EntityFenderium.AI;

import fendirain.fendirain.common.entity.mob.EntityFenderium.EntityFenderiumMob;
import fendirain.fendirain.utility.helper.FullBlock;
import fendirain.fendirain.utility.tools.TreeChecker;
import fendirain.fendirain.utility.tools.TreeChopper;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.Random;

public class EntityAIChopTrees extends EntityAIBase {
    private final int maxLogRange, timePer, sightRange;
    private final boolean doTimePerLog;
    private final Random rand;
    private final EntityFenderiumMob entity;
    private final PathNavigate pathFinder;
    private final float moveSpeed;
    TreeChopper treeChopper;
    private boolean alreadyExecuting;
    private int timeToWaitUntilNextRun;
    private boolean reloaded = false;

    public EntityAIChopTrees(EntityFenderiumMob entity, Random rand, int sightRange, float moveSpeed, boolean doTimePerLog, int timePer) {
        this.maxLogRange = 3;
        this.entity = entity;
        this.rand = rand;
        this.sightRange = sightRange;
        this.moveSpeed = moveSpeed;
        this.doTimePerLog = doTimePerLog;
        this.timePer = timePer;
        this.pathFinder = entity.getNavigator();
        alreadyExecuting = false;
        timeToWaitUntilNextRun = 2400;
        treeChopper = null;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (reloaded) return true;
        if (alreadyExecuting || timeToWaitUntilNextRun > 0) {
            return false;
        }
        if (!entity.worldObj.isRemote && (timeToWaitUntilNextRun == 0 || rand.nextInt(1000) == 1)) {
            World world = entity.worldObj;
            int range = entity.getMaxRange();
            FullBlock closest = null;
            double currentDist = -1.0;

            for (int y = (int) entity.posY - range; y <= (int) entity.posY + range; y++) {
                for (int x = (int) entity.posX - range; x <= (int) entity.posX + range; x++) {
                    for (int z = (int) entity.posZ - range; z <= (int) entity.posZ + range; z++) {
                        BlockPos blockPos = new BlockPos(x, y, z);
                        if (entity.isItemValidForBreaking(world.getBlockState(blockPos).getBlock()) && entity.isAnySpaceForItemPickup(new ItemStack(world.getBlockState(blockPos).getBlock(), 1))) {
                            Vec3 blockVec = new Vec3(x, y, z).subtract(entity.getPositionVector());
                            Vec3 lookVec = entity.getLookVec();
                            double degree = Math.acos(((blockVec.xCoord * lookVec.xCoord) + (blockVec.yCoord * lookVec.yCoord) + (blockVec.zCoord * lookVec.zCoord)) / (Math.sqrt((blockVec.xCoord * blockVec.xCoord) + (blockVec.yCoord * blockVec.yCoord) + (blockVec.zCoord * blockVec.zCoord)) * Math.sqrt((lookVec.xCoord * lookVec.xCoord) + (lookVec.yCoord * lookVec.yCoord) + (lookVec.zCoord * lookVec.zCoord)))) * 180 / Math.PI;
                            if (degree < 80 && TreeChecker.isTree(world, blockPos) != null) {
                                FullBlock fullBlock = new FullBlock(world.getBlockState(blockPos).getBlock(), blockPos, world.getBlockState(blockPos).getBlock().getDamageValue(world, blockPos));
                                double dist = entity.getDistance(x, y, z);
                                PathEntity pathEntity = pathFinder.getPathToXYZ(x, y, z);
                                if (pathEntity == null) break;
                                BlockPos blockPos1 = new BlockPos(pathEntity.getFinalPathPoint().xCoord, pathEntity.getFinalPathPoint().yCoord, pathEntity.getFinalPathPoint().zCoord);
                                boolean canReach = fullBlock.compareTo(new FullBlock(world.getBlockState(blockPos1).getBlock(), blockPos1, world.getBlockState(blockPos1).getBlock().getDamageValue(world, blockPos1))) <= 2;
                                if (canReach && (closest == null || currentDist == -1 || dist < currentDist)) {
                                    currentDist = dist;
                                    closest = fullBlock;
                                }
                            }
                        }
                    }
                }
            }
            if (closest != null) {
                treeChopper = new TreeChopper(entity, closest, TreeChecker.isTree(world, closest.getBlockPos()));
                return true;
            } else timeToWaitUntilNextRun = timeToWaitUntilNextRun + 50;
        }
        return false;
    }

    @Override
    public boolean continueExecuting() {
        if (treeChopper == null) return false;
        if (entity.worldObj.getBlockState(treeChopper.getMainBlock().getBlockPos()).getBlock() != treeChopper.getMainBlock().getBlock()) {
            treeChopper.setMainBlockToClosest();
        }
        return !treeChopper.isFinished();
    }

    @Override
    public void startExecuting() {
        if (!alreadyExecuting) {
            alreadyExecuting = true;
        } else if (reloaded) {
            //currentTree = getAllConnectingTreeBlocks(entity.worldObj, treeTargetFullBlock, new HashSet<>(), true);
            reloaded = false;
        }
        pathFinder.tryMoveToXYZ(treeChopper.getMainBlock().getBlockPos().getX(), treeChopper.getMainBlock().getBlockPos().getY(), treeChopper.getMainBlock().getBlockPos().getZ(), this.moveSpeed);
    }

    @Override
    public void resetTask() {
        if (treeChopper != null) {
            treeChopper.resetBlockProgress();
            treeChopper = null;
            alreadyExecuting = false;
        }

        for (Object potion : entity.getActivePotionEffects()) {
            PotionEffect potionEffect = (PotionEffect) potion;
            if (potionEffect.getPotionID() == 3) {
                entity.removePotionEffect(3);
                break;
            }
        }
    }

    @Override
    public void updateTask() {
        if (!entity.worldObj.isRemote) {
            if (entity.getDistance(treeChopper.getMainBlock().getBlockPos().getX(), treeChopper.getMainBlock().getBlockPos().getY(), treeChopper.getMainBlock().getBlockPos().getZ()) < 2) {
                //LogHelper.info("Ran");
                ItemStack itemStack = treeChopper.continueBreaking(entity.getBreakSpeed());
                if (itemStack != null) {
                    entity.putIntoInventory(itemStack.copy());
                    if (doTimePerLog) {
                        if (timeToWaitUntilNextRun < 0) {
                            timeToWaitUntilNextRun = 0;
                        }
                        timeToWaitUntilNextRun += timePer * itemStack.stackSize;
                    } else if (timeToWaitUntilNextRun != timePer) {
                        timeToWaitUntilNextRun = timePer;
                    }
                }
                if (treeChopper.isFinished() || (itemStack != null && !entity.isAnySpaceForItemPickup(itemStack)))
                    resetTask();
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

    public NBTTagCompound writeToNBT() {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        nbtTagCompound.setBoolean("alreadyExecuting", alreadyExecuting);
        if (alreadyExecuting) {
            /*nbtTagCompound.setInteger("currentBlockProgress", currentBlockProgress);
            nbtTagCompound.setDouble("treeTargetBlockProgress0", treeTargetBlockProgress[0]);
            nbtTagCompound.setDouble("treeTargetBlockProgress1", treeTargetBlockProgress[1]);
            nbtTagCompound.setDouble("treeTargetBlockProgress2", treeTargetBlockProgress[2]);
            nbtTagCompound.setTag("targetTreeBlock", treeTargetFullBlock.writeToNBT(new NBTTagCompound()));
            nbtTagCompound.setTag("treeLeafBlock", treeLeaf.writeToNBT(new NBTTagCompound()));*/
           /* NBTTagCompound blocks = new NBTTagCompound();
            final int[] i = {0};
            currentTree.forEach(block -> blocks.setTag("block" + i[0]++, block.writeToNBT(blocks)));
            nbtTagCompound.setTag("currentTree", blocks);*/
        }
        nbtTagCompound.setInteger("timeToWaitUntilNextRun", timeToWaitUntilNextRun);
        return nbtTagCompound;
    }

    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        /*alreadyExecuting = nbtTagCompound.getBoolean("alreadyExecuting");
        if (alreadyExecuting) {
            currentBlockProgress = nbtTagCompound.getInteger("currentBlockProgress");
            double[] temp = new double[3];
            temp[0] = nbtTagCompound.getDouble("treeTargetBlockProgress0");
            temp[1] = nbtTagCompound.getDouble("treeTargetBlockProgress1");
            temp[2] = nbtTagCompound.getDouble("treeTargetBlockProgress2");
            treeTargetBlockProgress = temp;
            treeTargetFullBlock = new FullBlock(nbtTagCompound.getCompoundTag("targetTreeBlock"));
            treeLeaf = new FullBlock(nbtTagCompound.getCompoundTag("treeLeafBlock"));
            *//*NBTTagCompound treeBlocks = nbtTagCompound.getCompoundTag("currentTree");
            Set<FullBlock> currentTree = new LinkedHashSet<>(treeBlocks.getKeySet().size());
            treeBlocks.getKeySet().forEach(key -> currentTree.add(new FullBlock(treeBlocks.getCompoundTag(key))));
            this.currentTree = currentTree;*//*
            //reloaded = true;
        }*/
        timeToWaitUntilNextRun = nbtTagCompound.getInteger("timeToWaitUntilNextRun");
    }
}
