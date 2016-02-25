package fendirain.fendirain.utility.helper;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;

import java.util.LinkedHashSet;
import java.util.Set;

public class FullBlock implements Comparable<FullBlock> {

    private final net.minecraft.block.Block block;
    private final BlockPos blockPos;
    private final int damageValue;

    public FullBlock(net.minecraft.block.Block block, BlockPos blockPos, int damageValue) {
        this.block = block;
        this.blockPos = blockPos;
        this.damageValue = damageValue;
    }

    public FullBlock(NBTTagCompound nbtTagCompound) {
        block = net.minecraft.block.Block.getBlockFromName(nbtTagCompound.getString("blockMaterial"));
        blockPos = BlockPos.fromLong(nbtTagCompound.getLong("blockPos"));
        damageValue = nbtTagCompound.getInteger("blockDamageValue");
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
    public int compareTo(FullBlock fullBlock) {
        return (Math.abs(this.getBlockPos().getX() - fullBlock.getBlockPos().getX())) + (Math.abs(this.getBlockPos().getY() - fullBlock.getBlockPos().getY())) + (Math.abs(this.getBlockPos().getZ() - fullBlock.getBlockPos().getZ()));
    }

    public boolean isSameType(FullBlock fullBlock) {
        return block == fullBlock.getBlock();
    }

    public boolean isSameBlock(FullBlock fullBlock) {
        return block == fullBlock.getBlock() && blockPos.toLong() == fullBlock.blockPos.toLong();
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
        nbtTagCompound.setString("blockMaterial", block.getUnlocalizedName());
        nbtTagCompound.setLong("blockPos", blockPos.toLong());
        nbtTagCompound.setInteger("blockDamageValue", damageValue);
        return nbtTagCompound;
    }

    public Set<BlockPos> getSurroundingBlockPos(int dist) {
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
}
