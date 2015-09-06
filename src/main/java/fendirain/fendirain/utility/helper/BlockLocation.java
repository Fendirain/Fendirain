package fendirain.fendirain.utility.helper;

import net.minecraft.block.Block;

public class BlockLocation implements Comparable<BlockLocation> {

    private final Block block;
    private final int posX;
    private final int posY;
    private final int posZ;
    private final int damageValue;

    public BlockLocation(Block block, int posX, int posY, int posZ, int damageValue) {
        this.block = block;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.damageValue = damageValue;
    }

    public Block getBlock() {
        return block;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getPosZ() {
        return posZ;
    }

    public int getDamageValue() {
        return damageValue;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(BlockLocation blockLocation) {
        return (Math.abs(this.getPosX() - blockLocation.getPosX())) + (Math.abs(this.getPosY() - blockLocation.getPosY())) + (Math.abs(this.getPosZ() - blockLocation.getPosZ()));
    }
}
