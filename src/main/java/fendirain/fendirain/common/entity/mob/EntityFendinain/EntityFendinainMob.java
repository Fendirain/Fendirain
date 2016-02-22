package fendirain.fendirain.common.entity.mob.EntityFendinain;

import fendirain.fendirain.common.entity.mob.EntityFendinain.AI.EntityAIBegPlayer;
import fendirain.fendirain.common.entity.mob.EntityFendinain.AI.EntityAICollectSaplings;
import fendirain.fendirain.common.entity.mob.EntityFendinain.AI.EntityAIPlantSapling;
import fendirain.fendirain.reference.ConfigValues;
import fendirain.fendirain.reference.Reference;
import fendirain.fendirain.utility.helper.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.ArrayList;
import java.util.Arrays;

public class EntityFendinainMob extends EntityCreature implements IInventory {
    private final int inventorySize = 6, maxStackSize = 12;
    private final EntityAICollectSaplings entityAICollectSaplings;
    private final EntityAIPlantSapling entityAIPlantSapling;
    private ItemStack[] inventory = new ItemStack[inventorySize];
    private boolean firstUpdate;

    public EntityFendinainMob(World world) {
        super(world);
        this.setSize(.39F, .85F);
        this.entityAIPlantSapling = new EntityAIPlantSapling(this, ConfigValues.fendinainMob_minTimeToWaitToPlant, ConfigValues.fendinainMob_maxTimeToWaitToPlant);
        this.entityAICollectSaplings = new EntityAICollectSaplings(this, 1F);
        firstUpdate = true;
        ((PathNavigateGround) this.getNavigator()).setAvoidsWater(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 1.2F));
        this.tasks.addTask(2, entityAIPlantSapling);
        this.tasks.addTask(2, entityAICollectSaplings);
        this.tasks.addTask(3, new EntityAIBegPlayer(this, 1.0F, this.rand));
        this.tasks.addTask(4, new EntityAIWander(this, 1.0F));
        this.tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
    }

    @Override
    public void onLivingUpdate() {
        if (firstUpdate) {
            this.addNewSpawnInventory();
            this.setCurrentItemOrArmor(0, this.getRandomSlot());
            firstUpdate = false;
        }
        super.onLivingUpdate();
        this.entityAIPlantSapling.addToTimeSinceLastPlacement(1);
    }

    private void addNewSpawnInventory() {
        String biome = this.worldObj.getBiomeGenForCoords(new BlockPos(this.posX, this.posY, this.posZ)).biomeName;
        int amountOfSaplings = rand.nextInt(this.getInventoryStackLimit()) + 1;
        // Adds the proper type of saplings for the biome it's spawned in. Done this way for future compatibility with mods. May be changed later.
        if (biome != null) {
            ArrayList<String> saplings = new ArrayList<>();
            // Mixed Oak or Spruce
            saplings.add(BiomeGenBase.extremeHills.biomeName);
            saplings.add(BiomeGenBase.extremeHillsEdge.biomeName);
            saplings.add(BiomeGenBase.extremeHillsPlus.biomeName);
            if (saplings.contains(biome)) {
                if (rand.nextInt(2) == 0) {
                    // Oak Saplings
                    this.putIntoInventory(new ItemStack(Blocks.sapling, amountOfSaplings, 0));
                } else {
                    // Spruce Saplings
                    this.putIntoInventory(new ItemStack(Blocks.sapling, amountOfSaplings, 1));
                }
            } else {
                saplings.clear();
                // Mixed Birch or Oak
                saplings.add(BiomeGenBase.forest.biomeName);
                saplings.add(BiomeGenBase.forestHills.biomeName);
                if (saplings.contains(biome)) {
                    if (rand.nextInt(10) == 0) {
                        // Birch Saplings
                        this.putIntoInventory(new ItemStack(Blocks.sapling, amountOfSaplings, 2));
                    } else {
                        // Oak Saplings
                        this.putIntoInventory(new ItemStack(Blocks.sapling, amountOfSaplings, 0));
                    }
                } else {
                    saplings.clear();
                    saplings.add(BiomeGenBase.plains.biomeName);
                    if (saplings.contains(biome)) {
                        // Oak Saplings
                        if (biome.matches(BiomeGenBase.plains.biomeName))
                            this.putIntoInventory(new ItemStack(Blocks.sapling, amountOfSaplings / 2, 0)); // Since its planes, It should start with less.
                        this.putIntoInventory(new ItemStack(Blocks.sapling, amountOfSaplings, 0));
                    } else {
                        saplings.clear();
                        saplings.add(BiomeGenBase.coldTaiga.biomeName);
                        saplings.add(BiomeGenBase.coldTaigaHills.biomeName);
                        saplings.add(BiomeGenBase.taiga.biomeName);
                        saplings.add(BiomeGenBase.taigaHills.biomeName);
                        saplings.add(BiomeGenBase.megaTaiga.biomeName);
                        saplings.add(BiomeGenBase.megaTaigaHills.biomeName);
                        if (saplings.contains(biome)) {
                            // Spruce Saplings
                            this.putIntoInventory(new ItemStack(Blocks.sapling, amountOfSaplings, 1));
                        } else {
                            saplings.clear();
                            saplings.add(BiomeGenBase.birchForest.biomeName);
                            saplings.add(BiomeGenBase.birchForestHills.biomeName);
                            if (saplings.contains(biome)) {
                                // Birch Saplings
                                this.putIntoInventory(new ItemStack(Blocks.sapling, amountOfSaplings, 2));
                            } else {
                                saplings.clear();
                                saplings.add(BiomeGenBase.jungle.biomeName);
                                saplings.add(BiomeGenBase.jungleEdge.biomeName);
                                saplings.add(BiomeGenBase.jungleHills.biomeName);
                                if (saplings.contains(biome)) {
                                    // Jungle Saplings
                                    this.putIntoInventory(new ItemStack(Blocks.sapling, amountOfSaplings, 3));
                                } else {
                                    saplings.clear();
                                    saplings.add(BiomeGenBase.savanna.biomeName);
                                    saplings.add(BiomeGenBase.savannaPlateau.biomeName);
                                    if (saplings.contains(biome)) {
                                        // Acacia Saplings
                                        this.putIntoInventory(new ItemStack(Blocks.sapling, amountOfSaplings, 4));
                                    } else {
                                        saplings.clear();
                                        saplings.add(BiomeGenBase.roofedForest.biomeName);
                                        if (saplings.contains(biome)) {
                                            // Roofed Oak Saplings
                                            this.putIntoInventory(new ItemStack(Blocks.sapling, amountOfSaplings, 5));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    @Override
    public void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(25.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3D);
    }

    @Override
    public boolean getCanSpawnHere() {
        return super.getCanSpawnHere() && (worldObj.getBlockState(new BlockPos(this.posX, this.posY - 1, this.posZ)) != Blocks.leaves || worldObj.getBlockState(new BlockPos(this.posX, this.posY - 1, this.posZ)) != Blocks.leaves2);
    }

    @Override
    public boolean allowLeashing() {
        return false;
    }

    @Override
    public boolean interact(EntityPlayer entityPlayer) {
        ItemStack itemStack = entityPlayer.inventory.getCurrentItem();
        if (itemStack != null) {
            if (this.isValidForPickup(itemStack.getItem()) && this.isAnySpaceForItemPickup(itemStack)) {
                putIntoInventory(itemStack);
                return true;
            }

            if (ConfigValues.isDebugSettingsEnabled) {
                // Test / Debug Code Following
                if (itemStack.getItem() == Items.wooden_hoe) {
                    this.onDeath(DamageSource.causePlayerDamage(entityPlayer));
                    return true;
                } else if (itemStack.getItem() == Items.arrow) {
                    String[] printQueue = new String[this.inventorySize];
                    for (int slot = 0; slot < inventory.length; slot++) {
                        if (inventory[slot] != null) {
                            printQueue[slot] = (inventory[slot].getItem().getItemStackDisplayName(inventory[slot]) + "x" + inventory[slot].stackSize);
                        } else {
                            printQueue[slot] = (slot + " is null");
                        }
                    }
                    if (!worldObj.isRemote) {
                        entityPlayer.addChatMessage(new ChatComponentText(Arrays.toString(printQueue)));
                    }
                    LogHelper.info(Arrays.toString(printQueue));
                    return true;
                } else if (itemStack.getItem() == Items.blaze_rod) {
                    entityAIPlantSapling.startExecuting();
                    return true;
                } else if (itemStack.getItem() == Items.diamond_axe) {
                    for (int slot = 0; slot < inventory.length; slot++) {
                        inventory[slot] = null;
                    }
                    this.setCurrentItemOrArmor(0, null);
                    return true;
                } else if (itemStack.getItem() == Items.wooden_axe) {
                    Boolean alreadyChanged = false;
                    for (int slot = 0; slot < inventory.length; slot++) {
                        if (inventory[slot] != null) {
                            if (alreadyChanged) {
                                inventory[slot] = null;
                            } else {
                                inventory[slot].stackSize -= 1;
                                if (inventory[slot].stackSize == 0) {
                                    inventory[slot] = null;
                                }
                                alreadyChanged = true;
                            }
                        }
                    }
                    this.setCurrentItemOrArmor(0, this.getRandomSlot());
                    return true;
                } else if (itemStack.getItem() == Items.paper) {
                    String[] printQueue = new String[this.inventorySize];
                    for (int slot = 0; slot < inventory.length; slot++) {
                        if (inventory[slot] != null) {
                            printQueue[slot] = (inventory[slot].getItem().getItemStackDisplayName(inventory[slot]) + "x" + inventory[slot].stackSize);
                        } else {
                            printQueue[slot] = (slot + " is null");
                        }
                    }
                    if (!worldObj.isRemote) {
                        entityPlayer.addChatMessage(new ChatComponentText("Health: " + this.getHealth()));
                        entityPlayer.addChatMessage(new ChatComponentText("Last Placed: " + entityAIPlantSapling.getTimeSinceLastPlacement() + '/' + ConfigValues.fendinainMob_maxTimeToWaitToPlant + " (Max)"));
                        LogHelper.info("Health: " + this.getHealth());
                        LogHelper.info("Last Placed: " + entityAIPlantSapling.getTimeSinceLastPlacement() + '/' + ConfigValues.fendinainMob_maxTimeToWaitToPlant + " (Max)");
                        for (Object object : this.getActivePotionEffects()) {
                            PotionEffect potionEffect = (PotionEffect) object;
                            LogHelper.info("Potion: " + potionEffect.getEffectName() + " - " + potionEffect.getAmplifier() + " - " + potionEffect.getDuration());
                            entityPlayer.addChatMessage(new ChatComponentText("Potion: " + potionEffect.getEffectName() + " - " + potionEffect.getAmplifier() + " - " + potionEffect.getDuration()));
                        }
                        entityPlayer.addChatMessage(new ChatComponentText("Inventory: " + Arrays.toString(printQueue)));
                        LogHelper.info("Inventory: " + Arrays.toString(printQueue));
                        entityPlayer.addChatMessage(new ChatComponentText("Percent Full: " + this.getPercentageOfInventoryFull()));
                        LogHelper.info("Percent Full: " + this.getPercentageOfInventoryFull());
                        int timesKilled;
                        if (entityPlayer.getEntityData().hasKey("fendirainMobOne"))
                            timesKilled = entityPlayer.getEntityData().getInteger("fendirainMobOne");
                        else timesKilled = 0;
                        entityPlayer.addChatMessage(new ChatComponentText("Killed by: \"" + entityPlayer.getName() + "\" " + timesKilled + " time(s)."));
                        LogHelper.info("Killed by: \"" + entityPlayer.getName() + "\" " + timesKilled + " time(s).");
                    }
                    return true;
                }  // End Test / Debug Code
            }
        }
        return false;
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        if (damageSource.getEntity() != null && damageSource.getEntity() instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer) damageSource.getEntity();
            if (!entityPlayer.capabilities.isCreativeMode || entityPlayer.getHeldItem().getItem() instanceof ItemHoe) {
                NBTTagCompound nbtTagCompound = entityPlayer.getEntityData();
                // Named as "fendirainMobOne" for simplicity in the future if I decide to change the mobs name.
                if (nbtTagCompound.hasKey("fendirainMobOne"))
                    nbtTagCompound.setInteger("fendirainMobOne", nbtTagCompound.getInteger("fendirainMobOne") + 1);
                else nbtTagCompound.setInteger("fendirainMobOne", 1);
            }
        }

    }

    public void putIntoInventory(ItemStack itemStack) {
        if ((itemStack != null && itemStack.stackSize > 0) && (itemStack.getItem() instanceof ItemBlock && Block.getBlockFromItem(itemStack.getItem()) == Blocks.sapling)) {
            for (int slot = 0; slot < inventory.length; slot++) {
                if (inventory[slot] == null) {
                    int amountToAdd;
                    if (this.getInventoryStackLimit() < itemStack.stackSize) {
                        amountToAdd = this.getInventoryStackLimit();
                    } else amountToAdd = itemStack.stackSize;
                    inventory[slot] = itemStack.copy();
                    inventory[slot].stackSize = amountToAdd;
                    itemStack.stackSize -= amountToAdd;
                } else if (inventory[slot].getUnlocalizedName().matches(itemStack.getUnlocalizedName())) {
                    int freeSpace = this.getInventoryStackLimit() - this.inventory[slot].stackSize, amountToAdd;
                    if (freeSpace < itemStack.stackSize) {
                        amountToAdd = freeSpace;
                    } else amountToAdd = itemStack.stackSize;
                    this.inventory[slot].stackSize += amountToAdd;
                    itemStack.stackSize -= amountToAdd;
                }
                if (itemStack.stackSize == 0) break;
            }
            if (this.getHeldItem() == null) {
                this.setCurrentItemOrArmor(0, getRandomSlot());
            }
        }
    }

    @Override
    public void dropFewItems(boolean wasHitRecently, int lootingLevel) {
        for (ItemStack itemStack : inventory) {
            if (itemStack != null) {
                this.entityDropItem(itemStack, 1F);
            }
        }
    }

    @Override
    public String getLivingSound() {
        return null;
    }

    @Override
    public String getHurtSound() {
        return Reference.MOD_ID + ":" + "mob.fendirain.hurt";
    }

    @Override
    public String getDeathSound() {
        return Reference.MOD_ID + ":" + "mob.fendirain.hurt";
    }

    @Override
    public float getSoundVolume() {
        return 0.6F;
    }

    @Override
    public boolean canTriggerWalking() {
        return false;
    }

    @Override
    public boolean canDespawn() {
        return false;
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEFINED;
    }

    @Override
    public int getSizeInventory() {
        return 6;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.inventory[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        ItemStack itemstack;
        if (this.inventory[slot] != null) {
            if (this.inventory[slot].stackSize <= amount) {
                itemstack = this.inventory[slot];
                this.inventory[slot] = null;
                return itemstack;
            } else {
                itemstack = this.inventory[slot].splitStack(amount);
                if (this.inventory[slot].stackSize == 0) {
                    this.inventory[slot] = null;
                }
                return itemstack;
            }
        } else return null;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        inventory[index] = null;
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack item) {
        this.inventory[slot] = item;
        if (item != null && item.stackSize > this.getInventoryStackLimit()) {
            item.stackSize = this.getInventoryStackLimit();
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return maxStackSize;
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityPlayer) {
        return false;
    }

    @Override
    public void openInventory(EntityPlayer entityPlayer) {

    }

    @Override
    public void closeInventory(EntityPlayer entityPlayer) {

    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
        return itemStack.getItem() instanceof ItemBlock && Block.getBlockFromItem(itemStack.getItem()) == Blocks.sapling;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {

    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
        NBTTagList nbttaglist = nbtTagCompound.getTagList("Items", 10);
        this.inventory = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbtTagCompound1 = nbttaglist.getCompoundTagAt(i);
            int j = nbtTagCompound1.getByte("Slot") & 255;

            if (j >= 0 && j < this.inventory.length) {
                this.inventory[j] = ItemStack.loadItemStackFromNBT(nbtTagCompound1);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);
        NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < this.inventory.length; ++i) {
            if (this.inventory[i] != null) {
                NBTTagCompound nbtTagCompound1 = new NBTTagCompound();
                nbtTagCompound1.setByte("Slot", (byte) i);
                this.inventory[i].writeToNBT(nbtTagCompound1);
                nbttaglist.appendTag(nbtTagCompound1);
            }
        }
        nbtTagCompound.setTag("Items", nbttaglist);
    }

    public ItemStack getItemToPlace() {
        return this.getHeldItem();
    }

    private ItemStack getRandomSlot() {
        ArrayList<ItemStack> items = new ArrayList<>();
        for (ItemStack item : inventory) {
            if (item != null) {
                boolean contained = false;
                for (ItemStack item1 : items) {
                    if (item1 != null && item1.getUnlocalizedName().matches(item.getUnlocalizedName())) {
                        contained = true;
                        break;
                    }
                }
                if (!contained) {
                    items.add(item);
                }
            }
        }
        if (!items.isEmpty()) {
            return items.get(rand.nextInt(items.size()));
        } else return null;
    }

    public boolean isAnySpaceForItemPickup(ItemStack item) {
        if (item != null) {
            for (ItemStack itemStack : inventory) {
                if (itemStack == null || itemStack.getUnlocalizedName().matches(item.getUnlocalizedName()) && itemStack.stackSize != this.getInventoryStackLimit()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isItemToPlace() {
        for (ItemStack itemStack : inventory) {
            if (itemStack != null) {
                return true;
            }
        }
        return false;
    }

    public boolean isValidForPickup(Item item) {
        return item instanceof ItemBlock && Block.getBlockFromItem(item) == Blocks.sapling;
    }

    public void removeItemFromInventory(ItemStack itemStack, int amount, boolean resetCurrentSapling) {
        for (int slot = inventory.length - 1; slot >= 0; slot--) {
            if (inventory[slot] != null && inventory[slot].getUnlocalizedName().matches(itemStack.getUnlocalizedName())) {
                if (inventory[slot].stackSize >= amount) {
                    inventory[slot].stackSize -= amount;
                    amount = 0;
                    if (inventory[slot].stackSize <= 0) {
                        inventory[slot] = null;
                    }
                } else {
                    amount -= inventory[slot].stackSize;
                    inventory[slot] = null;
                }
                if (resetCurrentSapling) {
                    this.setCurrentItemOrArmor(0, this.getRandomSlot());
                }
                if (amount == 0) {
                    break;
                }
            }
        }
    }

    public int getPercentageOfInventoryFull() {
        int maxSize = this.maxStackSize * this.inventorySize, inventoryAmount = 0;
        boolean slotNull = false;
        for (ItemStack itemStack : inventory) {
            if (itemStack != null) {
                inventoryAmount += itemStack.stackSize;
            } else slotNull = true;
        }
        if (inventoryAmount > 0) {
            int result = (inventoryAmount * 100) / maxSize;
            if (!slotNull && result < 50) {
                result = 50;
            }
            return result;
        } else return 0;
    }
}
