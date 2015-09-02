package fendirain.fendirain.utility.helper;

import net.minecraft.block.Block;

public class BlockLocation {

    private final Block block;
    private final int posX;
    private final int posY;
    private final int posZ;

    public BlockLocation(Block block, int posX, int posY, int posZ) {
        this.block = block;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
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
}
