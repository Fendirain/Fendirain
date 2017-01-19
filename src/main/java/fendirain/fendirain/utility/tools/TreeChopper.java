package fendirain.fendirain.utility.tools;

import fendirain.fendirain.common.entity.mob.EntityFenderium.EntityFenderiumMob;
import fendirain.fendirain.common.item.ItemFenderiumAxe;
import fendirain.fendirain.network.PacketHandler;
import fendirain.fendirain.network.packets.BlockHitEffectPacket;
import fendirain.fendirain.reference.ConfigValues;
import fendirain.fendirain.utility.helper.BlockTools;
import fendirain.fendirain.utility.helper.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;

public class TreeChopper {
    private final World world;
    private final Entity entity;
    private final Block treeLeaf;
    private final int treeLeafMeta;
    private final Block mainBlock;
    private final int mainBlockMeta;
    private BlockPos mainBlockPos;
    private Set<BlockPos> currentTree = new LinkedHashSet<>();
    private boolean isFinished = false;
    private boolean finishedSearching = false;

    // These only apply's to non-player entities (Fenderium currently)
    private int currentBlockProgress;
    private double[] treeTargetBlockProgress;
    private BlockPos currentlyBreakingPos;

    // This only apply's to the player
    private ItemStack itemStack;

    public TreeChopper(Entity entity, BlockPos mainBlockPos, BlockPos treeLeaf, boolean useLeavesToCheck, ItemStack itemStack) {
        this.world = entity.getEntityWorld();
        this.entity = entity;
        this.mainBlockPos = mainBlockPos;
        this.mainBlock = world.getBlockState(mainBlockPos).getBlock();
        this.mainBlockMeta = BlockTools.getBlockMeta(world.getBlockState(mainBlockPos));
        this.treeLeaf = useLeavesToCheck ? world.getBlockState(treeLeaf).getBlock() : null;
        this.treeLeafMeta = useLeavesToCheck ? BlockTools.getBlockMeta(world.getBlockState(treeLeaf)) : -2;
        if (entity instanceof EntityPlayer) {
            this.currentTree.addAll(getAllConnectingTreeBlocks(world, useLeavesToCheck));
            this.itemStack = itemStack;
        } else if (entity instanceof EntityFenderiumMob)
            MinecraftForge.EVENT_BUS.register(new GetTreeBlocks(useLeavesToCheck, 5));
        else currentlyBreakingPos = null;

    }

    private void setFenderiumValues() {
        currentBlockProgress = 0;
        treeTargetBlockProgress = new double[]{(double) 10 / currentTree.size(), (double) currentTree.size(), 0.0d};
        currentlyBreakingPos = returnFurthestLog(currentTree);
    }

    public int breakAllBlocks(int maxToBreak) {
        int amountBroken;
        Set<BlockPos> blocksToBreak = new LinkedHashSet<>(currentTree.size());
        BlockPos closestBlock;

        if (maxToBreak < currentTree.size()) {
            Set<BlockPos> blocksToIterate = new LinkedHashSet<>(currentTree.size());
            blocksToIterate.addAll(currentTree);
            for (int i = 0; i < ((maxToBreak < currentTree.size()) ? maxToBreak : currentTree.size()); i++) {
                BlockPos blockToBreak = returnFurthestLog(blocksToIterate);
                blocksToBreak.add(blockToBreak);
                blocksToIterate.remove(blockToBreak);
            }
        } else blocksToBreak.addAll(currentTree);
        closestBlock = returnClosestLog(blocksToBreak);
        if (closestBlock == null) closestBlock = mainBlockPos;

        amountBroken = blocksToBreak.size();
        LogHelper.info(amountBroken);
        if (amountBroken > 0) entity.playSound(SoundEvents.BLOCK_WOOD_HIT, 6, .2F);
        MinecraftForge.EVENT_BUS.register(new BreakBlocksQueue(blocksToBreak, closestBlock, (EntityPlayer) entity));
        currentTree.removeAll(blocksToBreak);
        if (currentTree.isEmpty()) this.isFinished = true;
        return amountBroken;
    }

