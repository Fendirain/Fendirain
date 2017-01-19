package fendirain.fendirain.utility.helper;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

import java.util.LinkedHashSet;
import java.util.Set;

public class BlockTools {
    public static int compareTo(BlockPos blockPos1, BlockPos blockPos2) {
        return (Math.abs(blockPos1.getX() - blockPos2.getX())) + (Math.abs(blockPos1.getY() - blockPos2.getY())) + (Math.abs(blockPos1.getZ() - blockPos2.getZ()));
    }

    public static Set<BlockPos> getSurroundingBlockPos(BlockPos blockPos, int dist) {
        Set<BlockPos> surroundBlockPos = new LinkedHashSet<>();
        for (int y = blockPos.getY() - dist; y <= blockPos.getY() + dist; y++) {
            for (int z = blockPos.getZ() - dist; z <= blockPos.getZ() + dist; z++) {
                for (int x = blockPos.getX() - dist; x <= blockPos.getX() + dist; x++) {
                    if (!(x == blockPos.getX() && y == blockPos.getY() && z == blockPos.getZ()))
                        surroundBlockPos.add(new BlockPos(x, y, z));
                }
            }
        }
        return surroundBlockPos;
    }

    public static int getBlockMeta(IBlockState iBlockState) {
        return iBlockState.getBlock().damageDropped(iBlockState);
    }
}
