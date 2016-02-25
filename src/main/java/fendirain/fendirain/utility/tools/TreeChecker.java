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
        Set<FullBlock> blocksAroundCurrent = new LinkedHashSet<>(26);
        fullBlock.getSurroundingBlockPos(1).stream().filter(blockPos1 -> !(world.getBlockState(blockPos1) instanceof BlockAir)).forEach(blockPos -> {
            FullBlock fullBlock1 = isBlockValid(searchedBlocks, world, blockPos);
            if (fullBlock1 != null) {
                blocksAroundCurrent.add(fullBlock1);
                baseTree.add(fullBlock1);
            }
        });
        blocksAroundCurrent.forEach(fullBlock1 -> {
            if (fullBlock1.getBlock() instanceof BlockLog)
                baseTree.addAll(getBaseTree(world, fullBlock1, searchedBlocks, false));
        });
        return baseTree;
    }

    private static FullBlock isBlockValid(Set<Long> searchedBlocks, World world, BlockPos blockPos) {
        if (!searchedBlocks.contains(blockPos.toLong())) {
            FullBlock fullBlock = new FullBlock(world.getBlockState(blockPos).getBlock(), blockPos, world.getBlockState(blockPos).getBlock().getDamageValue(world, blockPos));
            if (fullBlock.getBlock() instanceof BlockLog || fullBlock.getBlock() instanceof BlockLeavesBase) {
                searchedBlocks.add(fullBlock.getBlockPos().toLong());
                return fullBlock;
            }
        }
        return null;
    }
}