    public void breakFurthestBlock() {
        BlockPos logToBreak = returnFurthestLog(currentTree);
        assert logToBreak != null;
        EntityItem entityItem = new EntityItem(world, logToBreak.getX(), logToBreak.getY(), logToBreak.getZ());
        ItemStack itemStack = mainBlock.getPickBlock(world.getBlockState(mainBlockPos), null, world, mainBlockPos, null);
        itemStack.setCount(1);
        entityItem.setEntityItemStack(itemStack);
        entityItem.setDefaultPickupDelay();
        world.spawnEntity(entityItem);
        world.playEvent(2001, logToBreak, Block.getIdFromBlock(mainBlock) + (mainBlockMeta << 12));
        BlockLog log = (BlockLog) mainBlock;
        log.breakBlock(world, logToBreak, world.getBlockState(logToBreak));
        world.setBlockToAir(logToBreak);
        world.playSound(logToBreak.getX(), logToBreak.getY(), logToBreak.getZ(), SoundEvents.BLOCK_WOOD_HIT, SoundCategory.BLOCKS, 2, 5F, true);
        itemStack.damageItem(1, (EntityPlayer) entity);
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
        PacketHandler.sendToAllAround(new BlockHitEffectPacket(mainBlockPos.toLong(), entity.getHorizontalFacing().getOpposite()), entity, 32);
        if (this.currentBlockProgress >= 240) {
            itemStack = mainBlock.getPickBlock(world.getBlockState(mainBlockPos), null, world, mainBlockPos, null);
            itemStack.setCount(1);
            world.sendBlockBreakProgress(entity.getEntityId(), currentlyBreakingPos, -1);
            world.playEvent(2001, currentlyBreakingPos, Block.getIdFromBlock(mainBlock) + (mainBlockMeta << 12));
            BlockLog log = (BlockLog) mainBlock;
            log.breakBlock(world, currentlyBreakingPos, world.getBlockState(currentlyBreakingPos));
            world.setBlockToAir(currentlyBreakingPos);
            world.playSound(currentlyBreakingPos.getX(), currentlyBreakingPos.getY(), currentlyBreakingPos.getZ(), SoundEvents.BLOCK_WOOD_HIT, SoundCategory.BLOCKS, 2, 5F, true);
            entity.playSound(SoundEvents.BLOCK_WOOD_HIT, 2, 0.5F);
            if (currentTree.contains(currentlyBreakingPos)) currentTree.remove(currentlyBreakingPos);
            treeTargetBlockProgress[2] = (treeTargetBlockProgress[2] + ((treeTargetBlockProgress[1] - currentTree.size()) * treeTargetBlockProgress[0]));
            treeTargetBlockProgress[1] = (double) currentTree.size();
            currentlyBreakingPos = this.returnFurthestLog(currentTree);
            if (currentlyBreakingPos == null) this.isFinished = true;
            currentBlockProgress = 0;
        } else if (currentBlockProgress % 4 == 0) {
            entity.playSound(SoundEvents.BLOCK_WOOD_HIT, 1, 1);
            world.playSound(currentlyBreakingPos.getX(), currentlyBreakingPos.getY(), currentlyBreakingPos.getZ(), SoundEvents.BLOCK_WOOD_HIT, SoundCategory.BLOCKS, 2, 5F, true);
        }
        return itemStack;
    }

