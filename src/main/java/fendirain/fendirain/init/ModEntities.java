package fendirain.fendirain.init;

import cpw.mods.fml.common.registry.EntityRegistry;
import fendirain.fendirain.Fendirain;
import fendirain.fendirain.common.entity.mob.EntityFenderium.EntityFenderiumMob;
import fendirain.fendirain.common.entity.mob.EntityFendinain.EntityFendinainMob;
import fendirain.fendirain.reference.ConfigValues;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;

public class ModEntities {

    public static void init() {
        int entityID;
        // Fendinain
        entityID = EntityRegistry.findGlobalUniqueEntityId();
        EntityRegistry.registerGlobalEntityID(EntityFendinainMob.class, "fendinain", entityID, 0x27624D, 0x212121);
        EntityRegistry.registerModEntity(EntityFendinainMob.class, "Fendinain", entityID, Fendirain.instance, 64, 3, false);
        if (ConfigValues.fendinainMob_enableSpawning) {
            registerSpawningInTreeBiomes(EntityFendinainMob.class, 1);
        }

        // Fenderium
        entityID = EntityRegistry.findGlobalUniqueEntityId();
        EntityRegistry.registerGlobalEntityID(EntityFenderiumMob.class, "fenderium", entityID, 0x24424D, 0x210021);
        EntityRegistry.registerModEntity(EntityFenderiumMob.class, "Fenderium", entityID, Fendirain.instance, 64, 3, false);
        if (ConfigValues.fenderiumMob_enableSpawning) {
            registerSpawningInTreeBiomes(EntityFenderiumMob.class, 2);
        }
    }

    @SuppressWarnings("unchecked")
    private static void registerSpawningInTreeBiomes(Class mobClass, int spawnCutter) {
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.creature, BiomeGenBase.birchForest);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.creature, BiomeGenBase.birchForestHills);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.creature, BiomeGenBase.coldTaiga);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.creature, BiomeGenBase.coldTaigaHills);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.creature, BiomeGenBase.extremeHills);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.creature, BiomeGenBase.extremeHillsEdge);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.creature, BiomeGenBase.extremeHillsPlus);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.creature, BiomeGenBase.forest);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.creature, BiomeGenBase.forestHills);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.creature, BiomeGenBase.megaTaiga);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.creature, BiomeGenBase.megaTaigaHills);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.creature, BiomeGenBase.roofedForest);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.creature, BiomeGenBase.savanna);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.creature, BiomeGenBase.savannaPlateau);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.creature, BiomeGenBase.taiga);
        EntityRegistry.addSpawn(mobClass, 200 / spawnCutter, 1, 1, EnumCreatureType.creature, BiomeGenBase.taigaHills);
        EntityRegistry.addSpawn(mobClass, 150 / spawnCutter, 1, 1, EnumCreatureType.creature, BiomeGenBase.jungleHills);
        EntityRegistry.addSpawn(mobClass, 150 / spawnCutter, 1, 1, EnumCreatureType.creature, BiomeGenBase.jungle);
        EntityRegistry.addSpawn(mobClass, 150 / spawnCutter, 1, 1, EnumCreatureType.creature, BiomeGenBase.jungleEdge);
    }
}
