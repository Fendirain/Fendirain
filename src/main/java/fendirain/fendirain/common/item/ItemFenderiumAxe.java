package fendirain.fendirain.common.item;

import fendirain.fendirain.creativetab.CreativeTabFendirain;
import fendirain.fendirain.init.ModItems;
import fendirain.fendirain.network.PacketHandler;
import fendirain.fendirain.network.packets.DestroyItemPacket;
import fendirain.fendirain.reference.Reference;
import fendirain.fendirain.utility.helper.LogHelper;
import fendirain.fendirain.utility.tools.TreeChecker;
import fendirain.fendirain.utility.tools.TreeChopper;
import net.minecraft.block.Block;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemFenderiumAxe extends ItemAxe {
    private final int axeDamagePerBlock = 4;
    private TreeChopper treeChopper = null;
    private int count = 0, lastUsed = 0;

    public ItemFenderiumAxe() {
        super(ModItems.fendi);
        this.setUnlocalizedName("itemFenderiumAxe");
        this.setCreativeTab(CreativeTabFendirain.FENDIRAIN_TAB);
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayerIn, World worldIn, BlockPos blockPos, EnumFacing side, float hitX, float hitY, float hitZ) {
        World world = entityPlayerIn.getEntityWorld();
        if (itemStack.getMaxDamage() - itemStack.getItemDamage() > 4) {
            Block block = world.getBlockState(blockPos).getBlock();
            if (block.isWood(world, blockPos)) {
                if (treeChopper != null && treeChopper.isBlockContainedInTree(blockPos)) {
                    treeChopper.setMainBlockPos(blockPos);
                    entityPlayerIn.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
                    return true;
                } else {
                    BlockPos treeLeaf = TreeChecker.isTree(world, blockPos);
                    if (treeLeaf != null) {
                        if (!world.isRemote)
                            treeChopper = new TreeChopper(entityPlayerIn, blockPos, treeLeaf, true, itemStack);
                        entityPlayerIn.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onUsingTick(ItemStack itemStack, EntityPlayer entityPlayerIn, int count) {
        this.count = count;
        this.lastUsed = 0;
    }


    @Override
    public void onPlayerStoppedUsing(ItemStack itemStack, World worldIn, EntityPlayer entityPlayerIn, int timeLeft) {
        if (!worldIn.isRemote) {
            int maxToBreak = ((this.getMaxItemUseDuration(itemStack) - timeLeft) / 2 < (itemStack.getMaxDamage() - itemStack.getItemDamage()) / axeDamagePerBlock) ? (this.getMaxItemUseDuration(itemStack) - timeLeft) / 2 : (itemStack.getMaxDamage() - itemStack.getItemDamage()) / axeDamagePerBlock;
            treeChopper.breakAllBlocks(maxToBreak);
        }
        if (itemStack.getMaxDamage() <= itemStack.getItemDamage()) {
            PacketHandler.simpleNetworkWrapper.sendTo(new DestroyItemPacket(entityPlayerIn.getCurrentEquippedItem()), (EntityPlayerMP) entityPlayerIn);
            entityPlayerIn.destroyCurrentEquippedItem();
        } else entityPlayerIn.clearItemInUse();
        if (treeChopper.isFinished()) treeChopper = null;
        count = 0;
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (treeChopper != null) { // This is clear treeChopper if the user doesn't use that axe for 2 minutes.
            lastUsed++;
            if (lastUsed > 2400) {
                treeChopper = null;
                LogHelper.info(entityIn.getDisplayName() + " had their FenderiumAxe reset the currently loaded tree (Was 2+ Minutes since last use).");
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelResourceLocation getModel(ItemStack itemStack, EntityPlayer player, int useRemaining) {
        String name = GameData.getItemRegistry().getNameForObject(this).toString();
        if (treeChopper != null && count > 0) {
            double percentageCharged = (double) ((this.getMaxItemUseDuration(itemStack) - count) / 2) / (treeChopper.getNumberOfLogs() <= ((itemStack.getMaxDamage() - itemStack.getItemDamage()) / axeDamagePerBlock) ? (double) treeChopper.getNumberOfLogs() : ((double) (itemStack.getMaxDamage() - itemStack.getItemDamage()) / axeDamagePerBlock));
            if (percentageCharged >= 1)
                return new ModelResourceLocation(name + "_Charging_9", "inventory");
            else if (percentageCharged > .85)
                return new ModelResourceLocation(name + "_Charging_8", "inventory");
            else if (percentageCharged > .75)
                return new ModelResourceLocation(name + "_Charging_7", "inventory");
            else if (percentageCharged > .65)
                return new ModelResourceLocation(name + "_Charging_6", "inventory");
            else if (percentageCharged > .55)
                return new ModelResourceLocation(name + "_Charging_5", "inventory");
            else if (percentageCharged > .45)
                return new ModelResourceLocation(name + "_Charging_4", "inventory");
            else if (percentageCharged > .35)
                return new ModelResourceLocation(name + "_Charging_3", "inventory");
            else if (percentageCharged > .25)
                return new ModelResourceLocation(name + "_Charging_2", "inventory");
            else if (percentageCharged > .15)
                return new ModelResourceLocation(name + "_Charging_1", "inventory");
        }
        return new ModelResourceLocation(name + "_Default", "inventory");
    }


    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemStack, BlockPos blockPos, EntityPlayer entityPlayerIn) {
        World world = entityPlayerIn.getEntityWorld();
        if (!entityPlayerIn.capabilities.isCreativeMode && !world.isRemote && itemStack.getMaxDamage() - itemStack.getItemDamage() > 0) {
            Block block = world.getBlockState(blockPos).getBlock();
            if (block.isWood(world, blockPos)) {
                if (treeChopper == null || !treeChopper.isBlockContainedInTree(blockPos)) {
                    BlockPos treeLeaf = TreeChecker.isTree(world, blockPos);
                    if (treeLeaf != null) {
                        treeChopper = new TreeChopper(entityPlayerIn, blockPos, treeLeaf, true, itemStack);
                        treeChopper.breakFurthestBlock();
                    } else {
                        treeChopper = new TreeChopper(entityPlayerIn, blockPos, null, false, itemStack);
                        treeChopper.breakFurthestBlock();
                    }
                } else {
                    if (treeChopper.getMainBlockPos().toLong() != blockPos.toLong())
                        treeChopper.setMainBlockPos(blockPos);
                    treeChopper.breakFurthestBlock();
                }
                if (treeChopper.isFinished()) treeChopper = null;
                lastUsed = 0;
                if (itemStack.getMaxDamage() <= itemStack.getItemDamage()) {
                    PacketHandler.simpleNetworkWrapper.sendTo(new DestroyItemPacket(entityPlayerIn.getCurrentEquippedItem()), (EntityPlayerMP) entityPlayerIn);
                    entityPlayerIn.destroyCurrentEquippedItem();
                }
                return true;
            } else return false;
        }
        return false;
    }

    @Override
    public String getUnlocalizedName() {
        return String.format("item.%s%s", Reference.MOD_ID + ":", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        return String.format("item.%s%s", Reference.MOD_ID + ":", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

    public int getDamageAmplifier() {
        return this.axeDamagePerBlock;
    }

    protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }
}

