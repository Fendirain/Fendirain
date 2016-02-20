package fendirain.fendirain.utility.tools;

import fendirain.fendirain.utility.helper.FullBlock;
import fendirain.fendirain.utility.helper.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.BlockLog;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class TreeChopper {
    private final World world;
    private final EntityPlayer entityPlayer;
    private final FullBlock treeLeaf;
    private final Block blockType;
    private FullBlock mainBlock;
    private Set<FullBlock> currentTree = new LinkedHashSet<>();
    private boolean isFinished = false;


    public TreeChopper(EntityPlayer entityPlayer, FullBlock mainBlock, FullBlock treeLeaf) {
        this.world = entityPlayer.getEntityWorld();
        this.entityPlayer = entityPlayer;
        this.mainBlock = mainBlock;
        this.treeLeaf = treeLeaf;
        this.blockType = mainBlock.getBlock();

        this.currentTree.addAll(getAllConnectingTreeBlocks(world, mainBlock, new LinkedHashSet<>(), true));
    }

    public int breakAllBlocks(int maxToBreak) {
        int amountBroken = 0;
        LogHelper.info(currentTree.size());
        for (FullBlock fullBlock : currentTree) {
            if (amountBroken >= maxToBreak) break;
            if (world.getBlockState(fullBlock.getBlockPos()).getBlock() == fullBlock.getBlock()) {
                /*try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    LogHelper.error(e.getStackTrace());
                }*/
                Minecraft.getMinecraft().effectRenderer.addBlockDestroyEffects(fullBlock.getBlockPos(), fullBlock.getBlock().getDefaultState());
                BlockLog log = (BlockLog) fullBlock.getBlock();
                log.breakBlock(world, fullBlock.getBlockPos(), world.getBlockState(fullBlock.getBlockPos()));
                world.setBlockToAir(fullBlock.getBlockPos());
                world.playSoundAtEntity(entityPlayer, "dig.wood", 2, .5F);
                amountBroken++;
            }
        }
        EntityItem entityItem = new EntityItem(world, mainBlock.getBlockPos().getX(), mainBlock.getBlockPos().getY(), mainBlock.getBlockPos().getZ());
        entityItem.setEntityItemStack(new ItemStack(mainBlock.getBlock(), amountBroken));
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
        world.playSoundAtEntity(entityPlayer, "dig.wood", 2, .5F);
        if (currentTree != null && currentTree.contains(logToBreak)) currentTree.remove(logToBreak);
        assert currentTree != null;
        if (currentTree.isEmpty()) isFinished = true;
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
                for (int z = blockPos.getZ() - maxLogRange; z <= blockPos.getZ() + maxLogRange; z++) {
                    result.add(new BlockPos(x, y, z).toString());
                }
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
                if (entityPlayer.getEntityWorld().getBlockState(fullBlock.getBlockPos()).getBlock() == blockType) {
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

    public boolean isFinished() {
        return isFinished;
    }
}
