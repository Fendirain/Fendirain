package fendirain.fendirain.common.entity.mob.EntityFenderium;

import fendirain.fendirain.common.entity.mob.EntityFenderium.AI.EntityAIChopTrees;
import fendirain.fendirain.reference.ConfigValues;
import fendirain.fendirain.reference.Reference;
import fendirain.fendirain.utility.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.Arrays;

public class EntityFenderiumMob extends EntityCreature implements IInventory {
    private final String name = "Fendinain";
    private final int inventorySize = 6, maxStackSize = 12, range = 12;
    private final EntityAIChopTrees entityAIChopTrees = new EntityAIChopTrees(this, 1.0F, 16);
    private ItemStack[] inventory = new ItemStack[inventorySize];

    public EntityFenderiumMob(World world) {
        super(world);
        this.setSize(.39F, .85F);
        this.getNavigator().setCanSwim(true);
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, entityAIChopTrees);
        this.tasks.addTask(2, new EntityAIWander(this, 1.0F));
        this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, false));
    }

    @Override
    public boolean interact(EntityPlayer entityPlayer) {
        ItemStack itemStack = entityPlayer.inventory.getCurrentItem();
        if (itemStack != null) {
            if (ConfigValues.isDebugSettingsEnabled) {
                // Test / Debug Code Following
                if (itemStack.getItem() == Items.wooden_hoe) {
                    this.kill();
                    this.setDead();
                    if (this.entityAIChopTrees.isAlreadyExecuting()) {
                        this.entityAIChopTrees.resetTask();
                    }
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
                    return true;
                } // End Test / Debug Code
            }
        }
        return false;
    }

    @Override
    public boolean isAIEnabled() {
        return true;
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
    public void dropFewItems(boolean wasHitRecently, int lootingLevel) { //TODO Change this
        for (ItemStack itemStack : inventory) {
            if (itemStack != null) {
                this.entityDropItem(itemStack, 1F);
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
        }
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

    public boolean isValidForPickup(ItemStack itemStack) {
        return itemStack.getItem() instanceof ItemBlock && (Block.getBlockFromItem(itemStack.getItem()) == Blocks.log || Block.getBlockFromItem(itemStack.getItem()) == Blocks.log2);
    }

    public void removeItemFromInventory(ItemStack itemStack, int amount) {
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
                if (amount == 0) {
                    break;
                }
            }
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        if (entityAIChopTrees.isAlreadyExecuting()) {
            this.entityAIChopTrees.resetTask();
        }
    }

    @Override
    public int getSizeInventory() {
        return inventorySize;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory[slot];
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
    public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
        return itemStack.getItem() instanceof ItemBlock && (Block.getBlockFromItem(itemStack.getItem()) == Blocks.log || Block.getBlockFromItem(itemStack.getItem()) == Blocks.log2);
    }

    public boolean isItemValidForBreaking(Block block) {
        return block instanceof BlockLog;
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
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte) i);
                this.inventory[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }
        nbtTagCompound.setTag("Items", nbttaglist);
    }

    public int getMaxRange() {
        return range;
    }
}
