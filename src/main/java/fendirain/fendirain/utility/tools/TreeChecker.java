package fendirain.fendirain.utility.tools;

import fendirain.fendirain.utility.helper.BlockTools;
import net.minecraft.block.*;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class TreeChecker {
    public static BlockPos isTree(World world, BlockPos blockPos) {
        Block block = world.getBlockState(blockPos.down()).getBlock();
        if (block != Blocks.air && (block.isWood(world, blockPos.down()) || block instanceof BlockGrass || block instanceof BlockDirt)) {
            Set<BlockPos> connectedBlocks = getBaseTree(world, blockPos, new HashSet<>(), true);
            Map<Integer, Integer> leaves = new HashMap<>();
            final int[] logs = {0};
            connectedBlocks.forEach(fullBlock1 -> {
                Block block1 = world.getBlockState(fullBlock1).getBlock();
                int damageValue = block1.getDamageValue(world, fullBlock1);
                if (block1.isWood(world, fullBlock1)) logs[0]++;
                else if (block1 instanceof BlockLeavesBase) {
                    if (!leaves.containsKey(damageValue)) leaves.put(damageValue, 0);
                    leaves.replace(damageValue, leaves.get(damageValue) + 1);
                }
            });

            BlockPos leafBlock = null;
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
                for (BlockPos blockPos1 : connectedBlocks) {
                    if (world.getBlockState(blockPos1).getBlock().getDamageValue(world, blockPos1) == leafType[0] && world.getBlockState(blockPos1).getBlock() instanceof BlockLeavesBase) {
                        leafBlock = blockPos1;
                        break;
                    }
                }
                if (leafBlock == null) return null;
            }
            if (mostLeaves[0] > 4) return leafBlock;
        }
        return null;
    }

    private static Set<BlockPos> getBaseTree(World world, BlockPos blockPos, Set<Long> searchedBlocks, boolean originalPass) {
        Set<BlockPos> baseTree = new LinkedHashSet<>();
        if (originalPass) baseTree.add(blockPos);
        Set<BlockPos> blocksAroundCurrent = new LinkedHashSet<>(26);
        BlockTools.getSurroundingBlockPos(blockPos, 1).stream().filter(blockPos1 -> !(world.getBlockState(blockPos1) instanceof BlockAir)).forEach(blockPos1 -> {
            if (!searchedBlocks.contains(blockPos1.toLong())) {
                Block block = world.getBlockState(blockPos1).getBlock();
                if (block.isWood(world, blockPos1) || block instanceof BlockLeavesBase) {
                    searchedBlocks.add(blockPos1.toLong());
                    blocksAroundCurrent.add(blockPos1);
                    baseTree.add(blockPos1);
                }
            }
        });
        blocksAroundCurrent.forEach(fullBlock1 -> {
            if (world.getBlockState(fullBlock1).getBlock().isWood(world, fullBlock1))
                baseTree.addAll(getBaseTree(world, fullBlock1, searchedBlocks, false));
        });
        return baseTree;
    }
}