    private Set<BlockPos> getAllConnectingTreeBlocks(World world, boolean useLeavesToCheck) {
        Set<BlockPos> fullBlocks = new LinkedHashSet<>();
        Set<Long> searchedBlocks = new HashSet<>();
        searchedBlocks.add(mainBlockPos.toLong());
        int maxRange = 14;
        Set<BlockPos> blocksToSearch = new LinkedHashSet<>();
        blocksToSearch.add(mainBlockPos);
        while (!blocksToSearch.isEmpty()) {
            Set<BlockPos> currentTask = new LinkedHashSet<>(blocksToSearch);
            blocksToSearch.clear();
            currentTask.forEach(blockPos -> {
                fullBlocks.add(blockPos);
                BlockTools.getSurroundingBlockPos(blockPos, 1).stream().filter(blockPosChecking -> {
                    if (searchedBlocks.contains(blockPosChecking.toLong())) return false;
                    searchedBlocks.add(blockPosChecking.toLong());
                    if (!(blockPosChecking.getX() < mainBlockPos.getX() + maxRange && blockPosChecking.getX() > mainBlockPos.getX() - maxRange && blockPos.getZ() < mainBlockPos.getZ() + maxRange && blockPos.getZ() > mainBlockPos.getZ() - maxRange))
                        return false;
                    Block block = world.getBlockState(blockPosChecking).getBlock();
                    int blockMeta = BlockTools.getBlockMeta(world.getBlockState(blockPosChecking));
                    return (block == mainBlock && blockMeta == mainBlockMeta) || (useLeavesToCheck && treeLeaf != null && block == treeLeaf && blockMeta == treeLeafMeta);
                }).forEach(blocksToSearch::add);
            });
        }
        if (!fullBlocks.isEmpty()) {
            Iterator<BlockPos> iterator = fullBlocks.iterator();
            Set<Long> validCoords = new HashSet<>();
            while (iterator.hasNext()) {
                BlockPos next = iterator.next();
                if (world.getBlockState(next).getBlock() instanceof BlockLeaves) iterator.remove();
                else {
                    if (!validCoords.isEmpty() && !validCoords.contains(next.toLong())) iterator.remove();
                    else
                        BlockTools.getSurroundingBlockPos(next, 3).stream().filter(coord -> !validCoords.contains(coord.toLong())).forEach(blockPos -> validCoords.add(blockPos.toLong()));
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
                if (entity.world.getBlockState(blockPos).getBlock() == mainBlock) {
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

    public void updateCurrentTreeBlocks() {
        Iterator<BlockPos> blockPosIterator = currentTree.iterator();
        while (blockPosIterator.hasNext())
            if (world.getBlockState(blockPosIterator.next()).getBlock() != mainBlock) blockPosIterator.remove();
        if (currentTree.isEmpty()) this.isFinished = true;
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

    public BlockPos getCurrentlyBreakingPos() {
        return currentlyBreakingPos;
    }

    public int getWholeTreeProgress() {
        return currentlyBreakingPos != null ? (int) treeTargetBlockProgress[2] : -1;
    }

    public boolean isFinishedSearching() {
        return finishedSearching;
    }

    // Lets slow down the breaking a bit, No need to have it all try to happen in 1 tick.
    private class BreakBlocksQueue {
        private BlockPos closestPos;
        private Set<BlockPos> blockPosSet = null;
        private Iterator<BlockPos> blockPosIterator;
        private EntityPlayer entityPlayer;
        private ItemStack itemStack;
        private int i = 1;
        private boolean spawnBreakParticles = true;
        private int logsToDrop = 0;

        BreakBlocksQueue(Set<BlockPos> blockPosSet, BlockPos closestPos, EntityPlayer entityPlayer) {
            if (!blockPosSet.isEmpty()) {
                this.blockPosSet = blockPosSet;
                this.closestPos = closestPos;
                this.blockPosIterator = blockPosSet.iterator();
                this.entityPlayer = entityPlayer;
                this.itemStack = mainBlock.getPickBlock(world.getBlockState(mainBlockPos), null, world, mainBlockPos, null);
            } else this.finish();
        }

        @SubscribeEvent
        public void breakBlock(TickEvent.WorldTickEvent worldTickEvent) {
            if (i == 1) {
                if (blockPosSet == null || blockPosSet.isEmpty()) {
                    this.finish();
                    return;
                }

                if (worldTickEvent.side.isServer()) {
                    BlockPos blockPos = blockPosIterator.next();
                    if (world.getBlockState(blockPos).getBlock() == mainBlock) {
                        BlockLog log = (BlockLog) world.getBlockState(blockPos).getBlock();
                        if (entity instanceof EntityPlayerMP)
                            ((EntityPlayerMP) entity).connection.sendPacket(new SPacketBlockChange(world, blockPos));
                        if (itemStack.getItem() instanceof ItemFenderiumAxe)
                            itemStack.damageItem(((ItemFenderiumAxe) itemStack.getItem()).getDamageAmplifier(), entityPlayer);
                        log.breakBlock(world, blockPos, world.getBlockState(blockPos));
                        spawnBreakParticles = !spawnBreakParticles;
                        if (spawnBreakParticles)
                            world.playEvent(2001, blockPos, Block.getIdFromBlock(mainBlock) + (mainBlockMeta << 12));
                        world.setBlockToAir(blockPos);
                        world.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.BLOCKS, 1, .5F);
                        //world.playSoundEffect(blockPos.getX(), blockPos.getY(), blockPos.getZ(), "dig.wood", 1, .5F);
                        // world.playSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.BLOCK_WOOD_HIT, SoundCategory.BLOCKS, 2, 5F, true);
                        if (ConfigValues.fenderiumAxe_dropItemPerLog) {
                            EntityItem entityItem = new EntityItem(world);
                            entityItem.setPosition(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                            itemStack.setCount(1);
                            entityItem.setEntityItemStack(itemStack);
                            entityItem.setDefaultPickupDelay();
                            world.spawnEntity(entityItem);
                        } else logsToDrop++;
                        i = ConfigValues.fenderiumAxe_blockBreakSpeed;
                    }
                    blockPosIterator.remove();
                    if (blockPosSet.isEmpty()) finish();
                }
            } else i++;
        }

        private void finish() {
            if (!ConfigValues.fenderiumAxe_dropItemPerLog) {
                EntityItem entityItem = new EntityItem(world);
                entityItem.setPosition(closestPos.getX(), closestPos.getY(), closestPos.getZ());
                itemStack.setCount(logsToDrop);
                entityItem.setEntityItemStack(itemStack);
                entityItem.setPickupDelay(20);
                world.spawnEntity(entityItem);
            }
            MinecraftForge.EVENT_BUS.unregister(this);
        }
    }

    private class GetTreeBlocks {
        private final int iterationsPerTick;
        private final boolean useLeavesToCheck;
        private final Set<BlockPos> fullBlocks;
        private final Set<Long> searchedBlocks;
        private final Set<BlockPos> blocksToSearch;

        public GetTreeBlocks(boolean useLeavesToCheck, int iterationsPerTick) {
            this.iterationsPerTick = iterationsPerTick;
            this.useLeavesToCheck = useLeavesToCheck;
            this.fullBlocks = new LinkedHashSet<>();
            this.searchedBlocks = new HashSet<>();
            this.blocksToSearch = new LinkedHashSet<>();
            searchedBlocks.add(mainBlockPos.toLong());
            blocksToSearch.add(mainBlockPos);
        }

        @SubscribeEvent
        public void findAllBlocks(TickEvent.WorldTickEvent worldTickEvent) {
            int currentIteration = 0;
            int maxRange = 14;
            while (currentIteration <= (blocksToSearch.size() < iterationsPerTick ? blocksToSearch.size() : iterationsPerTick)) {
                BlockPos blockPos = blocksToSearch.stream().findFirst().get();
                blocksToSearch.remove(blockPos);
                fullBlocks.add(blockPos);
                BlockTools.getSurroundingBlockPos(blockPos, 1).stream().filter(blockPosChecking -> {
                    if (searchedBlocks.contains(blockPosChecking.toLong())) return false;
                    searchedBlocks.add(blockPosChecking.toLong());
                    if (!(blockPosChecking.getX() < mainBlockPos.getX() + maxRange && blockPosChecking.getX() > mainBlockPos.getX() - maxRange && blockPos.getZ() < mainBlockPos.getZ() + maxRange && blockPos.getZ() > mainBlockPos.getZ() - maxRange))
                        return false;
                    Block block = world.getBlockState(blockPosChecking).getBlock();
                    int metaValue = BlockTools.getBlockMeta(world.getBlockState(blockPosChecking));
                    return (block == mainBlock && metaValue == mainBlockMeta) || (useLeavesToCheck && treeLeaf != null && block == treeLeaf && metaValue == treeLeafMeta);
                }).forEach(blocksToSearch::add);
                currentIteration++;
                if (blocksToSearch.isEmpty()) finish();
            }
        }

        private void finish() {
            if (!fullBlocks.isEmpty()) {
                Iterator<BlockPos> iterator = fullBlocks.iterator();
                Set<Long> validCoords = new HashSet<>();
                while (iterator.hasNext()) {
                    BlockPos next = iterator.next();
                    if (world.getBlockState(next).getBlock() instanceof BlockLeaves) iterator.remove();
                    else {
                        if (!validCoords.isEmpty() && !validCoords.contains(next.toLong())) iterator.remove();
                        else
                            BlockTools.getSurroundingBlockPos(next, 3).stream().filter(coord -> !validCoords.contains(coord.toLong())).forEach(blockPos -> validCoords.add(blockPos.toLong()));
                    }
                }
            } else isFinished = true;

            currentTree = fullBlocks;
            finishedSearching = true;
            setFenderiumValues();

            MinecraftForge.EVENT_BUS.unregister(this);
        }
    }
}
