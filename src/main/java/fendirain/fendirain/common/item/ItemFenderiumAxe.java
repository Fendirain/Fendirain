package fendirain.fendirain.common.item;

import fendirain.fendirain.creativetab.CreativeTabFendirain;
import fendirain.fendirain.init.ModItems;
import fendirain.fendirain.reference.Reference;
import fendirain.fendirain.utility.helper.FullBlock;
import fendirain.fendirain.utility.helper.LogHelper;
import fendirain.fendirain.utility.tools.TreeChecker;
import fendirain.fendirain.utility.tools.TreeChopper;
import net.minecraft.block.BlockLog;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
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
    public static final String[] axePullIconNameArray = new String[]{"Charging_1", "Charging_2", "Charging_3"};
    private TreeChopper treeChopper = null;
    private int count = 0;

    public ItemFenderiumAxe() {
        super(ModItems.fendi);
        this.setUnlocalizedName("itemFenderiumAxe");
        this.setCreativeTab(CreativeTabFendirain.FENDIRAIN_TAB);
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayerIn, World worldIn, BlockPos blockPos, EnumFacing side, float hitX, float hitY, float hitZ) {
        World world = entityPlayerIn.getEntityWorld();
        if (itemStack.getMaxDamage() - itemStack.getItemDamage() > 4) {
            FullBlock fullBlock = new FullBlock(world.getBlockState(blockPos).getBlock(), blockPos, world.getBlockState(blockPos).getBlock().getDamageValue(world, blockPos));
            if (fullBlock.getBlock() instanceof BlockLog) {
                if (treeChopper != null && treeChopper.isBlockContainedInTree(fullBlock)) {
                    treeChopper.setMainBlock(fullBlock);
                    entityPlayerIn.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
                    return true;
                } else {
                    FullBlock treeLeaf = TreeChecker.isTree(world, fullBlock.getBlockPos());
                    if (treeLeaf != null) {
                        if (!world.isRemote) treeChopper = new TreeChopper(entityPlayerIn, fullBlock, treeLeaf, true);
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
        //LogHelper.info(this.getMaxItemUseDuration(itemStack) - count);
        /*if (!entityPlayerIn.worldObj.isRemote && treeChopper != null) {
            MovingObjectPosition movingObjectPosition = entityPlayerIn.rayTrace(2, 1);
            LogHelper.info(movingObjectPosition.hitVec.distanceTo(new Vec3(treeChopper.getMainBlock().getBlockPos().getX(), treeChopper.getMainBlock().getBlockPos().getY(), treeChopper.getMainBlock().getBlockPos().getZ())));
            int timeUsed = this.getMaxItemUseDuration(itemStack) - count;
            if (timeUsed > 0) {
               *//* if (treeChopper.getNumberOfLogs() > timeUsed / 2) LogHelper.info(2 + " || " + (double) (timeUsed / 2) / (double) treeChopper.getNumberOfLogs());
                LogHelper.info(3 + " || " + treeChopper.getNumberOfLogs() + " || " + timeUsed / 2);*//*
            }
        } else if (treeChopper == null && entityPlayerIn.getHeldItem() == itemStack) entityPlayerIn.clearItemInUse();*/
        /*if (!entityPlayerIn.worldObj.isRemote) {
            LogHelper.info("Ran2 || " + count);
            if (count == 1) {
                if (treeChopper != null) {
                    int itemDamage = treeChopper.breakAllBlocks((itemStack.getMaxDamage() - itemStack.getItemDamage()) / 4);
                    if (itemDamage > 0) itemStack.damageItem(itemDamage * 4, entityPlayerIn);
                    entityPlayerIn.clearItemInUse();
                }
            }
        }*/
    }

    /*@Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        //treeChopper = null;
        return stack;
    }*/

    @Override
    public void onPlayerStoppedUsing(ItemStack itemStack, World worldIn, EntityPlayer entityPlayerIn, int timeLeft) {
        //LogHelper.info("Ran2 || " + (this.getMaxItemUseDuration(itemStack) - timeLeft) / 2);
        if (!worldIn.isRemote) {
            int maxToBreak = ((this.getMaxItemUseDuration(itemStack) - timeLeft) / 2 < (itemStack.getMaxDamage() - itemStack.getItemDamage()) / 4) ? (this.getMaxItemUseDuration(itemStack) - timeLeft) / 2 : (itemStack.getMaxDamage() - itemStack.getItemDamage()) / 4;
            int itemDamage = treeChopper.breakAllBlocks(maxToBreak);
            if (itemDamage > 0) itemStack.damageItem(itemDamage * 6, entityPlayerIn);
        }
        entityPlayerIn.clearItemInUse();
        if (treeChopper.isFinished()) treeChopper = null;
        count = 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelResourceLocation getModel(ItemStack itemStack, EntityPlayer player, int useRemaining) {
        String name = GameData.getItemRegistry().getNameForObject(this).toString();

        if (treeChopper != null && count > 0) {
            double percentageCharged = (double) ((this.getMaxItemUseDuration(itemStack) - count) / 2) / (double) treeChopper.getNumberOfLogs();
            LogHelper.info(name + " || " + percentageCharged + " || " + (this.getMaxItemUseDuration(itemStack) - count));
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
            FullBlock fullBlock = new FullBlock(world.getBlockState(blockPos).getBlock(), blockPos, world.getBlockState(blockPos).getBlock().getDamageValue(world, blockPos));
            if (fullBlock.getBlock() instanceof BlockLog) {
                if (treeChopper == null || !treeChopper.isBlockContainedInTree(fullBlock)) {
                    FullBlock treeLeaf = TreeChecker.isTree(world, fullBlock.getBlockPos());
                    if (treeLeaf != null) {
                        treeChopper = new TreeChopper(entityPlayerIn, fullBlock, treeLeaf, true);
                        treeChopper.breakFurthestBlock();
                        itemStack.damageItem(1, entityPlayerIn);
                    } else {
                        treeChopper = new TreeChopper(entityPlayerIn, fullBlock, null, false);
                        treeChopper.breakFurthestBlock();
                        itemStack.damageItem(1, entityPlayerIn);
                    }
                } else {
                    if (!treeChopper.getMainBlock().isSameBlock(fullBlock)) treeChopper.setMainBlock(fullBlock);
                    treeChopper.breakFurthestBlock();
                    itemStack.damageItem(1, entityPlayerIn);
                }
                if (treeChopper.isFinished()) treeChopper = null;
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

    protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }
}

