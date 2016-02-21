package fendirain.fendirain.worldgen;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class WorldGenCustom extends WorldGenerator {

    private final Block block;
    private final int blockMeta;
    private final Predicate<IBlockState> target;

    public WorldGenCustom(Block block, int blockMeta, Predicate<IBlockState> target) {
        this.block = block;
        this.blockMeta = blockMeta;
        this.target = target;
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos blockPos) {
        if (world.getBlockState(blockPos).getBlock().isReplaceableOreGen(world, blockPos, target)) {
            world.setBlockState(blockPos, this.block.getDefaultState());
            return true;
        }
        return false;
    }

    public boolean generate(World world, Random rand, BlockPos blockPos, int numberOfBlocks) {
        if (world.getBlockState(blockPos).getBlock().isReplaceableOreGen(world, blockPos, target)) {
            world.setBlockState(blockPos, this.block.getStateFromMeta(this.blockMeta), 2);
            int lastDirection = -1;
            while (numberOfBlocks > 1) {
                int randomInt = rand.nextInt(6);
                while (randomInt == lastDirection) randomInt = rand.nextInt(6);
                switch (randomInt) {
                    case 0:
                        blockPos.east();
                        break;
                    case 1:
                        blockPos.west();
                        break;
                    case 2:
                        blockPos.up();
                        break;
                    case 3:
                        blockPos.down();
                        break;
                    case 4:
                        blockPos.south();
                        break;
                    case 5:
                        blockPos.north();
                        break;
                }
                if (world.getBlockState(blockPos).getBlock().isReplaceableOreGen(world, blockPos, this.target))
                    world.setBlockState(blockPos, this.block.getStateFromMeta(this.blockMeta), 2);
                lastDirection = randomInt;
                numberOfBlocks--;
            }
            return true;
        }
        return false;
    }
}
