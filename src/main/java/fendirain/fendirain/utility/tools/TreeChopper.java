package fendirain.fendirain.utility.tools;

import fendirain.fendirain.network.PacketHandler;
import fendirain.fendirain.network.packets.BlockHitEffectPacket;
import fendirain.fendirain.utility.helper.BlockTools;
import fendirain.fendirain.utility.helper.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.BlockLog;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.*;

public class TreeChopper {
    private final World world;
    private final Entity entity;
    private final Block treeLeaf;
    private final int treeLeafDamageValue;
    private final Block mainBlock;
    private final int mainBlockDamageValue;
    private BlockPos mainBlockPos;
    private Set<BlockPos> currentTree = new LinkedHashSet<>();
    private boolean isFinished = false;

    // These only apply's to non-player entities (Fenderium currently)
    private int currentBlockProgress;
    private double[] treeTargetBlockProgress;
    private BlockPos currentlyBreakingPos;


    public TreeChopper(Entity entity, BlockPos mainBlockPos, BlockPos treeLeaf, boolean useLeavesToCheck) {
        this.world = entity.getEntityWorld();
        this.entity = entity;
        this.mainBlockPos = mainBlockPos;
        this.mainBlock = world.getBlockState(mainBlockPos).getBlock();
        this.mainBlockDamageValue = mainBlock.getDamageValue(world, mainBlockPos);
        this.treeLeaf = useLeavesToCheck ? world.getBlockState(treeLeaf).getBlock() : null;
        this.treeLeafDamageValue = useLeavesToCheck ? this.treeLeaf.getDamageValue(world, treeLeaf) : -1;


        Long timer = System.currentTimeMillis();
        try {
            this.currentTree.addAll(getAllConnectingTreeBlocks(world, mainBlockPos, new LinkedHashSet<>(), useLeavesToCheck, true));
        } catch (StackOverflowError e) {
            LogHelper.info(e.toString());
        }
        //LogHelper.info((System.currentTimeMillis() - timer) + " MilliSeconds.");

        if (!(entity instanceof EntityPlayer)) {
            currentBlockProgress = 0;
            treeTargetBlockProgress = new double[]{(double) 10 / currentTree.size(), Double.valueOf(currentTree.size()), 0.0d};
            currentlyBreakingPos = returnFurthestLog(currentTree);
        }
    }

