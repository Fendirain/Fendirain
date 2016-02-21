package fendirain.fendirain.init;

import fendirain.fendirain.Fendirain;
import fendirain.fendirain.common.entity.mob.EntityFenderium.EntityFenderiumMob;
import fendirain.fendirain.common.entity.mob.EntityFendinain.EntityFendinainMob;
import fendirain.fendirain.reference.ConfigValues;
import fendirain.fendirain.reference.EntityNames;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;
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
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, BiomeGenBase.birchForest);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, BiomeGenBase.birchForestHills);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, BiomeGenBase.coldTaiga);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, BiomeGenBase.coldTaigaHills);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, BiomeGenBase.extremeHills);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, BiomeGenBase.extremeHillsEdge);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, BiomeGenBase.extremeHillsPlus);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, BiomeGenBase.forest);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, BiomeGenBase.forestHills);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, BiomeGenBase.megaTaiga);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, BiomeGenBase.megaTaigaHills);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, BiomeGenBase.roofedForest);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, BiomeGenBase.savanna);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, BiomeGenBase.savannaPlateau);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, BiomeGenBase.taiga);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, BiomeGenBase.taigaHills);
        EntityRegistry.addSpawn(mobClass, 150 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, BiomeGenBase.jungleHills);
        EntityRegistry.addSpawn(mobClass, 150 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, BiomeGenBase.jungle);
        EntityRegistry.addSpawn(mobClass, 150 / spawnCutter, 1, 1, EnumCreatureType.CREATURE, BiomeGenBase.jungleEdge);
    }
}
