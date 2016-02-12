package fendirain.fendirain.common.entity.mob.EntityFenderium.AI;

import fendirain.fendirain.common.entity.mob.EntityFenderium.EntityFenderiumMob;
import fendirain.fendirain.utility.helper.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.BlockLog;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
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
    private Block treeTargetBlock;
    private Block currentlyBreaking;
    private Block treeLeaf;
    private boolean alreadyExecuting;
    private Set<Block> currentTree;
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
        treeTargetBlock = null;
        currentlyBreaking = null;
        treeLeaf = null;
        alreadyExecuting = false;
        timeToWaitUntilNextRun = 2400;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (alreadyExecuting || timeToWaitUntilNextRun > 0) {
            return false;
        }
        if (!entity.worldObj.isRemote && (timeToWaitUntilNextRun == 0 || rand.nextInt(1000) == 1)) {
            World world = entity.worldObj;
            int range = entity.getMaxRange();
            Block closest = null;
            double currentDist = -1.0;
            for (int y = (int) entity.posY - range; y <= (int) entity.posY + range; y++) {
                for (int x = (int) entity.posX - range; x <= (int) entity.posX + range; x++) {
                    for (int z = (int) entity.posZ - range; z <= (int) entity.posZ + range; z++) {
                        BlockPos blockPos = new BlockPos(x, y, z);
                        if (entity.isItemValidForBreaking(world.getBlockState(blockPos).getBlock()) && isTree(world, blockPos, false)) {
                            Block block = new Block(world.getBlockState(blockPos).getBlock(), blockPos, world.getBlockState(blockPos).getBlock().getDamageValue(world, blockPos));
                            double dist = entity.getDistance(x, y, z);
                            if (closest == null || currentDist == -1 || dist < currentDist) {
                                currentDist = dist;
                                closest = block;
                            }
                        }
                    }
                }
            }
            if (closest != null) {
                isTree(world, closest.getBlockPos(), true);
                treeTargetBlock = closest;
                return true;
            }
        }
        return false;
    }

    private boolean isTree(World world, BlockPos blockPos, Boolean setTreeLeaf) {
        net.minecraft.block.Block block = world.getBlockState(blockPos.down()).getBlock();
        if (block != Blocks.air && (block instanceof BlockLog || block instanceof BlockGrass || block instanceof BlockDirt)) {
            boolean isWood;
            int pos = 1;
            do {
                if (entity.isItemValidForBreaking(world.getBlockState(blockPos.up(pos)).getBlock())) {
                    pos++;
                    isWood = true;
                } else {
                    if (world.getBlockState(blockPos.up(pos)).getBlock() instanceof BlockLeavesBase) {
                        if (setTreeLeaf) {
                            treeLeaf = new Block(world.getBlockState(blockPos.up(pos)).getBlock(), blockPos.up(pos), world.getBlockState(blockPos.up(pos)).getBlock().getDamageValue(world, blockPos.up(pos)));
                        }
                        return true;
                    }
                    isWood = false;
                }
            } while (isWood);
        }
        return false;
    }

    private Set<Block> getAllConnectingTreeBlocks(World world, Block block, Set<String> searchedBlocks, boolean originalPass) {
        Set<Block> blocks = new LinkedHashSet<>();
        if (originalPass) blocks.add(treeTargetBlock);
        if (block.getBlockPos().getX() < treeTargetBlock.getBlockPos().getX() + sightRange && block.getBlockPos().getX() > treeTargetBlock.getBlockPos().getX() - sightRange && block.getBlockPos().getZ() < treeTargetBlock.getBlockPos().getZ() + sightRange && block.getBlockPos().getZ() > treeTargetBlock.getBlockPos().getZ() - sightRange) {
            blocks.addAll(checkBlocks(blocks, searchedBlocks, world, block.getBlockPos().up()));
            blocks.addAll(checkBlocks(blocks, searchedBlocks, world, block.getBlockPos().north()));
            blocks.addAll(checkBlocks(blocks, searchedBlocks, world, block.getBlockPos().east()));
            blocks.addAll(checkBlocks(blocks, searchedBlocks, world, block.getBlockPos().south()));
            blocks.addAll(checkBlocks(blocks, searchedBlocks, world, block.getBlockPos().west()));
            blocks.addAll(checkBlocks(blocks, searchedBlocks, world, block.getBlockPos().down()));
            blocks.addAll(checkBlocks(blocks, searchedBlocks, world, block.getBlockPos().north().east()));
            blocks.addAll(checkBlocks(blocks, searchedBlocks, world, block.getBlockPos().north().west()));
            blocks.addAll(checkBlocks(blocks, searchedBlocks, world, block.getBlockPos().north().up()));
            blocks.addAll(checkBlocks(blocks, searchedBlocks, world, block.getBlockPos().north().up().east()));
            blocks.addAll(checkBlocks(blocks, searchedBlocks, world, block.getBlockPos().north().up().west()));
            blocks.addAll(checkBlocks(blocks, searchedBlocks, world, block.getBlockPos().north().down()));
            blocks.addAll(checkBlocks(blocks, searchedBlocks, world, block.getBlockPos().north().down().east()));
            blocks.addAll(checkBlocks(blocks, searchedBlocks, world, block.getBlockPos().north().down().west()));
            blocks.addAll(checkBlocks(blocks, searchedBlocks, world, block.getBlockPos().up().east()));
            blocks.addAll(checkBlocks(blocks, searchedBlocks, world, block.getBlockPos().up().west()));
            blocks.addAll(checkBlocks(blocks, searchedBlocks, world, block.getBlockPos().down().east()));
            blocks.addAll(checkBlocks(blocks, searchedBlocks, world, block.getBlockPos().down().west()));
            blocks.addAll(checkBlocks(blocks, searchedBlocks, world, block.getBlockPos().south().east()));
            blocks.addAll(checkBlocks(blocks, searchedBlocks, world, block.getBlockPos().south().west()));
            blocks.addAll(checkBlocks(blocks, searchedBlocks, world, block.getBlockPos().south().up()));
            blocks.addAll(checkBlocks(blocks, searchedBlocks, world, block.getBlockPos().south().up().east()));
            blocks.addAll(checkBlocks(blocks, searchedBlocks, world, block.getBlockPos().south().up().west()));
            blocks.addAll(checkBlocks(blocks, searchedBlocks, world, block.getBlockPos().south().down()));
            blocks.addAll(checkBlocks(blocks, searchedBlocks, world, block.getBlockPos().south().down().east()));
            blocks.addAll(checkBlocks(blocks, searchedBlocks, world, block.getBlockPos().south().down().west()));
        }

        if (originalPass) {
            if (!blocks.isEmpty()) {
                Iterator<Block> iterator = blocks.iterator();
                Set<String> validCoords = new HashSet<>();
                while (iterator.hasNext()) {
                    Block next = iterator.next();
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
        return blocks;
    }

    private Set<Block> checkBlocks(Set<Block> blocksList, Set<String> searchedBlocks, World world, BlockPos blockPos) {
        Block block = new Block(world.getBlockState(blockPos).getBlock(), blockPos, world.getBlockState(blockPos).getBlock().getDamageValue(world, blockPos));
        if ((block.getBlock() == treeTargetBlock.getBlock() && block.getDamageValue() == treeTargetBlock.getDamageValue()) || (block.getBlock() == treeLeaf.getBlock() && block.getDamageValue() == treeLeaf.getDamageValue())) {
            if (!searchedBlocks.contains(blockPos.toString())) {
                searchedBlocks.add(blockPos.toString());
                blocksList.add(block);
                blocksList.addAll(getAllConnectingTreeBlocks(world, block, searchedBlocks, false));
            }
        }
        return blocksList;
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
        if (entity.worldObj.getBlockState(treeTargetBlock.getBlockPos()).getBlock() != treeTargetBlock.getBlock()) {
            if (currentTree != null && currentTree.contains(treeTargetBlock)) currentTree.remove(treeTargetBlock);
            treeTargetBlock = this.returnClosestLog(currentTree);
        }
        if (treeTargetBlock != null && entity.isItemValidForBreaking(entity.worldObj.getBlockState(treeTargetBlock.getBlockPos()).getBlock()) && entity.isAnySpaceForItemPickup(new ItemStack(treeTargetBlock.getBlock(), 1, treeTargetBlock.getDamageValue()))) {
            return true;
        } else {
            treeTargetBlock = null;
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
            currentTree = getAllConnectingTreeBlocks(entity.worldObj, treeTargetBlock, new HashSet<>(), true);
            treeTargetBlockProgress = new double[]{(double) 10 / currentTree.size(), Double.valueOf(currentTree.size()), 0.0d};
            alreadyExecuting = true;
        }
        pathFinder.tryMoveToXYZ(this.treeTargetBlock.getBlockPos().getX(), this.treeTargetBlock.getBlockPos().getY(), this.treeTargetBlock.getBlockPos().getZ(), this.moveSpeed);
    }

    @Override
    public void resetTask() {
        World world = entity.worldObj;
        if (currentlyBreaking != null && world.getBlockState(currentlyBreaking.getBlockPos()).getBlock() == currentlyBreaking.getBlock())
            world.sendBlockBreakProgress(entity.getEntityId(), currentlyBreaking.getBlockPos(), -1);
        if (treeTargetBlock != null && world.getBlockState(treeTargetBlock.getBlockPos()).getBlock() == treeTargetBlock.getBlock())
            world.sendBlockBreakProgress(entity.getEntityId(), treeTargetBlock.getBlockPos(), -1);
    }

    private Block returnFurthestLog(Set<Block> blocks) {
        if (!blocks.isEmpty()) {
            Block result = null;
            int dist = -1;
            ArrayList<Block> removeBlocks = new ArrayList<>();
            for (Block block : blocks) {
                if (entity.worldObj.getBlockState(block.getBlockPos()).getBlock() == treeTargetBlock.getBlock()) {
                    int blockDist = block.compareTo(treeTargetBlock);
                    if (dist == -1) {
                        dist = blockDist;
                        result = block;
                    } else if (blockDist > dist || (dist == blockDist && block.getBlockPos().getY() > result.getBlockPos().getY())) {
                        dist = blockDist;
                        result = block;
                    }
                } else removeBlocks.add(block);
            }
            removeBlocks.stream().filter(blocks::contains).forEach(blocks::remove);
            return result;
        }
        return null;
    }

    private Block returnClosestLog(Set<Block> blocks) {
        if (!blocks.isEmpty()) {
            Block result = null;
            int dist = -1;
            ArrayList<Block> removeBlocks = new ArrayList<>();
            for (Block block : blocks) {
                if (entity.worldObj.getBlockState(block.getBlockPos()).getBlock() == treeTargetBlock.getBlock()) {
                    int blockDist = block.compareTo(treeTargetBlock);
                    if (dist == -1) {
                        dist = blockDist;
                        result = block;
                    } else if (blockDist < dist) {
                        dist = blockDist;
                        result = block;
                    }
                } else removeBlocks.add(block);
            }
            removeBlocks.stream().filter(blocks::contains).forEach(blocks::remove);
            return result;
        }
        return null;
    }

    @Override
    public void updateTask() {
        if (!entity.worldObj.isRemote) {
            if (entity.getDistance(treeTargetBlock.getBlockPos().getX(), treeTargetBlock.getBlockPos().getY(), treeTargetBlock.getBlockPos().getZ()) < 2) {
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
                    if (treeTargetBlock != currentlyBreaking)
                        world.sendBlockBreakProgress(entity.getEntityId(), currentlyBreaking.getBlockPos(), breakProgress);

                    world.sendBlockBreakProgress(entity.getEntityId(), treeTargetBlock.getBlockPos(), (int) treeTargetBlockProgress[2]);
                    entity.getLookHelper().setLookPosition(treeTargetBlock.getBlockPos().getX(), treeTargetBlock.getBlockPos().getY(), treeTargetBlock.getBlockPos().getZ(), 0, 0);
                    Minecraft.getMinecraft().effectRenderer.addBlockHitEffects(treeTargetBlock.getBlockPos(), entity.getHorizontalFacing().getOpposite());

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
        NBTTagCompound nBTTagCompound = new NBTTagCompound();
        nBTTagCompound.setBoolean("alreadyExecuting", alreadyExecuting);
        if (alreadyExecuting) {
            nBTTagCompound.setInteger("currentBlockProgress", currentBlockProgress);
            nBTTagCompound.setDouble("treeTargetBlockProgress0", treeTargetBlockProgress[0]);
            nBTTagCompound.setDouble("treeTargetBlockProgress1", treeTargetBlockProgress[1]);
            nBTTagCompound.setDouble("treeTargetBlockProgress2", treeTargetBlockProgress[2]);
            nBTTagCompound.setTag("targetTreeBlock", treeTargetBlock.writeToNBT(new NBTTagCompound()));
            nBTTagCompound.setTag("treeLeafBlock", treeLeaf.writeToNBT(new NBTTagCompound()));
            NBTTagCompound blocks = new NBTTagCompound();
            final int[] i = {0};
            currentTree.forEach(block -> blocks.setTag("block" + i[0]++, block.writeToNBT(blocks)));
            nBTTagCompound.setTag("currentTree", blocks);
        }
        nBTTagCompound.setInteger("timeToWaitUntilNextRun", timeToWaitUntilNextRun);
        return nBTTagCompound;
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
            treeTargetBlock = new Block(nbtTagCompound.getCompoundTag("targetTreeBlock"));
            treeLeaf = new Block(nbtTagCompound.getCompoundTag("treeLeafBlock"));
            NBTTagCompound treeBlocks = nbtTagCompound.getCompoundTag("currentTree");
            Set<Block> currentTree = new LinkedHashSet<>(treeBlocks.getKeySet().size());
            treeBlocks.getKeySet().forEach(key -> currentTree.add(new Block(treeBlocks.getCompoundTag(key))));
            this.currentTree = currentTree;
        }
        timeToWaitUntilNextRun = nbtTagCompound.getInteger("timeToWaitUntilNextRun");
    }
}
