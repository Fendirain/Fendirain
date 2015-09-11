package fendirain.fendirain.utility.helper;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;

public class BlockLocation implements Comparable<BlockLocation> {

    private final Block block;
    private final BlockPos blockPos;
    private final int damageValue;

    public BlockLocation(Block block, BlockPos blockPos, int damageValue) {
        this.block = block;
        this.blockPos = blockPos;
        this.damageValue = damageValue;
    }

    public Block getBlock() {
        return block;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public int getDamageValue() {
        return damageValue;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(BlockLocation blockLocation) {
        return (Math.abs(this.getBlockPos().getX() - blockLocation.getBlockPos().getX())) + (Math.abs(this.getBlockPos().getY() - blockLocation.getBlockPos().getY())) + (Math.abs(this.getBlockPos().getZ() - blockLocation.getBlockPos().getZ()));
    }
}
