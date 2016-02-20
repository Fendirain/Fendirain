package fendirain.fendirain.common.entity.mob.EntityFenderium.AI;

import fendirain.fendirain.common.entity.mob.EntityFenderium.EntityFenderiumMob;
import fendirain.fendirain.utility.helper.FullBlock;
import fendirain.fendirain.utility.helper.LogHelper;
import fendirain.fendirain.utility.tools.TreeChecker;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.BlockLog;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.*;

public class EntityAIChopTrees extends EntityAIBase {
    private final int maxLogRange, timePer, sightRange;
    private final boolean doTimePerLog;
    private final Random rand;
    private final EntityFenderiumMob entity;
    private final PathNavigate pathFinder;
    private final float moveSpeed;
    private int currentBlockProgress;
    private double[] treeTargetBlockProgress;
    private FullBlock treeTargetFullBlock;
    private FullBlock currentlyBreaking;
    private FullBlock treeLeaf;
    private boolean alreadyExecuting;
    private Set<FullBlock> currentTree;
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
        treeTargetFullBlock = null;
        currentlyBreaking = null;
        treeLeaf = null;
        alreadyExecuting = false;
        timeToWaitUntilNextRun = 2400;
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
                        Vec3 blockVector = new Vec3(x, y, z).subtract(entity.getPositionVector());
                        Vec3 lookVector = entity.getLookVec();
                        double degree = Math.acos(((blockVector.xCoord * lookVector.xCoord) + (blockVector.yCoord * lookVector.yCoord) + (blockVector.zCoord * lookVector.zCoord)) / (Math.sqrt((blockVector.xCoord * blockVector.xCoord) + (blockVector.yCoord * blockVector.yCoord) + (blockVector.zCoord * blockVector.zCoord)) * Math.sqrt((lookVector.xCoord * lookVector.xCoord) + (lookVector.yCoord * lookVector.yCoord) + (lookVector.zCoord * lookVector.zCoord)))) * 180 / Math.PI;
                        BlockPos blockPos = new BlockPos(x, y, z);
                        if (degree > 0 && degree < 80 && entity.isItemValidForBreaking(world.getBlockState(blockPos).getBlock()) && TreeChecker.isTree(world, blockPos) != null) {
                            FullBlock fullBlock = new FullBlock(world.getBlockState(blockPos).getBlock(), blockPos, world.getBlockState(blockPos).getBlock().getDamageValue(world, blockPos));
                            double dist = entity.getDistance(x, y, z);
                            if (closest == null || currentDist == -1 || dist < currentDist) {
                                currentDist = dist;
                                closest = fullBlock;
                            }
                        }
                    }
                }
            }
            if (closest != null) {
                treeLeaf = TreeChecker.isTree(world, closest.getBlockPos());
                treeTargetFullBlock = closest;
                return true;
            }
        }
        return false;
    }

    private Set<FullBlock> getAllConnectingTreeBlocks(World world, FullBlock fullBlock, Set<Long> searchedBlocks, boolean originalPass) {
        Set<FullBlock> fullBlocks = new LinkedHashSet<>();
        if (originalPass) fullBlocks.add(treeTargetFullBlock);
        if (fullBlock.getBlockPos().getX() < treeTargetFullBlock.getBlockPos().getX() + sightRange && fullBlock.getBlockPos().getX() > treeTargetFullBlock.getBlockPos().getX() - sightRange && fullBlock.getBlockPos().getZ() < treeTargetFullBlock.getBlockPos().getZ() + sightRange && fullBlock.getBlockPos().getZ() > treeTargetFullBlock.getBlockPos().getZ() - sightRange) {
            fullBlocks.addAll(checkBlocks(searchedBlocks, world, fullBlock.getBlockPos().up()));
            fullBlocks.addAll(checkBlocks(searchedBlocks, world, fullBlock.getBlockPos().north()));
            fullBlocks.addAll(checkBlocks(searchedBlocks, world, fullBlock.getBlockPos().east()));
            fullBlocks.addAll(checkBlocks(searchedBlocks, world, fullBlock.getBlockPos().south()));
            fullBlocks.addAll(checkBlocks(searchedBlocks, world, fullBlock.getBlockPos().west()));
            fullBlocks.addAll(checkBlocks(searchedBlocks, world, fullBlock.getBlockPos().down()));
            fullBlocks.addAll(checkBlocks(searchedBlocks, world, fullBlock.getBlockPos().north().east()));
            fullBlocks.addAll(checkBlocks(searchedBlocks, world, fullBlock.getBlockPos().north().west()));
            fullBlocks.addAll(checkBlocks(searchedBlocks, world, fullBlock.getBlockPos().north().up()));
            fullBlocks.addAll(checkBlocks(searchedBlocks, world, fullBlock.getBlockPos().north().up().east()));
            fullBlocks.addAll(checkBlocks(searchedBlocks, world, fullBlock.getBlockPos().north().up().west()));
            fullBlocks.addAll(checkBlocks(searchedBlocks, world, fullBlock.getBlockPos().north().down()));
            fullBlocks.addAll(checkBlocks(searchedBlocks, world, fullBlock.getBlockPos().north().down().east()));
            fullBlocks.addAll(checkBlocks(searchedBlocks, world, fullBlock.getBlockPos().north().down().west()));
            fullBlocks.addAll(checkBlocks(searchedBlocks, world, fullBlock.getBlockPos().up().east()));
            fullBlocks.addAll(checkBlocks(searchedBlocks, world, fullBlock.getBlockPos().up().west()));
            fullBlocks.addAll(checkBlocks(searchedBlocks, world, fullBlock.getBlockPos().down().east()));
            fullBlocks.addAll(checkBlocks(searchedBlocks, world, fullBlock.getBlockPos().down().west()));
            fullBlocks.addAll(checkBlocks(searchedBlocks, world, fullBlock.getBlockPos().south().east()));
            fullBlocks.addAll(checkBlocks(searchedBlocks, world, fullBlock.getBlockPos().south().west()));
            fullBlocks.addAll(checkBlocks(searchedBlocks, world, fullBlock.getBlockPos().south().up()));
            fullBlocks.addAll(checkBlocks(searchedBlocks, world, fullBlock.getBlockPos().south().up().east()));
            fullBlocks.addAll(checkBlocks(searchedBlocks, world, fullBlock.getBlockPos().south().up().west()));
            fullBlocks.addAll(checkBlocks(searchedBlocks, world, fullBlock.getBlockPos().south().down()));
            fullBlocks.addAll(checkBlocks(searchedBlocks, world, fullBlock.getBlockPos().south().down().east()));
            fullBlocks.addAll(checkBlocks(searchedBlocks, world, fullBlock.getBlockPos().south().down().west()));
        }

        if (originalPass) {
            if (!fullBlocks.isEmpty()) {
                Iterator<FullBlock> iterator = fullBlocks.iterator();
                Set<String> validCoords = new HashSet<>();
                while (iterator.hasNext()) {
                    FullBlock next = iterator.next();
                    if (next.getBlock() instanceof BlockLeavesBase) iterator.remove();
                    else {
                        if (!validCoords.isEmpty() && !validCoords.contains(next.getBlockPos().toString())) {
                            iterator.remove();
                        } else
                            this.getBlockAreaCoords(next.getBlockPos()).stream().filter(coord -> !validCoords.contains(coord)).forEach(validCoords::add);
                    }
                }
            }
        }
        return fullBlocks;
    }

    private Set<FullBlock> checkBlocks(Set<Long> searchedBlocks, World world, BlockPos blockPos) {
        Set<FullBlock> foundBlocks = new LinkedHashSet<>();
        FullBlock fullBlock = new FullBlock(world.getBlockState(blockPos).getBlock(), blockPos, world.getBlockState(blockPos).getBlock().getDamageValue(world, blockPos));
        if ((treeTargetFullBlock.isSameType(fullBlock) && fullBlock.getDamageValue() == treeTargetFullBlock.getDamageValue()) || (treeLeaf.isSameType(fullBlock) && fullBlock.getDamageValue() == treeLeaf.getDamageValue())) {
            if (!searchedBlocks.contains(blockPos.toLong())) {
                searchedBlocks.add(blockPos.toLong());
                foundBlocks.add(fullBlock);
                foundBlocks.addAll(getAllConnectingTreeBlocks(world, fullBlock, searchedBlocks, false));
            }
        }
        return foundBlocks;
    }

    private Set<String> getBlockAreaCoords(BlockPos blockPos) {
        Set<String> result = new HashSet<>();
        for (int y = blockPos.getY() - maxLogRange; y <= blockPos.getY() + maxLogRange; y++) {
            for (int x = blockPos.getX() - maxLogRange; x <= blockPos.getX() + maxLogRange; x++) {
                for (int z = blockPos.getZ() - maxLogRange; z <= blockPos.getZ() + maxLogRange; z++) {
                    result.add(new BlockPos(x, y, z).toString());
                }
            }
        }
        return result;
    }

    @Override
    public boolean continueExecuting() {
        if (entity.worldObj.getBlockState(treeTargetFullBlock.getBlockPos()).getBlock() != treeTargetFullBlock.getBlock()) {
            if (currentTree != null && currentTree.contains(treeTargetFullBlock))
                currentTree.remove(treeTargetFullBlock);
            treeTargetFullBlock = this.returnClosestLog(currentTree);
        }
        if (treeTargetFullBlock != null && entity.isItemValidForBreaking(entity.worldObj.getBlockState(treeTargetFullBlock.getBlockPos()).getBlock()) && entity.isAnySpaceForItemPickup(new ItemStack(treeTargetFullBlock.getBlock(), 1, treeTargetFullBlock.getDamageValue()))) {
            return true;
        } else {
            treeTargetFullBlock = null;
            currentTree = null;
            alreadyExecuting = false;
            treeLeaf = null;
            treeTargetBlockProgress = null;
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
        if (!alreadyExecuting) {
            currentTree = getAllConnectingTreeBlocks(entity.worldObj, treeTargetFullBlock, new HashSet<>(), true);
            treeTargetBlockProgress = new double[]{(double) 10 / currentTree.size(), Double.valueOf(currentTree.size()), 0.0d};
            alreadyExecuting = true;
        } else if (reloaded) {
            currentTree = getAllConnectingTreeBlocks(entity.worldObj, treeTargetFullBlock, new HashSet<>(), true);
            LogHelper.info("Ran");
            reloaded = false;
        }
        pathFinder.tryMoveToXYZ(this.treeTargetFullBlock.getBlockPos().getX(), this.treeTargetFullBlock.getBlockPos().getY(), this.treeTargetFullBlock.getBlockPos().getZ(), this.moveSpeed);
    }

    @Override
    public void resetTask() {
        World world = entity.worldObj;
        if (currentlyBreaking != null && world.getBlockState(currentlyBreaking.getBlockPos()).getBlock() == currentlyBreaking.getBlock())
            world.sendBlockBreakProgress(entity.getEntityId(), currentlyBreaking.getBlockPos(), -1);
        if (treeTargetFullBlock != null && world.getBlockState(treeTargetFullBlock.getBlockPos()).getBlock() == treeTargetFullBlock.getBlock())
            world.sendBlockBreakProgress(entity.getEntityId(), treeTargetFullBlock.getBlockPos(), -1);
    }

    private FullBlock returnFurthestLog(Set<FullBlock> fullBlocks) {
        if (!fullBlocks.isEmpty()) {
            FullBlock result = null;
            int dist = -1;
            ArrayList<FullBlock> removeFullBlocks = new ArrayList<>();
            for (FullBlock fullBlock : fullBlocks) {
                if (entity.worldObj.getBlockState(fullBlock.getBlockPos()).getBlock() == treeTargetFullBlock.getBlock()) {
                    int blockDist = fullBlock.compareTo(treeTargetFullBlock);
                    if (dist == -1) {
                        dist = blockDist;
                        result = fullBlock;
                    } else if (blockDist > dist || (dist == blockDist && fullBlock.getBlockPos().getY() > result.getBlockPos().getY())) {
                        dist = blockDist;
                        result = fullBlock;
                    }
                } else removeFullBlocks.add(fullBlock);
            }
            removeFullBlocks.stream().filter(fullBlocks::contains).forEach(fullBlocks::remove);
            return result;
        }
        return null;
    }

    private FullBlock returnClosestLog(Set<FullBlock> fullBlocks) {
        if (!fullBlocks.isEmpty()) {
            FullBlock result = null;
            int dist = -1;
            ArrayList<FullBlock> removeFullBlocks = new ArrayList<>();
            for (FullBlock fullBlock : fullBlocks) {
                if (entity.worldObj.getBlockState(fullBlock.getBlockPos()).getBlock() == treeTargetFullBlock.getBlock()) {
                    int blockDist = fullBlock.compareTo(treeTargetFullBlock);
                    if (dist == -1) {
                        dist = blockDist;
                        result = fullBlock;
                    } else if (blockDist < dist) {
                        dist = blockDist;
                        result = fullBlock;
                    }
                } else removeFullBlocks.add(fullBlock);
            }
            removeFullBlocks.stream().filter(fullBlocks::contains).forEach(fullBlocks::remove);
            return result;
        }
        return null;
    }

    @Override
    public void updateTask() {
        if (!entity.worldObj.isRemote) {
            if (entity.getDistance(treeTargetFullBlock.getBlockPos().getX(), treeTargetFullBlock.getBlockPos().getY(), treeTargetFullBlock.getBlockPos().getZ()) < 2) {
                World world = entity.worldObj;
                if (currentlyBreaking == null || world.getBlockState(currentlyBreaking.getBlockPos()).getBlock() != currentlyBreaking.getBlock()) {
                    if (currentlyBreaking != null) {
                        world.sendBlockBreakProgress(entity.getEntityId(), currentlyBreaking.getBlockPos(), -1);
                        if (currentTree != null && currentTree.contains(currentlyBreaking))
                            currentTree.remove(currentlyBreaking);
                        currentlyBreaking = null;
                        currentBlockProgress = 0;
                    }
                    currentlyBreaking = this.returnFurthestLog(currentTree);
                }
                if (currentlyBreaking != null) {
                    currentBlockProgress = currentBlockProgress + 1 + Math.abs(entity.getBreakSpeed());
                    int breakProgress = (int) ((float) this.currentBlockProgress / 240.0F * 10.0F);
                    //if (treeTargetFullBlock != currentlyBreaking)
                    world.sendBlockBreakProgress(entity.getEntityId(), currentlyBreaking.getBlockPos(), breakProgress);
                    //world.sendBlockBreakProgress(entity.getEntityId(), treeTargetFullBlock.getBlockPos(), (int) treeTargetBlockProgress[2]);
                    entity.getLookHelper().setLookPosition(treeTargetFullBlock.getBlockPos().getX(), treeTargetFullBlock.getBlockPos().getY(), treeTargetFullBlock.getBlockPos().getZ(), 0, 0);
                    Minecraft.getMinecraft().effectRenderer.addBlockHitEffects(treeTargetFullBlock.getBlockPos(), entity.getHorizontalFacing().getOpposite());

                    if (this.currentBlockProgress >= 240) {
                        ItemStack itemStack = new ItemStack(currentlyBreaking.getBlock(), 1, currentlyBreaking.getDamageValue());
                        if (entity.isAnySpaceForItemPickup(itemStack)) {
                            entity.putIntoInventory(new ItemStack(currentlyBreaking.getBlock(), 1, currentlyBreaking.getDamageValue()));
                            world.sendBlockBreakProgress(entity.getEntityId(), currentlyBreaking.getBlockPos(), -1);
                            Minecraft.getMinecraft().effectRenderer.addBlockDestroyEffects(currentlyBreaking.getBlockPos(), currentlyBreaking.getBlock().getDefaultState());
                            BlockLog log = (BlockLog) currentlyBreaking.getBlock();
                            log.breakBlock(world, currentlyBreaking.getBlockPos(), world.getBlockState(currentlyBreaking.getBlockPos()));
                            world.setBlockToAir(currentlyBreaking.getBlockPos());
                            world.playSoundAtEntity(entity, "dig.wood", 2, .5F);
                            if (currentTree != null) {
                                if (currentTree.contains(currentlyBreaking)) currentTree.remove(currentlyBreaking);

                                treeTargetBlockProgress[2] = (treeTargetBlockProgress[2] + ((treeTargetBlockProgress[1] - currentTree.size()) * treeTargetBlockProgress[0]));
                                treeTargetBlockProgress[1] = (double) currentTree.size();
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
                            currentBlockProgress = 0;
                        }
                    } else if (currentBlockProgress % 4 == 0) {
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

    public NBTTagCompound writeToNBT() {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        nbtTagCompound.setBoolean("alreadyExecuting", alreadyExecuting);
        if (alreadyExecuting) {
            nbtTagCompound.setInteger("currentBlockProgress", currentBlockProgress);
            nbtTagCompound.setDouble("treeTargetBlockProgress0", treeTargetBlockProgress[0]);
            nbtTagCompound.setDouble("treeTargetBlockProgress1", treeTargetBlockProgress[1]);
            nbtTagCompound.setDouble("treeTargetBlockProgress2", treeTargetBlockProgress[2]);
            nbtTagCompound.setTag("targetTreeBlock", treeTargetFullBlock.writeToNBT(new NBTTagCompound()));
            nbtTagCompound.setTag("treeLeafBlock", treeLeaf.writeToNBT(new NBTTagCompound()));
           /* NBTTagCompound blocks = new NBTTagCompound();
            final int[] i = {0};
            currentTree.forEach(block -> blocks.setTag("block" + i[0]++, block.writeToNBT(blocks)));
            nbtTagCompound.setTag("currentTree", blocks);*/
        }
        nbtTagCompound.setInteger("timeToWaitUntilNextRun", timeToWaitUntilNextRun);
        return nbtTagCompound;
    }

    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        alreadyExecuting = nbtTagCompound.getBoolean("alreadyExecuting");
        if (alreadyExecuting) {
            currentBlockProgress = nbtTagCompound.getInteger("currentBlockProgress");
            double[] temp = new double[3];
            temp[0] = nbtTagCompound.getDouble("treeTargetBlockProgress0");
            temp[1] = nbtTagCompound.getDouble("treeTargetBlockProgress1");
            temp[2] = nbtTagCompound.getDouble("treeTargetBlockProgress2");
            treeTargetBlockProgress = temp;
            treeTargetFullBlock = new FullBlock(nbtTagCompound.getCompoundTag("targetTreeBlock"));
            treeLeaf = new FullBlock(nbtTagCompound.getCompoundTag("treeLeafBlock"));
            /*NBTTagCompound treeBlocks = nbtTagCompound.getCompoundTag("currentTree");
            Set<FullBlock> currentTree = new LinkedHashSet<>(treeBlocks.getKeySet().size());
            treeBlocks.getKeySet().forEach(key -> currentTree.add(new FullBlock(treeBlocks.getCompoundTag(key))));
            this.currentTree = currentTree;*/
            reloaded = true;
        }
        timeToWaitUntilNextRun = nbtTagCompound.getInteger("timeToWaitUntilNextRun");
    }

    public double getTreeTargetBlockProgress() {
        return treeTargetBlockProgress[2];
    }

    public List<FullBlock> getBreakingBlocks() {
        List<FullBlock> list = new LinkedList<>();
        list.add(treeTargetFullBlock);
        return list;
    }
}
