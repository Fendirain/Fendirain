package fendirain.fendirain.worldgen;

import cpw.mods.fml.common.IWorldGenerator;
import fendirain.fendirain.init.ModBlocks;
import fendirain.fendirain.reference.ConfigValues;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.Random;

public class FendiOreGenerator implements IWorldGenerator {

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        if (ConfigValues.isGenerationEnabled) {
            Block blockToGenerate = ModBlocks.blockOreFendi;
            switch (world.provider.dimensionId) {
                case -1: // Nether
                    break;
                case 0: // Over-world
                    generateOres(world, random, blockToGenerate, Blocks.stone, chunkX, chunkZ, 4, 1, 88, 1);
                    break;
                case 1: // End
                    break;
            }
        }
    }

    private void generateOres(World world, Random rand, Block blockToGenerate, Block blockToReplace, int chunkX, int chunkZ, int maxVienSize, int minHeight, int maxHeight, int chanceToGenerate) {
        int heightDiff = (maxHeight - minHeight) + 1;
        WorldGenCustom worldGenCustom = new WorldGenCustom(blockToGenerate, maxVienSize, blockToReplace);
        for (int i = 1; i <= chanceToGenerate; i++) {
            if (rand.nextInt(4) == 1) {
                int x = chunkX + rand.nextInt(16), y = minHeight + rand.nextInt(heightDiff), z = chunkZ + rand.nextInt(16);
                int numberOfOre = 1;
                while (rand.nextInt(12) == 2) {
                    numberOfOre = 2;
                }
                worldGenCustom.generate(world, rand, x, y, z, numberOfOre);
            }
        }
    }
}