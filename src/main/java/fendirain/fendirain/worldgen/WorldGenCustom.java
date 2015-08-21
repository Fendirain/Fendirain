package fendirain.fendirain.worldgen;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class WorldGenCustom extends WorldGenerator {

    private Block block;
    private int blockMeta;
    private Block target;

    public WorldGenCustom(Block block, int blockMeta, Block target) {
        this.block = block;
        this.blockMeta = blockMeta;
        this.target = target;
    }

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        if (world.getBlock(x, y, z).isReplaceableOreGen(world, x, y, z, this.target)) {
            world.setBlock(x, y, z, this.block, this.blockMeta, 2);
            return true;
        }
        return false;
    }

    public boolean generate(World world, Random rand, int x, int y, int z, int numberOfBlocks) {
        if (world.getBlock(x, y, z).isReplaceableOreGen(world, x, y, z, this.target)) {
            world.setBlock(x, y, z, this.block, this.blockMeta, 2);
            int lastDirection = -1;
            while (numberOfBlocks > 1) {
                int randomInt = rand.nextInt(6);
                while (randomInt == lastDirection) {
                    randomInt = rand.nextInt(6);
                }
                switch (randomInt) {
                    case 0:
                        x++;
                        break;
                    case 1:
                        x--;
                        break;
                    case 2:
                        y++;
                        break;
                    case 3:
                        y--;
                        break;
                    case 4:
                        z++;
                    case 5:
                        z--;
                        break;
                }
                if (world.getBlock(x, y, z).isReplaceableOreGen(world, x, y, z, this.target)) {
                    world.setBlock(x, y, z, this.block, this.blockMeta, 2);
                }
                lastDirection = randomInt;
                numberOfBlocks--;
            }
            return true;
        }
        return false;
    }
}
