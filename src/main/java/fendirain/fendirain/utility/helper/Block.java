package fendirain.fendirain.utility.helper;

import net.minecraft.util.BlockPos;

public class Block implements Comparable<Block> {

    private final net.minecraft.block.Block block;
    private final BlockPos blockPos;
    private final int damageValue;

    public Block(net.minecraft.block.Block block, BlockPos blockPos, int damageValue) {
        this.block = block;
        this.blockPos = blockPos;
        this.damageValue = damageValue;
    }

    public net.minecraft.block.Block getBlock() {
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
    public int compareTo(Block block) {
        return (Math.abs(this.getBlockPos().getX() - block.getBlockPos().getX())) + (Math.abs(this.getBlockPos().getY() - block.getBlockPos().getY())) + (Math.abs(this.getBlockPos().getZ() - block.getBlockPos().getZ()));
    }
}
