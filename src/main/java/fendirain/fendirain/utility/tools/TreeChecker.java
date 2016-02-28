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
            Set<BlockPos> connectedBlocks = getBaseTree(world, blockPos);
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

    private static Set<BlockPos> getBaseTree(World world, BlockPos blockPos) {
        Set<BlockPos> baseTree = new LinkedHashSet<>();
        Set<Long> searchedBlocks = new HashSet<>();

        int maxRange = 8;

        searchedBlocks.add(blockPos.toLong());
        baseTree.add(blockPos);

        Set<BlockPos> blocksToSearch = new LinkedHashSet<>();
        blocksToSearch.add(blockPos);

        while (!blocksToSearch.isEmpty()) {
            Set<BlockPos> currentTask = new LinkedHashSet<>(blocksToSearch);
            blocksToSearch.clear();
            currentTask.forEach(blockPos1 -> BlockTools.getSurroundingBlockPos(blockPos1, 1).stream().filter(blockPosChecking -> {
                if (searchedBlocks.contains(blockPosChecking.toLong())) return false;
                searchedBlocks.add(blockPosChecking.toLong());
                if (!(blockPosChecking.getX() < blockPos.getX() + maxRange && blockPosChecking.getX() > blockPos.getX() - maxRange && blockPosChecking.getZ() < blockPos.getZ() + maxRange && blockPosChecking.getZ() > blockPos.getZ() - maxRange))
                    return false;
                Block block = world.getBlockState(blockPosChecking).getBlock();
                return ((block.isWood(world, blockPosChecking) || (block instanceof BlockLeavesBase && world.getBlockState(blockPosChecking).getValue(BlockLeaves.DECAYABLE))));
            }).forEach(blockPosAllowed -> {
                baseTree.add(blockPosAllowed);
                if (world.getBlockState(blockPosAllowed).getBlock().isWood(world, blockPosAllowed))
                    blocksToSearch.add(blockPosAllowed);
            }));
        }

        return baseTree;
    }
}
