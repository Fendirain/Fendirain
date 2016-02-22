package fendirain.fendirain.common.item;

import fendirain.fendirain.creativetab.CreativeTabFendirain;
import fendirain.fendirain.init.ModItems;
import fendirain.fendirain.reference.Reference;
import fendirain.fendirain.utility.helper.FullBlock;
import fendirain.fendirain.utility.tools.TreeChecker;
import fendirain.fendirain.utility.tools.TreeChopper;
import net.minecraft.block.BlockLog;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemFenderiumAxe extends ItemAxe {
    private TreeChopper treeChopper = null;

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
        if (treeChopper != null) {
            int maxToBreak = ((this.getMaxItemUseDuration(itemStack) - timeLeft) / 2 < (itemStack.getMaxDamage() - itemStack.getItemDamage()) / 4) ? (this.getMaxItemUseDuration(itemStack) - timeLeft) / 2 : (itemStack.getMaxDamage() - itemStack.getItemDamage()) / 4;
            int itemDamage = treeChopper.breakAllBlocks(maxToBreak);
            if (itemDamage > 0) itemStack.damageItem(itemDamage * 4, entityPlayerIn);
            entityPlayerIn.clearItemInUse();
            if (treeChopper.isFinished()) treeChopper = null;
        }
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
        if (!world.isRemote && itemStack.getMaxDamage() - itemStack.getItemDamage() > 1) {
            FullBlock fullBlock = new FullBlock(world.getBlockState(blockPos).getBlock(), blockPos, world.getBlockState(blockPos).getBlock().getDamageValue(world, blockPos));
            if (fullBlock.getBlock() instanceof BlockLog) {
                if (treeChopper == null || !treeChopper.isBlockContainedInTree(fullBlock)) {
                    FullBlock treeLeaf = TreeChecker.isTree(world, fullBlock.getBlockPos());
                    if (treeLeaf != null) {
                        treeChopper = new TreeChopper(entityPlayerIn, fullBlock, treeLeaf, true);
                        treeChopper.breakFurthestBlock();
                    } else {
                        treeChopper = new TreeChopper(entityPlayerIn, fullBlock, null, false);
                        treeChopper.breakFurthestBlock();
                    }
                } else {
                    if (!treeChopper.getMainBlock().isSameBlock(fullBlock)) treeChopper.setMainBlock(fullBlock);
                    treeChopper.breakFurthestBlock();
                }
                if (treeChopper.isFinished()) treeChopper = null;
            } else return false;
        }
        return true;
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

