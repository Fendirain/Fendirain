package fendirain.fendirain.common.entity.mob.EntityFendinain;

import fendirain.fendirain.common.entity.mob.EntityFendinain.AI.EntityAIBegPlayer;
import fendirain.fendirain.common.entity.mob.EntityFendinain.AI.EntityAICollectSaplings;
import fendirain.fendirain.common.entity.mob.EntityFendinain.AI.EntityAIPlantSapling;
import fendirain.fendirain.init.ModCompatibility;
import fendirain.fendirain.init.ModItems;
import fendirain.fendirain.reference.ConfigValues;
import fendirain.fendirain.utility.helper.LogHelper;
import net.minecraft.block.BlockPlanks;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;

public class EntityFendinainMob extends EntityCreature implements IInventory {
    private final int inventorySize = 6, maxStackSize = 12;
    private EntityAICollectSaplings entityAICollectSaplings;
    private EntityAIPlantSapling entityAIPlantSapling;
    private ItemStack[] inventory = new ItemStack[inventorySize];
    private boolean firstUpdate;

    public EntityFendinainMob(World world) {
        super(world);
        this.setSize(.39F, .85F);
        firstUpdate = true;
        //((PathNavigateGround) this.getNavigator()).setAvoidsWater(true);
        this.entityAIPlantSapling = new EntityAIPlantSapling(this, ConfigValues.fendinainMob_minTimeToWaitToPlant, ConfigValues.fendinainMob_maxTimeToWaitToPlant);
        this.entityAICollectSaplings = new EntityAICollectSaplings(this, 1F);
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
            if (this.getHeldItem(EnumHand.MAIN_HAND).isEmpty())
                this.setHeldItem(EnumHand.MAIN_HAND, this.getRandomSlot());
            firstUpdate = false;
        }
        super.onLivingUpdate();
        this.entityAIPlantSapling.addToTimeSinceLastPlacement(1);
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficultyInstance, IEntityLivingData iEntityLivingData) {
        this.addNewSpawnInventory();
        this.setHeldItem(EnumHand.MAIN_HAND, this.getRandomSlot());
        return iEntityLivingData;
    }

    private void addNewSpawnInventory() {
        String biome = this.world.getBiome(new BlockPos(this.posX, this.posY, this.posZ)).getBiomeName();
        int amountOfSaplings = rand.nextInt(this.getInventoryStackLimit()) + 1;
        // Adds the proper type of saplings for the biome it's spawned in. Done this way for future compatibility with mods. May be changed later.
        ArrayList<String> saplings = new ArrayList<>();
        // Mixed Oak or Spruce
        saplings.add(Biomes.EXTREME_HILLS.getBiomeName());
        saplings.add(Biomes.EXTREME_HILLS_EDGE.getBiomeName());
        saplings.add(Biomes.EXTREME_HILLS_WITH_TREES.getBiomeName());
        if (saplings.contains(biome)) {
            if (rand.nextInt(2) == 0) {
                // Oak Saplings
                this.putIntoInventory(new ItemStack(Blocks.SAPLING, amountOfSaplings, BlockPlanks.EnumType.OAK.getMetadata()));
            } else {
                // Spruce Saplings
                this.putIntoInventory(new ItemStack(Blocks.SAPLING, amountOfSaplings, BlockPlanks.EnumType.SPRUCE.getMetadata()));
            }
        } else {
            saplings.clear();
            // Mixed Birch or Oak
            saplings.add(Biomes.FOREST.getBiomeName());
            saplings.add(Biomes.FOREST_HILLS.getBiomeName());
            if (saplings.contains(biome)) {
                if (rand.nextInt(10) == 0) {
                    this.putIntoInventory(new ItemStack(Blocks.SAPLING, amountOfSaplings, BlockPlanks.EnumType.BIRCH.getMetadata()));
                } else {
                    this.putIntoInventory(new ItemStack(Blocks.SAPLING, amountOfSaplings, BlockPlanks.EnumType.OAK.getMetadata()));
                }
            } else {
                saplings.clear();
                saplings.add(Biomes.PLAINS.getBiomeName());
                if (saplings.contains(biome)) {
                    if (biome.matches(Biomes.PLAINS.getBiomeName()))
                        this.putIntoInventory(new ItemStack(Blocks.SAPLING, (amountOfSaplings > 2) ? amountOfSaplings / 2 : 1, BlockPlanks.EnumType.OAK.getMetadata())); // Since its planes, It should start with less.
                } else {
                    saplings.clear();
                    saplings.add(Biomes.COLD_TAIGA.getBiomeName());
                    saplings.add(Biomes.COLD_TAIGA_HILLS.getBiomeName());
                    saplings.add(Biomes.TAIGA.getBiomeName());
                    saplings.add(Biomes.TAIGA_HILLS.getBiomeName());
                    //saplings.add(Biomes.megaTaiga.getBiomeName()); // TODO Correct
                    //saplings.add(Biomes.megaTaigaHills.getBiomeName());
                    if (saplings.contains(biome)) {
                        this.putIntoInventory(new ItemStack(Blocks.SAPLING, amountOfSaplings, BlockPlanks.EnumType.SPRUCE.getMetadata()));
                    } else {
                        saplings.clear();
                        saplings.add(Biomes.BIRCH_FOREST.getBiomeName());
                        saplings.add(Biomes.BIRCH_FOREST_HILLS.getBiomeName());
                        if (saplings.contains(biome)) {
                            this.putIntoInventory(new ItemStack(Blocks.SAPLING, amountOfSaplings, BlockPlanks.EnumType.BIRCH.getMetadata()));
                        } else {
                            saplings.clear();
                            saplings.add(Biomes.JUNGLE.getBiomeName());
                            saplings.add(Biomes.JUNGLE_EDGE.getBiomeName());
                            saplings.add(Biomes.JUNGLE_HILLS.getBiomeName());
                            if (saplings.contains(biome)) {
                                this.putIntoInventory(new ItemStack(Blocks.SAPLING, amountOfSaplings, BlockPlanks.EnumType.JUNGLE.getMetadata()));
                            } else {
                                saplings.clear();
                                saplings.add(Biomes.SAVANNA.getBiomeName());
                                saplings.add(Biomes.SAVANNA_PLATEAU.getBiomeName());
                                if (saplings.contains(biome)) {
                                    this.putIntoInventory(new ItemStack(Blocks.SAPLING, amountOfSaplings, BlockPlanks.EnumType.ACACIA.getMetadata()));
                                } else {
                                    saplings.clear();
                                    saplings.add(Biomes.ROOFED_FOREST.getBiomeName());
                                    if (saplings.contains(biome)) {
                                        this.putIntoInventory(new ItemStack(Blocks.SAPLING, amountOfSaplings, BlockPlanks.EnumType.DARK_OAK.getMetadata()));
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
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(25.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
    }

    @Override
    public boolean getCanSpawnHere() {
        return super.getCanSpawnHere() && (world.getBlockState(new BlockPos(this.posX, this.posY - 1, this.posZ)) != Blocks.LEAVES || world.getBlockState(new BlockPos(this.posX, this.posY - 1, this.posZ)) != Blocks.LEAVES2);
    }

    protected boolean processInteract(EntityPlayer entityPlayer, EnumHand hand) {
        ItemStack itemStack = entityPlayer.getHeldItem(hand);
        if (this.isItemValidForEntity(itemStack.getItem()) && this.isAnySpaceForItemPickup(itemStack)) {
            putIntoInventory(itemStack);
            return true;
        }

        if (ConfigValues.isDebugSettingsEnabled) {
            // Test / Debug Code Following
            if (itemStack.getItem() == ModItems.itemDebug) {
                this.setHealth(0);
                this.playSound(this.getDeathSound(), this.getSoundVolume(), this.getSoundPitch());
                return true;
            } else if (itemStack.getItem() == Items.ARROW) {
                String[] printQueue = new String[this.inventorySize];
                for (int slot = 0; slot < inventory.length; slot++) {
                    if (inventory[slot] != null)
                        printQueue[slot] = (inventory[slot].getItem().getItemStackDisplayName(inventory[slot]) + "x" + inventory[slot].getCount());
                    else printQueue[slot] = (slot + " is null");
                }
                if (!world.isRemote)
                    entityPlayer.sendMessage(new TextComponentString(Arrays.toString(printQueue)));
                LogHelper.info(Arrays.toString(printQueue));
                return true;
            } else if (itemStack.getItem() == Items.BLAZE_ROD) {
                if (!world.isRemote) entityAIPlantSapling.startExecuting();
                return true;
            } else if (itemStack.getItem() == Items.DIAMOND_AXE) {
                for (int slot = 0; slot < inventory.length; slot++) inventory[slot] = null;
                this.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
                return true;
            } else if (itemStack.getItem() == Items.WOODEN_AXE) {
                Boolean alreadyChanged = false;
                for (int slot = 0; slot < inventory.length; slot++) {
                    if (inventory[slot] != null) {
                        if (alreadyChanged) inventory[slot] = null;
                        else {
                            inventory[slot].shrink(1);
                            if (inventory[slot].getCount() == 0) inventory[slot] = null;
                            alreadyChanged = true;
                        }
                    }
                }
                this.setHeldItem(EnumHand.MAIN_HAND, this.getRandomSlot());
                return true;
            } else if (itemStack.getItem() == Items.PAPER) {
                String[] printQueue = new String[this.inventorySize];
                for (int slot = 0; slot < inventory.length; slot++) {
                    if (inventory[slot] != null)
                        printQueue[slot] = (inventory[slot].getItem().getItemStackDisplayName(inventory[slot]) + "x" + inventory[slot].getCount());
                    else printQueue[slot] = (slot + " is null");
                }
                if (!world.isRemote) {
                    entityPlayer.sendMessage(new TextComponentString("Health: " + this.getHealth()));
                    entityPlayer.sendMessage(new TextComponentString("Last Placed: " + entityAIPlantSapling.getTimeSinceLastPlacement() + '/' + ConfigValues.fendinainMob_maxTimeToWaitToPlant + " (Max)"));
                    LogHelper.info("Health: " + this.getHealth());
                    LogHelper.info("Last Placed: " + entityAIPlantSapling.getTimeSinceLastPlacement() + '/' + ConfigValues.fendinainMob_maxTimeToWaitToPlant + " (Max)");
                    for (Object object : this.getActivePotionEffects()) {
                        PotionEffect potionEffect = (PotionEffect) object;
                        LogHelper.info("Potion: " + potionEffect.getEffectName() + " - " + potionEffect.getAmplifier() + " - " + potionEffect.getDuration());
                        entityPlayer.sendMessage(new TextComponentString("Potion: " + potionEffect.getEffectName() + " - " + potionEffect.getAmplifier() + " - " + potionEffect.getDuration()));
                    }
                    entityPlayer.sendMessage(new TextComponentString("Inventory: " + Arrays.toString(printQueue)));
                    LogHelper.info("Inventory: " + Arrays.toString(printQueue));
                    entityPlayer.sendMessage(new TextComponentString("Percent Full: " + this.getPercentageOfInventoryFull()));
                    LogHelper.info("Item to Place: " + getItemToPlace().getItem().getUnlocalizedName());
                    entityPlayer.sendMessage(new TextComponentString("Item to Place: " + this.getItemToPlace().getItem().getUnlocalizedName()));
                    LogHelper.info("Percent Full: " + this.getPercentageOfInventoryFull());
                    int timesKilled;
                    if (entityPlayer.getEntityData().hasKey("fendirainMobOne"))
                        timesKilled = entityPlayer.getEntityData().getInteger("fendirainMobOne");
                    else timesKilled = 0;
                    entityPlayer.sendMessage(new TextComponentString("Killed by: \"" + entityPlayer.getName() + "\" " + timesKilled + " time(s)."));
                    LogHelper.info("Killed by: \"" + entityPlayer.getName() + "\" " + timesKilled + " time(s).");
                }
                return true;
            }  // End Test / Debug Code
        }
        return false;
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        if (damageSource.getEntity() != null && damageSource.getEntity() instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer) damageSource.getEntity();
            if (!entityPlayer.capabilities.isCreativeMode || entityPlayer.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemHoe) {
                NBTTagCompound nbtTagCompound = entityPlayer.getEntityData();
                // Named as "fendirainMobOne" for simplicity in the future if I decide to change the mobs name.
                if (nbtTagCompound.hasKey("fendirainMobOne"))
                    nbtTagCompound.setInteger("fendirainMobOne", nbtTagCompound.getInteger("fendirainMobOne") + 1);
                else nbtTagCompound.setInteger("fendirainMobOne", 1);
            }
        }

    }

    public void putIntoInventory(ItemStack itemStack) {
        if ((itemStack != null && itemStack.getCount() > 0) && (itemStack.getItem() instanceof ItemBlock && ModCompatibility.saplings.contains(itemStack.getItem()))) {
            for (int slot = 0; slot < inventory.length; slot++) {
                if (inventory[slot] == null) {
                    int amountToAdd;
                    if (this.getInventoryStackLimit() < itemStack.getCount())
                        amountToAdd = this.getInventoryStackLimit();
                    else amountToAdd = itemStack.getCount();
                    inventory[slot] = itemStack.copy();
                    inventory[slot].setCount(amountToAdd);
                    itemStack.shrink(amountToAdd);
                } else if (inventory[slot].getUnlocalizedName().matches(itemStack.getUnlocalizedName())) {
                    int freeSpace = this.getInventoryStackLimit() - this.inventory[slot].getCount(), amountToAdd;
                    if (freeSpace < itemStack.getCount()) amountToAdd = freeSpace;
                    else amountToAdd = itemStack.getCount();
                    this.inventory[slot].grow(amountToAdd);
                    itemStack.shrink(amountToAdd);
                }
                if (itemStack.getCount() == 0) break;
            }
            if (this.getHeldItem(EnumHand.MAIN_HAND).isEmpty())
                this.setHeldItem(EnumHand.MAIN_HAND, this.getRandomSlot());
        }
    }

    @Override
    public void dropFewItems(boolean wasHitRecently, int lootingLevel) {
        for (ItemStack itemStack : inventory)
            if (itemStack != null) this.entityDropItem(itemStack, 1F);
    }

    // TODO Fix
   /* @Override
    public boolean allowLeashing() {
        return false;
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
    }*/

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
    public boolean isEmpty() {
        for (ItemStack anInventory : inventory) {
            if (anInventory != null) return false;
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.inventory[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        ItemStack itemstack;
        if (this.inventory[slot] != null) {
            if (this.inventory[slot].getCount() <= amount) {
                itemstack = this.inventory[slot];
                this.inventory[slot] = null;
                return itemstack;
            } else {
                itemstack = this.inventory[slot].splitStack(amount);
                if (this.inventory[slot].getCount() == 0) this.inventory[slot] = null;
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
        if (item.getCount() > this.getInventoryStackLimit())
            item.setCount(this.getInventoryStackLimit());
    }

    @Override
    public int getInventoryStackLimit() {
        return maxStackSize;
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
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
        return itemStack.getItem() instanceof ItemBlock && ModCompatibility.saplings.contains(itemStack.getItem());
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
            if (j >= 0 && j < this.inventory.length)
                this.inventory[j] = new ItemStack(nbtTagCompound1);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
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
        return nbtTagCompound;
    }

    public ItemStack getItemToPlace() {
        return this.getHeldItem(EnumHand.MAIN_HAND);
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
                if (!contained) items.add(item);
            }
        }
        if (!items.isEmpty()) return items.get(rand.nextInt(items.size()));
        else return ItemStack.EMPTY;
    }

    public boolean isAnySpaceForItemPickup(ItemStack item) {
        if (item != null) {
            for (ItemStack itemStack : inventory)
                if (itemStack == null || itemStack.getUnlocalizedName().matches(item.getUnlocalizedName()) && itemStack.getCount() != this.getInventoryStackLimit())
                    return true;
        }
        return false;
    }

    public boolean isItemToPlace() {
        for (ItemStack itemStack : inventory)
            if (itemStack != null) return true;
        return false;
    }

    public boolean isItemValidForEntity(Item item) {
        return item != null && item instanceof ItemBlock && ModCompatibility.saplings.contains(item);
    }

    public void removeItemFromInventory(ItemStack itemStack, int amount, boolean resetCurrentSapling) {
        for (int slot = inventory.length - 1; slot >= 0; slot--) {
            if (inventory[slot] != null && inventory[slot].getUnlocalizedName().matches(itemStack.getUnlocalizedName())) {
                if (inventory[slot].getCount() >= amount) {
                    inventory[slot].shrink(amount);
                    amount = 0;
                    if (inventory[slot].getCount() <= 0) inventory[slot] = null;
                } else {
                    amount -= inventory[slot].getCount();
                    inventory[slot] = null;
                }
                if (resetCurrentSapling) this.setHeldItem(EnumHand.MAIN_HAND, this.getRandomSlot());
                if (amount == 0) break;
            }
        }
    }

    public int getPercentageOfInventoryFull() {
        int maxSize = this.maxStackSize * this.inventorySize, inventoryAmount = 0;
        boolean slotNull = false;
        for (ItemStack itemStack : inventory) {
            if (itemStack != null) inventoryAmount += itemStack.getCount();
            else slotNull = true;
        }
        if (inventoryAmount > 0) {
            int result = (inventoryAmount * 100) / maxSize;
            if (!slotNull && result < 50) result = 50;
            return result;
        } else return 0;
    }
}
