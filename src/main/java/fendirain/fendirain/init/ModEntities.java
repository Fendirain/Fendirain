package fendirain.fendirain.init;

import cpw.mods.fml.common.registry.EntityRegistry;
import fendirain.fendirain.Fendirain;
import fendirain.fendirain.entity.mob.EntityFendinainMob;
import fendirain.fendirain.reference.Reference;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;

public class ModEntities {

    public static ResourceLocation fendinainMobTexture = new ResourceLocation(Reference.MOD_ID.toLowerCase() + ":" + "textures/mobs/fendinain.png");

    public static void init() {
        int id = 0;
        // Fendinain
        EntityRegistry.registerModEntity(EntityFendinainMob.class, "Fendinain", id, Fendirain.instance, 64, 3, false);
        EntityList.addMapping(EntityFendinainMob.class, "Fendinain", id);
        EntityRegistry.addSpawn(EntityFendinainMob.class, 200, 1, 1, EnumCreatureType.creature, BiomeGenBase.birchForest);
        EntityRegistry.addSpawn(EntityFendinainMob.class, 200, 1, 1, EnumCreatureType.creature, BiomeGenBase.birchForestHills);
        EntityRegistry.addSpawn(EntityFendinainMob.class, 200, 1, 1, EnumCreatureType.creature, BiomeGenBase.coldTaiga);
        EntityRegistry.addSpawn(EntityFendinainMob.class, 200, 1, 1, EnumCreatureType.creature, BiomeGenBase.coldTaigaHills);
        EntityRegistry.addSpawn(EntityFendinainMob.class, 200, 1, 1, EnumCreatureType.creature, BiomeGenBase.extremeHills);
        EntityRegistry.addSpawn(EntityFendinainMob.class, 200, 1, 1, EnumCreatureType.creature, BiomeGenBase.extremeHillsEdge);
        EntityRegistry.addSpawn(EntityFendinainMob.class, 200, 1, 1, EnumCreatureType.creature, BiomeGenBase.extremeHillsPlus);
        EntityRegistry.addSpawn(EntityFendinainMob.class, 200, 1, 1, EnumCreatureType.creature, BiomeGenBase.forest);
        EntityRegistry.addSpawn(EntityFendinainMob.class, 200, 1, 1, EnumCreatureType.creature, BiomeGenBase.forestHills);
        EntityRegistry.addSpawn(EntityFendinainMob.class, 200, 1, 1, EnumCreatureType.creature, BiomeGenBase.megaTaiga);
        EntityRegistry.addSpawn(EntityFendinainMob.class, 200, 1, 1, EnumCreatureType.creature, BiomeGenBase.megaTaigaHills);
        EntityRegistry.addSpawn(EntityFendinainMob.class, 200, 1, 1, EnumCreatureType.creature, BiomeGenBase.roofedForest);
        EntityRegistry.addSpawn(EntityFendinainMob.class, 200, 1, 1, EnumCreatureType.creature, BiomeGenBase.savanna);
        EntityRegistry.addSpawn(EntityFendinainMob.class, 200, 1, 1, EnumCreatureType.creature, BiomeGenBase.savannaPlateau);
        EntityRegistry.addSpawn(EntityFendinainMob.class, 200, 1, 1, EnumCreatureType.creature, BiomeGenBase.taiga);
        EntityRegistry.addSpawn(EntityFendinainMob.class, 200, 1, 1, EnumCreatureType.creature, BiomeGenBase.taigaHills);
        EntityRegistry.addSpawn(EntityFendinainMob.class, 150, 1, 1, EnumCreatureType.creature, BiomeGenBase.jungleHills);
        EntityRegistry.addSpawn(EntityFendinainMob.class, 150, 1, 1, EnumCreatureType.creature, BiomeGenBase.jungle);
        EntityRegistry.addSpawn(EntityFendinainMob.class, 150, 1, 1, EnumCreatureType.creature, BiomeGenBase.jungleEdge);


        //EntityRegistry.registerModEntity(Class entityClass, String entityName, int entityID, Object Mod, int TrackingRange, int UpdateFreq, boolean sendsVelocityUpdates);
    }
}
