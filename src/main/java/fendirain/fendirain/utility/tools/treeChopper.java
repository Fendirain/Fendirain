package fendirain.fendirain.utility.tools;

import fendirain.fendirain.utility.helper.FullBlock;
import fendirain.fendirain.utility.helper.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.BlockLog;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class TreeChopper {
    private final World world;
    private final Entity entity;
    private final FullBlock treeLeaf;
    private final Block blockType;
    private FullBlock mainBlock;
    private Set<FullBlock> currentTree = new LinkedHashSet<>();
    private boolean isFinished = false;

    private int currentBlockProgress;
    private double[] treeTargetBlockProgress;
    private FullBlock currentlyBreaking;


    public TreeChopper(Entity entity, FullBlock mainBlock, FullBlock treeLeaf) {
        this.world = entity.getEntityWorld();
        this.entity = entity;
        this.mainBlock = mainBlock;
        this.treeLeaf = treeLeaf;
        this.blockType = mainBlock.getBlock();

        this.currentTree.addAll(getAllConnectingTreeBlocks(world, mainBlock, new LinkedHashSet<>(), true));

        if (!(entity instanceof EntityPlayer)) {
            currentBlockProgress = 0;
            treeTargetBlockProgress = new double[]{(double) 10 / currentTree.size(), Double.valueOf(currentTree.size()), 0.0d};
            currentlyBreaking = returnFurthestLog();
        }
    }

    public int breakAllBlocks(int maxToBreak) {
        int amountBroken = 0;
        LogHelper.info(currentTree.size());
        for (FullBlock fullBlock : currentTree) {
            if (amountBroken >= maxToBreak) break;
            if (world.getBlockState(fullBlock.getBlockPos()).getBlock() == fullBlock.getBlock()) {
                Minecraft.getMinecraft().effectRenderer.addBlockDestroyEffects(fullBlock.getBlockPos(), fullBlock.getBlock().getDefaultState());
                BlockLog log = (BlockLog) fullBlock.getBlock();
                log.breakBlock(world, fullBlock.getBlockPos(), world.getBlockState(fullBlock.getBlockPos()));
                world.setBlockToAir(fullBlock.getBlockPos());
                world.playSoundAtEntity(entity, "dig.wood", 2, .5F);
                amountBroken++;
            }
        }
        EntityItem entityItem = new EntityItem(world, mainBlock.getBlockPos().getX(), mainBlock.getBlockPos().getY(), mainBlock.getBlockPos().getZ());
        entityItem.setEntityItemStack(new ItemStack(mainBlock.getBlock(), amountBroken, mainBlock.getDamageValue()));
        world.spawnEntityInWorld(entityItem);
        return amountBroken;
    }

    public void breakFurthestBlock() {
        FullBlock logToBreak = returnFurthestLog();
        LogHelper.info(logToBreak.getBlockPos().toString());
        EntityItem entityItem = new EntityItem(world, logToBreak.getBlockPos().getX(), logToBreak.getBlockPos().getY(), logToBreak.getBlockPos().getZ());
        entityItem.setEntityItemStack(new ItemStack(logToBreak.getBlock(), 1, logToBreak.getDamageValue()));
        world.spawnEntityInWorld(entityItem);
        Minecraft.getMinecraft().effectRenderer.addBlockDestroyEffects(logToBreak.getBlockPos(), logToBreak.getBlock().getDefaultState());
        BlockLog log = (BlockLog) logToBreak.getBlock();
        log.breakBlock(world, logToBreak.getBlockPos(), world.getBlockState(logToBreak.getBlockPos()));
        world.setBlockToAir(logToBreak.getBlockPos());
        world.playSoundAtEntity(entity, "dig.wood", 2, .5F);
        if (currentTree != null && currentTree.contains(logToBreak)) currentTree.remove(logToBreak);
        assert currentTree != null;
        if (currentTree.isEmpty()) isFinished = true;
    }

    public ItemStack continueBreaking(int breakSpeed) {
        if (world.getBlockState(currentlyBreaking.getBlockPos()).getBlock() != currentlyBreaking.getBlock()) {
            world.sendBlockBreakProgress(entity.getEntityId(), currentlyBreaking.getBlockPos(), -1);
            if (currentTree != null && currentTree.contains(currentlyBreaking))
                currentTree.remove(currentlyBreaking);
            currentlyBreaking = this.returnFurthestLog();
            if (currentlyBreaking == null && currentTree.isEmpty()) {
                this.isFinished = true;
                return null;
            }
            currentBlockProgress = 0;
        }
        ItemStack itemStack = null;
        currentBlockProgress = currentBlockProgress + 1 + Math.abs(breakSpeed);
        int breakProgress = (int) ((float) this.currentBlockProgress / 240.0F * 10.0F);
        //if (treeTargetFullBlock != currentlyBreaking)
        world.sendBlockBreakProgress(entity.getEntityId(), currentlyBreaking.getBlockPos(), breakProgress);
        //world.sendBlockBreakProgress(entity.getEntityId(), treeTargetFullBlock.getBlockPos(), (int) treeTargetBlockProgress[2]);
        //entity.getLookHelper().setLookPosition(treeTargetFullBlock.getBlockPos().getX(), treeTargetFullBlock.getBlockPos().getY(), treeTargetFullBlock.getBlockPos().getZ(), 0, 0);
        Minecraft.getMinecraft().effectRenderer.addBlockHitEffects(mainBlock.getBlockPos(), entity.getHorizontalFacing().getOpposite());
        if (this.currentBlockProgress >= 240) {
            itemStack = new ItemStack(currentlyBreaking.getBlock(), 1, currentlyBreaking.getDamageValue());
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
            currentlyBreaking = this.returnFurthestLog();
            if (currentlyBreaking == null && currentTree.isEmpty()) this.isFinished = true;
            currentBlockProgress = 0;
        } else if (currentBlockProgress % 4 == 0) world.playSoundAtEntity(entity, "dig.wood", 1, 1);
        if (itemStack != null) return itemStack;
        return null;
    }

    private Set<FullBlock> getAllConnectingTreeBlocks(World world, FullBlock fullBlock, Set<Long> searchedBlocks, boolean originalPass) {
        Set<FullBlock> fullBlocks = new LinkedHashSet<>();
        if (originalPass) fullBlocks.add(mainBlock);
        int maxRange = 12;
        if (fullBlock.getBlockPos().getX() < mainBlock.getBlockPos().getX() + maxRange && fullBlock.getBlockPos().getX() > mainBlock.getBlockPos().getX() - maxRange && fullBlock.getBlockPos().getZ() < mainBlock.getBlockPos().getZ() + maxRange && fullBlock.getBlockPos().getZ() > mainBlock.getBlockPos().getZ() - maxRange) {
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
                        if (!validCoords.isEmpty() && !validCoords.contains(next.getBlockPos().toString()))
                            iterator.remove();
                        else
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
        if ((mainBlock.isSameType(fullBlock) && fullBlock.getDamageValue() == mainBlock.getDamageValue()) || (treeLeaf.isSameType(fullBlock) && fullBlock.getDamageValue() == treeLeaf.getDamageValue())) {
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
        int maxLogRange = 3;
        for (int y = blockPos.getY() - maxLogRange; y <= blockPos.getY() + maxLogRange; y++) {
            for (int x = blockPos.getX() - maxLogRange; x <= blockPos.getX() + maxLogRange; x++) {
                for (int z = blockPos.getZ() - maxLogRange; z <= blockPos.getZ() + maxLogRange; z++)
                    result.add(new BlockPos(x, y, z).toString());
            }
        }
        return result;
    }

    private FullBlock returnFurthestLog() {
        FullBlock result = mainBlock;
        int dist = 0;
        ArrayList<FullBlock> removeFullBlocks = new ArrayList<>();
        for (FullBlock fullBlock : currentTree) {
            if (fullBlock != mainBlock) {
                if (world.getBlockState(fullBlock.getBlockPos()).getBlock() == blockType) {
                    int blockDist = fullBlock.compareTo(mainBlock);
                    if (blockDist > dist || (dist == blockDist && fullBlock.getBlockPos().getY() > result.getBlockPos().getY())) {
                        dist = blockDist;
                        result = fullBlock;
                    }
                } else removeFullBlocks.add(fullBlock);
            }
        }
        removeFullBlocks.stream().filter(currentTree::contains).forEach(currentTree::remove);
        return result;
    }

    private FullBlock returnClosestLog() {
        if (!currentTree.isEmpty()) {
            FullBlock result = null;
            int dist = -1;
            ArrayList<FullBlock> removeFullBlocks = new ArrayList<>();
            for (FullBlock fullBlock : currentTree) {
                if (entity.worldObj.getBlockState(fullBlock.getBlockPos()).getBlock() == mainBlock.getBlock()) {
                    int blockDist = fullBlock.compareTo(mainBlock);
                    if (dist == -1) {
                        dist = blockDist;
                        result = fullBlock;
                    } else if (blockDist < dist) {
                        dist = blockDist;
                        result = fullBlock;
                    }
                } else removeFullBlocks.add(fullBlock);
            }
            removeFullBlocks.stream().filter(currentTree::contains).forEach(currentTree::remove);
            return result;
        }
        return null;
    }

    public boolean setMainBlockToClosest() {
        FullBlock closest = returnClosestLog();
        if (closest != null) {
            mainBlock = closest;
            return true;
        }
        return false;
    }

    public FullBlock getMainBlock() {
        return mainBlock;
    }

    public void setMainBlock(FullBlock mainBlock) {
        this.mainBlock = mainBlock;
    }

    public boolean isBlockContainedInTree(FullBlock fullBlockIn) {
        for (FullBlock fullBlock : currentTree) {
            if (fullBlock.getBlock() == fullBlockIn.getBlock() && fullBlock.getBlockPos().toLong() == fullBlockIn.getBlockPos().toLong())
                return true;
        }
        return false;
    }

    public void resetBlockProgress() {
        if (currentlyBreaking != null && world.getBlockState(currentlyBreaking.getBlockPos()).getBlock() == currentlyBreaking.getBlock())
            world.sendBlockBreakProgress(entity.getEntityId(), currentlyBreaking.getBlockPos(), -1);
        if (mainBlock != null && world.getBlockState(mainBlock.getBlockPos()).getBlock() == mainBlock.getBlock())
            world.sendBlockBreakProgress(entity.getEntityId(), mainBlock.getBlockPos(), -1);
    }

    public boolean isFinished() {
        return isFinished;
    }
}
