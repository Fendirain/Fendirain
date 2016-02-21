package fendirain.fendirain.utility.tools;

import fendirain.fendirain.utility.helper.FullBlock;
import net.minecraft.block.*;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class TreeChecker {

    public static FullBlock isTree(World world, BlockPos blockPos) {
        Block block = world.getBlockState(blockPos.down()).getBlock();
        if (block != Blocks.air && (block instanceof BlockLog || block instanceof BlockGrass || block instanceof BlockDirt)) {
            FullBlock fullBlock = new FullBlock(world.getBlockState(blockPos).getBlock(), blockPos, world.getBlockState(blockPos).getBlock().getDamageValue(world, blockPos));
            Set<FullBlock> connectedBlocks = getBaseTree(world, fullBlock, new HashSet<>(), true);
            Map<Integer, Integer> leaves = new HashMap<>();
            final int[] logs = {0};
            connectedBlocks.forEach(fullBlock1 -> {
                if (fullBlock1.getBlock() instanceof BlockLog) logs[0]++;
                else if (fullBlock1.getBlock() instanceof BlockLeavesBase) {
                    if (!leaves.containsKey(fullBlock1.getDamageValue())) leaves.put(fullBlock1.getDamageValue(), 0);
                    leaves.replace(fullBlock1.getDamageValue(), leaves.get(fullBlock1.getDamageValue()) + 1);
                }
            });

            FullBlock leafBlock = null;
            final int[] mostLeaves = {-1};
            if (leaves.isEmpty()) return null;
            else {
                final int[] leafType = new int[1];
                leaves.forEach((leafDamageValue, amountOfLeaves) -> {
                    if (mostLeaves[0] == -1 || amountOfLeaves > mostLeaves[0]) {
                        leafType[0] = leafDamageValue;
                        mostLeaves[0] = amountOfLeaves;
                    }
                });
                for (FullBlock fullBlock1 : connectedBlocks) {
                    if (fullBlock1.getDamageValue() == leafType[0]) {
                        leafBlock = fullBlock1;
                        break;
                    }
                }
                if (leafBlock == null) return null;
            }
            if (mostLeaves[0] > 4) return leafBlock;
        }
        return null;
    }

    private static Set<FullBlock> getBaseTree(World world, FullBlock fullBlock, Set<Long> searchedBlocks, boolean originalPass) {
        Set<FullBlock> baseTree = new LinkedHashSet<>();
        if (originalPass) baseTree.add(fullBlock);
        baseTree.addAll(checkLog(searchedBlocks, world, fullBlock.getBlockPos().up()));
        baseTree.addAll(checkLog(searchedBlocks, world, fullBlock.getBlockPos().north()));
        baseTree.addAll(checkLog(searchedBlocks, world, fullBlock.getBlockPos().east()));
        baseTree.addAll(checkLog(searchedBlocks, world, fullBlock.getBlockPos().south()));
        baseTree.addAll(checkLog(searchedBlocks, world, fullBlock.getBlockPos().west()));
        baseTree.addAll(checkLog(searchedBlocks, world, fullBlock.getBlockPos().down()));
        baseTree.addAll(checkLog(searchedBlocks, world, fullBlock.getBlockPos().north().east()));
        baseTree.addAll(checkLog(searchedBlocks, world, fullBlock.getBlockPos().north().west()));
        baseTree.addAll(checkLog(searchedBlocks, world, fullBlock.getBlockPos().north().up()));
        baseTree.addAll(checkLog(searchedBlocks, world, fullBlock.getBlockPos().north().up().east()));
        baseTree.addAll(checkLog(searchedBlocks, world, fullBlock.getBlockPos().north().up().west()));
        baseTree.addAll(checkLog(searchedBlocks, world, fullBlock.getBlockPos().north().down()));
        baseTree.addAll(checkLog(searchedBlocks, world, fullBlock.getBlockPos().north().down().east()));
        baseTree.addAll(checkLog(searchedBlocks, world, fullBlock.getBlockPos().north().down().west()));
        baseTree.addAll(checkLog(searchedBlocks, world, fullBlock.getBlockPos().up().east()));
        baseTree.addAll(checkLog(searchedBlocks, world, fullBlock.getBlockPos().up().west()));
        baseTree.addAll(checkLog(searchedBlocks, world, fullBlock.getBlockPos().down().east()));
        baseTree.addAll(checkLog(searchedBlocks, world, fullBlock.getBlockPos().down().west()));
        baseTree.addAll(checkLog(searchedBlocks, world, fullBlock.getBlockPos().south().east()));
        baseTree.addAll(checkLog(searchedBlocks, world, fullBlock.getBlockPos().south().west()));
        baseTree.addAll(checkLog(searchedBlocks, world, fullBlock.getBlockPos().south().up()));
        baseTree.addAll(checkLog(searchedBlocks, world, fullBlock.getBlockPos().south().up().east()));
        baseTree.addAll(checkLog(searchedBlocks, world, fullBlock.getBlockPos().south().up().west()));
        baseTree.addAll(checkLog(searchedBlocks, world, fullBlock.getBlockPos().south().down()));
        baseTree.addAll(checkLog(searchedBlocks, world, fullBlock.getBlockPos().south().down().east()));
        baseTree.addAll(checkLog(searchedBlocks, world, fullBlock.getBlockPos().south().down().west()));
        return baseTree;
    }

    private static Set<FullBlock> checkLog(Set<Long> searchedBlocks, World world, BlockPos blockPos) {
        Set<FullBlock> foundBlocks = new LinkedHashSet<>();
        FullBlock fullBlock = new FullBlock(world.getBlockState(blockPos).getBlock(), blockPos, world.getBlockState(blockPos).getBlock().getDamageValue(world, blockPos));
        if (fullBlock.getBlock() instanceof BlockLog || fullBlock.getBlock() instanceof BlockLeavesBase) {
            if (!searchedBlocks.contains(blockPos.toLong())) {
                searchedBlocks.add(blockPos.toLong());
                foundBlocks.add(fullBlock);
                if (fullBlock.getBlock() instanceof BlockLog)
                    foundBlocks.addAll(getBaseTree(world, fullBlock, searchedBlocks, false));
            }
        }
        return foundBlocks;
    }
}
