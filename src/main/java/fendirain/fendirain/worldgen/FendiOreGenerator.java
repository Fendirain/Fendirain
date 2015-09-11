package fendirain.fendirain.worldgen;

import fendirain.fendirain.init.ModBlocks;
import fendirain.fendirain.reference.ConfigValues;
import net.minecraft.block.Block;
import net.minecraft.block.state.pattern.BlockHelper;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class FendiOreGenerator implements IWorldGenerator {

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        if (ConfigValues.isGenerationEnabled) {
            Block blockToGenerate = ModBlocks.fendiOre;
            switch (world.provider.getDimensionId()) {
                // Nether
                case -1:
                    break;
                // Over-world
                case 0:
                    generateOres(world, random, blockToGenerate, Blocks.stone, chunkX, chunkZ, 4, 1, 88, 1);
                    break;
                // End
                case 1:
                    break;
            }
        }
    }

    private void generateOres(World world, Random rand, Block blockToGenerate, Block blockToReplace, int chunkX, int chunkZ, int maxVienSize, int minHeight, int maxHeight, int chanceToGenerate) {
        int heightDiff = (maxHeight - minHeight) + 1;
        WorldGenCustom worldGenCustom = new WorldGenCustom(blockToGenerate, maxVienSize, BlockHelper.forBlock(blockToReplace));
        for (int i = 1; i <= chanceToGenerate; i++) {
            if (rand.nextInt(4) == 1) {
                BlockPos blockPos = new BlockPos(chunkX + rand.nextInt(16), minHeight + rand.nextInt(heightDiff), chunkZ + rand.nextInt(16));
                int numberOfOre = 1;
                while (rand.nextInt(12) == 2) {
                    numberOfOre = 2;
                }
                worldGenCustom.generate(world, rand, blockPos, numberOfOre);
            }
        }
    }
}