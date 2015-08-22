package fendirain.fendirain.entity.mob;

import fendirain.fendirain.entity.AI.EntityFendinainAI.EntityAICollectSaplings;
import fendirain.fendirain.entity.AI.EntityFendinainAI.EntityAIPlantSapling;
import fendirain.fendirain.reference.ConfigValues;
import fendirain.fendirain.reference.Reference;
import fendirain.fendirain.utility.LogHelper;
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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class EntityFendinainMob extends EntityCreature implements IInventory {

    private final int inventorySize = 6, maxStackSize = 12;
    private ItemStack[] inventory = new ItemStack[inventorySize];
    private EntityAIPlantSapling entityAIPlantSapling = new EntityAIPlantSapling(this, 4800, 12000);
    private boolean firstUpdate;

    public EntityFendinainMob(World world) {
        super(world);
        this.setSize(.39F, .85F);
        firstUpdate = true;
        this.getNavigator().setCanSwim(true);
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 0.5D));
        this.tasks.addTask(2, entityAIPlantSapling);
        this.tasks.addTask(2, new EntityAICollectSaplings(this, 1F));
        this.tasks.addTask(3, new EntityAIWander(this, 1F));
        this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
    }

    @Override
    public void updateAITick() {
        if (firstUpdate) {
            this.addNewSpawnInventory();
            firstUpdate = false;
        }
        this.entityAIPlantSapling.timeSinceLastPlacement++;
    }

    public void addNewSpawnInventory() {
        String biome = this.worldObj.getBiomeGenForCoords((int) this.posX, (int) this.posZ).biomeName;
        // Adds the proper type of saplings for the biome it's spawned in. Done this way for future compatibility with mods. May be changed later.
        if (biome != null) {
            ArrayList<String> saplings = new ArrayList<String>();
            saplings.add(BiomeGenBase.extremeHills.biomeName);
            saplings.add(BiomeGenBase.extremeHillsEdge.biomeName);
            saplings.add(BiomeGenBase.extremeHillsPlus.biomeName);
            saplings.add(BiomeGenBase.forest.biomeName);
            saplings.add(BiomeGenBase.forestHills.biomeName);
            if (saplings.contains(biome)) {
                this.putIntoInventory(new ItemStack(Blocks.sapling, this.maxStackSize, 0));
            } else {
                saplings.clear();
                saplings.add(BiomeGenBase.coldTaiga.biomeName);
                saplings.add(BiomeGenBase.coldTaigaHills.biomeName);
                saplings.add(BiomeGenBase.taiga.biomeName);
                saplings.add(BiomeGenBase.taigaHills.biomeName);
                saplings.add(BiomeGenBase.megaTaiga.biomeName);
                saplings.add(BiomeGenBase.megaTaigaHills.biomeName);
                if (saplings.contains(biome)) {
                    this.putIntoInventory(new ItemStack(Blocks.sapling, this.maxStackSize, 1));
                } else {
                    saplings.clear();
                    saplings.add(BiomeGenBase.birchForest.biomeName);
                    saplings.add(BiomeGenBase.birchForestHills.biomeName);
                    if (saplings.contains(biome)) {
                        this.putIntoInventory(new ItemStack(Blocks.sapling, this.maxStackSize, 2));
                    } else {
                        saplings.clear();
                        saplings.add(BiomeGenBase.jungle.biomeName);
                        saplings.add(BiomeGenBase.jungleEdge.biomeName);
                        saplings.add(BiomeGenBase.jungleHills.biomeName);
                        if (saplings.contains(biome)) {
                            this.putIntoInventory(new ItemStack(Blocks.sapling, this.maxStackSize, 3));
                        } else {
                            saplings.clear();
                            saplings.add(BiomeGenBase.savanna.biomeName);
                            saplings.add(BiomeGenBase.savannaPlateau.biomeName);
                            if (saplings.contains(biome)) {
                                this.putIntoInventory(new ItemStack(Blocks.sapling, this.maxStackSize, 4));
                            } else {
                                saplings.clear();
                                saplings.add(BiomeGenBase.roofedForest.biomeName);
                                if (saplings.contains(biome)) {
                                    this.putIntoInventory(new ItemStack(Blocks.sapling, this.maxStackSize, 5));
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
        return super.getCanSpawnHere() && (worldObj.getBlock((int) this.posX, (int) this.posY - 1, (int) this.posZ) != Blocks.leaves || worldObj.getBlock((int) this.posX, (int) this.posY - 1, (int) this.posZ) != Blocks.leaves2);
    }

    @Override
    public boolean allowLeashing() {
        return false;
    }

    @Override
    public boolean interact(EntityPlayer entityPlayer) {
        ItemStack itemStack = entityPlayer.inventory.getCurrentItem();
        if (itemStack != null) {
            if (this.isValidForPickup(itemStack.getItem()) && this.isAnySpaceForItemPickup(itemStack.getItem())) {
                putIntoInventory(itemStack);
                return true;
            }

            if (ConfigValues.isDebugSettingsEnabled) {
                // Test / Debug Code Following
                if (itemStack.getItem() == Items.wooden_hoe) {
                    this.kill();
                    this.setDead();
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
                    LogHelper.info(Arrays.toString(printQueue));
                    return true;
                } else if (itemStack.getItem() == Items.blaze_rod) {
                /*if (entityAIPlantSapling.shouldExecuteIgnoreMin()) {*/
                    entityAIPlantSapling.startExecuting();
                    return true;
                /*}*/
                } else if (itemStack.getItem() == Items.diamond_axe) {
                    for (int slot = 0; slot < inventory.length; slot++) {
                        inventory[slot] = null;
                    }
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
                    return true;
                } // End Test / Debug Code
            }
        }
        return false;
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
                } else if (Objects.equals(inventory[slot].getUnlocalizedName(), itemStack.getUnlocalizedName())) {
                    int freeSpace = this.getInventoryStackLimit() - this.inventory[slot].stackSize, amountToAdd;
                    if (freeSpace < itemStack.stackSize) {
                        amountToAdd = freeSpace;
                    } else amountToAdd = itemStack.stackSize;
                    this.inventory[slot].stackSize += amountToAdd;
                    itemStack.stackSize -= amountToAdd;
                }
                if (itemStack.stackSize == 0) break;
            }
        }
    }


    @Override
    public boolean isAIEnabled() {
        return true;
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
        return Reference.MOD_ID.toLowerCase() + ":" + "mob.fendirain.hurt";
    }

    @Override
    public String getDeathSound() {
        return Reference.MOD_ID.toLowerCase() + ":" + "mob.fendirain.hurt";
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
    public ItemStack getStackInSlotOnClosing(int slot) {
        if (this.inventory[slot] != null) {
            ItemStack itemStack = this.inventory[slot];
            this.inventory[slot] = null;
            return itemStack;
        } else return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack item) {
        this.inventory[slot] = item;
        if (item != null && item.stackSize > this.getInventoryStackLimit()) {
            item.stackSize = this.getInventoryStackLimit();
        }
    }

    @Override
    public String getInventoryName() {
        return null;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
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
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public boolean isItemValidForSlot(int i1, ItemStack itemStack) {
        return itemStack.getItem() instanceof ItemBlock && Block.getBlockFromItem(itemStack.getItem()) == Blocks.sapling;
    }

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
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte) i);
                this.inventory[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }
        nbtTagCompound.setTag("Items", nbttaglist);
    }

    public ItemStack getRandomSlot() {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        for (ItemStack item : inventory) {
            if (item != null) {
                boolean contained = false;
                for (ItemStack item1 : items) {
                    if (item1 != null && Objects.equals(item1.getUnlocalizedName(), item.getUnlocalizedName())) {
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

    public boolean isAnySpaceForItemPickup(Item item) {
        if (item != null) {
            for (ItemStack itemStack : inventory) {
                if (itemStack == null || Objects.equals(itemStack.getUnlocalizedName(), item.getUnlocalizedName()) && itemStack.stackSize != this.getInventoryStackLimit()) {
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
        return Block.getBlockFromItem(item) == Blocks.sapling;
    }

    public void removeItemFromInventory(ItemStack itemStack, int amount) {
        for (int slot = inventory.length - 1; slot >= 0; slot--) {
            if (inventory[slot] != null && Objects.equals(inventory[slot].getUnlocalizedName(), itemStack.getUnlocalizedName())) {
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
                if (amount == 0) {
                    break;
                }
            }
        }
    }

    /*public boolean canAttackClass(Class useClass)
    {
        return !(this.isPlayerCreated() && EntityPlayer.class.isAssignableFrom(useClass)) && super.canAttackClass(useClass);
    }

    public boolean isPlayerCreated()
    {
        return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
    }

    public void setPlayerCreated(boolean playerCreated)
    {
        byte b0 = this.dataWatcher.getWatchableObjectByte(16);

        if (playerCreated)
        {
            this.dataWatcher.updateObject(16, (byte) (b0 | 1));
        }
        else
        {
            this.dataWatcher.updateObject(16, (byte) (b0 & -2));
        }
    }

    public void writeEntityToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeEntityToNBT(nbtTagCompound);
        nbtTagCompound.setBoolean("PlayerCreated", this.isPlayerCreated());
    }

    public void readEntityFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readEntityFromNBT(nbtTagCompound);
        this.setPlayerCreated(nbtTagCompound.getBoolean("PlayerCreated"));
    }*/
}
