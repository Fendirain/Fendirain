package fendirain.fendirain.common.entity.mob.EntityFenderium;

import fendirain.fendirain.common.entity.mob.EntityFenderium.AI.EntityAIChopTrees;
import fendirain.fendirain.common.entity.mob.EntityFenderium.AI.EntityAIThrowWoodAtPlayer;
import fendirain.fendirain.init.ModItems;
import fendirain.fendirain.reference.ConfigValues;
import fendirain.fendirain.utility.helper.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
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

import java.util.Arrays;

public class EntityFenderiumMob extends EntityCreature implements IInventory {
    private final int inventorySize = 6;
    private final int maxStackSize = 28;
    private final int breakSpeed = ConfigValues.fenderiumMob_breakSpeed;
    private final EntityAIChopTrees entityAIChopTrees;
    private ItemStack[] inventory = new ItemStack[inventorySize];
    private boolean isChopping = false;
    private BlockPos currentlyBreakingPos = null;
    private int currentlyBreakingProgress = -1;

    public EntityFenderiumMob(World world) {
        super(world);
        this.setSize(.39F, .99F);
        entityAIChopTrees = new EntityAIChopTrees(this, rand, 1.0F, ConfigValues.fenderiumMob_waitPerTreeOrLog, ConfigValues.fenderiumMob_timePerBreak * 20);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, entityAIChopTrees);
        this.tasks.addTask(2, new EntityAIWander(this, 1.0F));
        this.tasks.addTask(3, new EntityAIThrowWoodAtPlayer(this, rand, 1.0F));
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficultyInstance, IEntityLivingData iEntityLivingData) {
        this.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(ModItems.itemFenderiumAxe));
        return iEntityLivingData;
    }


    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!entityAIChopTrees.isAlreadyExecuting() && !(entityAIChopTrees.getTimeToWaitUntilNextRun() <= 0))
            this.entityAIChopTrees.setTimeToWaitUntilNextRun(this.entityAIChopTrees.getTimeToWaitUntilNextRun() - 1);
    }

    protected boolean processInteract(EntityPlayer entityPlayer, EnumHand hand) {
        ItemStack itemStack = entityPlayer.getHeldItem(hand);
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
                if (!world.isRemote) {
                    entityPlayer.sendMessage(new TextComponentString(Arrays.toString(printQueue)));
                    LogHelper.info(Arrays.toString(printQueue));
                }
                return true;
            } else if (itemStack.getItem() == Items.BLAZE_ROD) {
                if (entityAIChopTrees.isAlreadyExecuting())
                    this.addPotionEffect(new PotionEffect(MobEffects.HASTE, 99999, 9));
                else entityAIChopTrees.setTimeToWaitUntilNextRun(0);
                return true;
            } else if (itemStack.getItem() == Items.DIAMOND_AXE) {
                for (int slot = 0; slot < inventory.length; slot++) inventory[slot] = null;
                this.setHeldItem(EnumHand.MAIN_HAND, null);
                return true;
            } else if (itemStack.getItem() == Items.NETHER_STAR) {
                for (int slot = 0; slot < inventory.length; slot++)
                    inventory[slot] = new ItemStack(Item.getItemFromBlock(Blocks.LOG), this.getInventoryStackLimit());
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
                    entityPlayer.sendMessage(new TextComponentString("Wait Time: " + entityAIChopTrees.getTimeToWaitUntilNextRun()));
                    entityPlayer.sendMessage(new TextComponentString("Currently Chopping: " + entityAIChopTrees.isAlreadyExecuting()));
                    LogHelper.info("Health: " + this.getHealth());
                    LogHelper.info("Wait Time: " + entityAIChopTrees.getTimeToWaitUntilNextRun());
                    LogHelper.info("Currently Chopping: " + entityAIChopTrees.isAlreadyExecuting());
                    for (PotionEffect potionEffect : this.getActivePotionEffects()) {
                        LogHelper.info("Potion: " + potionEffect.getEffectName() + " - " + potionEffect.getAmplifier() + " - " + potionEffect.getDuration());
                        entityPlayer.sendMessage(new TextComponentString("Potion: " + potionEffect.getEffectName() + " - " + potionEffect.getAmplifier() + " - " + potionEffect.getDuration()));
                    }
                    entityPlayer.sendMessage(new TextComponentString("Inventory: " + Arrays.toString(printQueue)));
                    LogHelper.info("Inventory: " + Arrays.toString(printQueue));
                    entityPlayer.sendMessage(new TextComponentString("Percent Full: " + this.getPercentageOfInventoryFull()));
                    LogHelper.info("Percent Full: " + this.getPercentageOfInventoryFull());
                    int timesKilled;
                    if (entityPlayer.getEntityData().hasKey("fendirainMobTwo"))
                        timesKilled = entityPlayer.getEntityData().getInteger("fendirainMobTwo");
                    else timesKilled = 0;
                    entityPlayer.sendMessage(new TextComponentString("Killed by: \"" + entityPlayer.getName() + "\" " + timesKilled + " time(s)."));
                    LogHelper.info("Killed by: \"" + entityPlayer.getName() + "\" " + timesKilled + " time(s).");
                }
                return true;
            } // End Test / Debug Code
        }
        return false;
    }

    public int getBreakSpeed() {
        if (this.isPotionActive(MobEffects.HASTE))
            return this.breakSpeed + (this.getActivePotionEffect(MobEffects.HASTE).getAmplifier() + 1);
        else return this.breakSpeed;
    }

    @Override
    public void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(25.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
    }

    @Override
    public boolean getCanSpawnHere() {
        Block block = world.getBlockState(new BlockPos(this).down()).getBlock();
        return super.getCanSpawnHere() && (block != Blocks.LEAVES || block != Blocks.LEAVES2);
    }

    // TODO Fix
    /*@Override
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
    public void dropFewItems(boolean wasHitRecently, int lootingLevel) { //TODO Might Change this
        for (ItemStack itemStack : inventory) if (itemStack != null) this.entityDropItem(itemStack, 1.0F);
    }

    public void putIntoInventory(ItemStack itemStack) {
        if ((itemStack != null && itemStack.getCount() > 0) && (itemStack.getItem() instanceof ItemBlock && Block.getBlockFromItem(itemStack.getItem()) instanceof BlockLog)) {
            for (int slot = 0; slot < inventory.length; slot++) {
                if (inventory[slot] == null) {
                    int amountToAdd;
                    if (this.getInventoryStackLimit() < itemStack.getCount())
                        amountToAdd = this.getInventoryStackLimit();
                    else amountToAdd = itemStack.getCount();
                    inventory[slot] = itemStack.copy();
                    inventory[slot].grow(amountToAdd);
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
        }
    }

    public ItemStack[] getEntityInventory() {
        return inventory;
    }

    public boolean isAnySpaceForItemPickup(ItemStack itemToPickup) {
        if (itemToPickup != null) {
            for (ItemStack itemStack : inventory)
                if (itemStack == null || itemStack.getUnlocalizedName().matches(itemToPickup.getUnlocalizedName()) && itemStack.getCount() != this.getInventoryStackLimit())
                    return true;
        }
        return false;
    }

    public boolean isValidForPickup(ItemStack itemStack) {
        return itemStack.getItem() instanceof ItemBlock && (Block.getBlockFromItem(itemStack.getItem()) == Blocks.LOG || Block.getBlockFromItem(itemStack.getItem()) == Blocks.LOG2);
    }

    public void removeItemFromInventory(ItemStack itemStack, int amount) {
        for (int slot = inventory.length - 1; slot >= 0; slot--) {
            if (inventory[slot] != null && inventory[slot].getUnlocalizedName().matches(itemStack.getUnlocalizedName())) {
                if (inventory[slot].getCount() >= amount) {
                    inventory[slot].shrink(amount);
                    amount = 0;
                    if (inventory[slot].getCount() <= 0) {
                        inventory[slot] = null;
                    }
                } else {
                    amount -= inventory[slot].getCount();
                    inventory[slot] = null;
                }
                if (amount == 0) {
                    break;
                }
            }
        }
    }

    public void clearInventory() {
        inventory = new ItemStack[inventorySize];
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        if (entityAIChopTrees.isAlreadyExecuting()) this.entityAIChopTrees.resetTask();
        if (damageSource.getEntity() != null && damageSource.getEntity() instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer) damageSource.getEntity();
            if (!entityPlayer.capabilities.isCreativeMode || entityPlayer.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemHoe) {
                NBTTagCompound nbtTagCompound = entityPlayer.getEntityData();
                if (nbtTagCompound.hasKey("fendirainMobTwo"))
                    nbtTagCompound.setInteger("fendirainMobTwo", nbtTagCompound.getInteger("fendirainMobTwo") + 1);
                else nbtTagCompound.setInteger("fendirainMobTwo", 1);
            }
        }
    }

    @Override
    public int getSizeInventory() {
        return inventorySize;
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
        return inventory[slot];
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
        return itemStack.getItem() instanceof ItemBlock && (Block.getBlockFromItem(itemStack.getItem()) == Blocks.LOG || Block.getBlockFromItem(itemStack.getItem()) == Blocks.LOG2);
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

    public boolean isItemValidForBreaking(World world, BlockPos blockPos, Block block) {
        return block.isWood(world, blockPos);
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
        entityAIChopTrees.readFromNBT(nbtTagCompound.getCompoundTag("entityAIChopTrees"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);
        NBTTagList nbtTagList = new NBTTagList();
        for (int i = 0; i < this.inventory.length; ++i) {
            if (this.inventory[i] != null) {
                NBTTagCompound nbtTagCompound1 = new NBTTagCompound();
                nbtTagCompound1.setByte("Slot", (byte) i);
                this.inventory[i].writeToNBT(nbtTagCompound1);
                nbtTagList.appendTag(nbtTagCompound1);
            }
        }
        nbtTagCompound.setTag("Items", nbtTagList);
        nbtTagCompound.setTag("entityAIChopTrees", entityAIChopTrees.writeToNBT());
        return nbtTagCompound;
    }

    public int getMaxRange() {
        return 12;
    }

    public boolean isCurrentlyChopping() {
        return this.isChopping;
    }

    public void setIsChopping(boolean chopping) {
        isChopping = chopping;
    }

    public void setCurrentlyBreakingProgress(int currentlyBreakingProgress) {
        this.currentlyBreakingProgress = currentlyBreakingProgress;
    }

    public BlockPos getCurrentlyBreakingPos() {
        return this.currentlyBreakingPos;
    }

    public void setCurrentlyBreakingPos(BlockPos blockPos) {
        this.currentlyBreakingPos = blockPos;
    }

    public int getWholeTreeProgress() {
        return this.currentlyBreakingProgress;
    }
}
