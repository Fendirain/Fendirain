package fendirain.fendirain.init;

import fendirain.fendirain.Fendirain;
import fendirain.fendirain.common.entity.mob.EntityFenderium.EntityFenderiumMob;
import fendirain.fendirain.common.entity.mob.EntityFendinain.EntityFendinainMob;
import fendirain.fendirain.reference.ConfigValues;
import fendirain.fendirain.reference.EntityNames;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ModEntities {

    @SuppressWarnings("UnusedAssignment")
    public static void init() {

        // Fendinain
        EntityRegistry.registerModEntity(EntityFendinainMob.class, EntityNames.EntityFendinain, 0, Fendirain.instance, 64, 3, true, 0x27624D, 0x212121);
        if (ConfigValues.fendinainMob_enableSpawning) registerSpawningInTreeBiomes(EntityFendinainMob.class, 1);
        // Fenderium
        EntityRegistry.registerModEntity(EntityFenderiumMob.class, EntityNames.EntityFenderium, 1, Fendirain.instance, 64, 3, true, 0x24424D, 0x210021);
        if (ConfigValues.fenderiumMob_enableSpawning) registerSpawningInTreeBiomes(EntityFenderiumMob.class, 2);
    }

    @SuppressWarnings("unchecked")
    private static void registerSpawningInTreeBiomes(Class mobClass, int spawnCutter) {
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, Biomes.BIRCH_FOREST);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, Biomes.BIRCH_FOREST_HILLS);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, Biomes.COLD_TAIGA);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, Biomes.COLD_TAIGA_HILLS);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, Biomes.EXTREME_HILLS);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, Biomes.EXTREME_HILLS_EDGE);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, Biomes.EXTREME_HILLS_WITH_TREES);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, Biomes.FOREST);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, Biomes.FOREST_HILLS);
        //EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, Biomes.megaTaiga); //TODO Fix
        //EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, Biomes.megaTaigaHills);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, Biomes.ROOFED_FOREST);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, Biomes.SAVANNA);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, Biomes.SAVANNA_PLATEAU);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, Biomes.TAIGA);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, Biomes.TAIGA_HILLS);
        EntityRegistry.addSpawn(mobClass, 150 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, Biomes.JUNGLE_HILLS);
        EntityRegistry.addSpawn(mobClass, 150 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, Biomes.JUNGLE);
        EntityRegistry.addSpawn(mobClass, 150 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, Biomes.JUNGLE_EDGE);
    }
}