    public int breakAllBlocks(int maxToBreak) {
        int amountBroken = 0;
        Set<BlockPos> blocksToBreak = new LinkedHashSet<>(currentTree.size());
        BlockPos closestBlock;
        if (maxToBreak < currentTree.size()) {
            Set<BlockPos> blocksToIterate = new LinkedHashSet<>(currentTree.size());
            blocksToIterate.addAll(currentTree);
            for (int i = 0; i < maxToBreak; i++) {
                BlockPos blockToBreak = returnFurthestLog(blocksToIterate);
                blocksToBreak.add(blockToBreak);
                blocksToIterate.remove(blockToBreak);
            }
            closestBlock = returnClosestLog(blocksToBreak);
            if (closestBlock == null) closestBlock = mainBlockPos;
        } else {
            blocksToBreak.addAll(currentTree);
            closestBlock = mainBlockPos;
        }
        for (BlockPos blockPos : blocksToBreak) {
            if (world.getBlockState(blockPos).getBlock() == mainBlock) {
                world.playAuxSFX(2001, blockPos, Block.getIdFromBlock(mainBlock) + (mainBlockDamageValue << 12));
                BlockLog log = (BlockLog) mainBlock;
                log.breakBlock(world, blockPos, world.getBlockState(blockPos));
                world.setBlockToAir(blockPos);
                world.playSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), "dig.wood", 2, 5F, true);
                world.playSoundAtEntity(entity, "dig.wood", 1, .5F);
                amountBroken++;
            }
            currentTree.remove(blockPos);
        }
        EntityItem entityItem = new EntityItem(world, closestBlock.getX(), closestBlock.getY(), closestBlock.getZ());
        entityItem.setEntityItemStack(new ItemStack(mainBlock, amountBroken, mainBlockDamageValue));
        entityItem.setDefaultPickupDelay();
        world.spawnEntityInWorld(entityItem);
        if (currentTree.isEmpty()) this.isFinished = true;
        return amountBroken;
    }

    public void breakFurthestBlock() {
        BlockPos logToBreak = returnFurthestLog(currentTree);
        EntityItem entityItem = new EntityItem(world, logToBreak.getX(), logToBreak.getY(), logToBreak.getZ());
        entityItem.setEntityItemStack(new ItemStack(mainBlock, 1, mainBlockDamageValue));
        entityItem.setDefaultPickupDelay();
        world.spawnEntityInWorld(entityItem);
        world.playAuxSFX(2001, logToBreak, Block.getIdFromBlock(mainBlock) + (mainBlockDamageValue << 12));
        BlockLog log = (BlockLog) mainBlock;
        log.breakBlock(world, logToBreak, world.getBlockState(logToBreak));
        world.setBlockToAir(logToBreak);
        world.playSound(logToBreak.getX(), logToBreak.getY(), logToBreak.getZ(), "dig.wood", 2, 5F, true);
        //world.playSoundAtEntity(entity, "dig.wood", 2, .5F);
        if (currentTree.contains(logToBreak)) currentTree.remove(logToBreak);
        if (currentTree.isEmpty()) isFinished = true;
    }

    public ItemStack continueBreaking(int breakSpeed) {
        if (world.getBlockState(currentlyBreakingPos).getBlock() != mainBlock) {
            world.sendBlockBreakProgress(entity.getEntityId(), currentlyBreakingPos, -1);
            if (currentTree != null && currentTree.contains(currentlyBreakingPos))
                currentTree.remove(currentlyBreakingPos);
            currentlyBreakingPos = this.returnFurthestLog(currentTree);
            if (currentlyBreakingPos == null && currentTree.isEmpty()) {
                this.isFinished = true;
                return null;
            }
            currentBlockProgress = 0;
        }
        ItemStack itemStack = null;
        currentBlockProgress = currentBlockProgress + 1 + Math.abs(breakSpeed);
        int breakProgress = (int) ((float) this.currentBlockProgress / 240.0F * 10.0F);
        //if (mainBlock != currentlyBreakingPos)
        world.sendBlockBreakProgress(entity.getEntityId(), currentlyBreakingPos, breakProgress);
        //world.sendBlockBreakProgress(entity.getEntityId(), treeTargetFullBlock.getBlockPos(), (int) treeTargetBlockProgress[2]);
        //entity.getLookHelper().setLookPosition(treeTargetFullBlock.getBlockPos().getX(), treeTargetFullBlock.getBlockPos().getY(), treeTargetFullBlock.getBlockPos().getZ(), 0, 0);
        PacketHandler.simpleNetworkWrapper.sendToAllAround(new BlockHitEffectPacket(mainBlockPos.toLong(), entity.getHorizontalFacing().getOpposite()), new NetworkRegistry.TargetPoint(entity.dimension, mainBlockPos.getX(), mainBlockPos.getY(), mainBlockPos.getZ(), 32));
        if (this.currentBlockProgress >= 240) {
            itemStack = new ItemStack(mainBlock, 1, mainBlockDamageValue);
            world.sendBlockBreakProgress(entity.getEntityId(), currentlyBreakingPos, -1);
            world.playAuxSFX(2001, currentlyBreakingPos, Block.getIdFromBlock(mainBlock) + (mainBlockDamageValue << 12));
            BlockLog log = (BlockLog) mainBlock;
            log.breakBlock(world, currentlyBreakingPos, world.getBlockState(currentlyBreakingPos));
            world.setBlockToAir(currentlyBreakingPos);
            world.playSound(currentlyBreakingPos.getX(), currentlyBreakingPos.getY(), currentlyBreakingPos.getZ(), "dig.wood", 2, .5F, true);
            world.playSoundAtEntity(entity, "dig.wood", 2, .5F);
            if (currentTree.contains(currentlyBreakingPos)) currentTree.remove(currentlyBreakingPos);
            treeTargetBlockProgress[2] = (treeTargetBlockProgress[2] + ((treeTargetBlockProgress[1] - currentTree.size()) * treeTargetBlockProgress[0]));
            treeTargetBlockProgress[1] = (double) currentTree.size();
            currentlyBreakingPos = this.returnFurthestLog(currentTree);
            if (currentlyBreakingPos == null) this.isFinished = true;
            currentBlockProgress = 0;
        } else if (currentBlockProgress % 4 == 0) {
            world.playSoundAtEntity(entity, "dig.wood", 1, 1);
            world.playSound(currentlyBreakingPos.getX(), currentlyBreakingPos.getY(), currentlyBreakingPos.getZ(), "dig.wood", 2, .5F, true);
        }
        return itemStack;
    }

    private Set<BlockPos> getAllConnectingTreeBlocks(World world, BlockPos fullBlockPos, Set<Long> searchedBlocks, boolean useLeavesToCheck, boolean originalPass) throws StackOverflowError {
        Set<BlockPos> fullBlocks = new LinkedHashSet<>();
        if (originalPass) {
            fullBlocks.add(mainBlockPos);
            searchedBlocks.add(mainBlockPos.toLong());
        }
        int maxRange = 12;
        //LogHelper.info(iteration);
        if (fullBlockPos.getX() < mainBlockPos.getX() + maxRange && fullBlockPos.getX() > mainBlockPos.getX() - maxRange && fullBlockPos.getZ() < mainBlockPos.getZ() + maxRange && fullBlockPos.getZ() > mainBlockPos.getZ() - maxRange) {
            Set<BlockPos> blocksAroundCurrent = new LinkedHashSet<>(26);
            BlockTools.getSurroundingBlockPos(fullBlockPos, 1).stream().filter(blockPos -> {
                boolean searchedBlockContains = searchedBlocks.contains(blockPos.toLong());
                if (!searchedBlockContains) searchedBlocks.add(blockPos.toLong());
                Block block = world.getBlockState(blockPos).getBlock();
                int damageValue = block.getDamageValue(world, blockPos);
                return (!searchedBlockContains && ((block == mainBlock && damageValue == mainBlockDamageValue) || (useLeavesToCheck && treeLeaf != null && block == treeLeaf && damageValue == treeLeafDamageValue)));
            }).forEach(blockPos -> {
                blocksAroundCurrent.add(blockPos);
                fullBlocks.add(blockPos);
            });
            //blocksAroundCurrent.forEach(fullBlock1 -> fullBlocks.addAll(getAllConnectingTreeBlocks(world, fullBlock1, searchedBlocks, useLeavesToCheck, false, iteration++)));
            for (BlockPos blockPos : blocksAroundCurrent) {
                fullBlocks.addAll(getAllConnectingTreeBlocks(world, blockPos, searchedBlocks, useLeavesToCheck, false));
            }
        }
        if (originalPass) {
            if (!fullBlocks.isEmpty()) {
                Iterator<BlockPos> iterator = fullBlocks.iterator();
                Set<Long> validCoords = new HashSet<>();
                while (iterator.hasNext()) {
                    BlockPos next = iterator.next();
                    if (world.getBlockState(next).getBlock() instanceof BlockLeavesBase) iterator.remove();
                    else {
                        if (!validCoords.isEmpty() && !validCoords.contains(next.toLong())) {
                            iterator.remove();
                        } else {
                            BlockTools.getSurroundingBlockPos(next, 3).stream().filter(coord -> !validCoords.contains(coord.toLong())).forEach(blockPos -> validCoords.add(blockPos.toLong()));
                        }
                    }
                }
            }
        }
        return fullBlocks;
    }

    private BlockPos returnFurthestLog(Set<BlockPos> blockSet) {
        if (!blockSet.isEmpty()) {
            BlockPos result = null;
            int dist = -1;
            ArrayList<BlockPos> removeFullBlocks = new ArrayList<>();
            for (BlockPos blockPos : blockSet) {
                if (world.getBlockState(blockPos).getBlock() == mainBlock) {
                    int blockDist = BlockTools.compareTo(blockPos, mainBlockPos);
                    if (dist == -1 || blockDist > dist || (dist == blockDist && blockPos.getY() > result.getY())) {
                        dist = blockDist;
                        result = blockPos;
                    }
                } else removeFullBlocks.add(blockPos);
            }
            removeFullBlocks.stream().filter(currentTree::contains).forEach(currentTree::remove);
            removeFullBlocks.stream().filter(blockSet::contains).forEach(blockSet::remove);
            return result;
        }
        return null;
    }

    private BlockPos returnClosestLog(Set<BlockPos> blockSet) {
        if (!blockSet.isEmpty()) {
            BlockPos result = null;
            int dist = -1;
            ArrayList<BlockPos> removeFullBlocks = new ArrayList<>();
            for (BlockPos blockPos : blockSet) {
                if (entity.worldObj.getBlockState(blockPos).getBlock() == mainBlock) {
                    int blockDist = BlockTools.compareTo(blockPos, mainBlockPos);
                    if (dist == -1 || blockDist < dist) {
                        dist = blockDist;
                        result = blockPos;
                    }
                } else removeFullBlocks.add(blockPos);
            }
            removeFullBlocks.stream().filter(currentTree::contains).forEach(currentTree::remove);
            removeFullBlocks.stream().filter(blockSet::contains).forEach(blockSet::remove);
            return result;
        }
        return null;
    }

    public boolean setMainBlockPosToClosest() {
        BlockPos closest = returnClosestLog(currentTree);
        if (closest != null) {
            mainBlockPos = closest;
            return true;
        }
        return false;
    }

    public BlockPos getMainBlockPos() {
        return mainBlockPos;
    }

    public void setMainBlockPos(BlockPos mainBlockPos) {
        this.mainBlockPos = mainBlockPos;
    }

    public Block getMainBlock() {
        return mainBlock;
    }

    public boolean isBlockContainedInTree(BlockPos blockPos) {
        for (BlockPos blockPos1 : currentTree) {
            if (world.getBlockState(blockPos1).getBlock() == world.getBlockState(blockPos).getBlock() && blockPos1.toLong() == blockPos.toLong())
                return true;
        }
        return false;
    }

    public void resetBlockProgress() {
        if (currentlyBreakingPos != null && world.getBlockState(currentlyBreakingPos).getBlock() == world.getBlockState(currentlyBreakingPos).getBlock())
            world.sendBlockBreakProgress(entity.getEntityId(), currentlyBreakingPos, -1);
        if (mainBlockPos != null && world.getBlockState(mainBlockPos).getBlock() == mainBlock)
            world.sendBlockBreakProgress(entity.getEntityId(), mainBlockPos, -1);
    }

    public int getNumberOfLogs() {
        return this.currentTree.size();
    }

    public boolean isFinished() {
        return isFinished;
    }

    public Set<BlockPos> getCurrentTree() {
        return currentTree;
    }
}
