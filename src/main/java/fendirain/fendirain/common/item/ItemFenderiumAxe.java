package fendirain.fendirain.common.item;

import fendirain.fendirain.creativetab.CreativeTabFendirain;
import fendirain.fendirain.init.ModItems;
import fendirain.fendirain.reference.Reference;
import fendirain.fendirain.utility.helper.FullBlock;
import fendirain.fendirain.utility.helper.LogHelper;
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
        super(ModItems.Fendi);
        this.setUnlocalizedName("itemFenderiumAxe");
        this.setCreativeTab(CreativeTabFendirain.FENDIRAIN_TAB);
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayerIn, World worldIn, BlockPos blockPos, EnumFacing side, float hitX, float hitY, float hitZ) {
        World world = entityPlayerIn.getEntityWorld();
        if (!world.isRemote && itemStack.getMaxDamage() - itemStack.getItemDamage() > 1) {
            FullBlock fullBlock = new FullBlock(world.getBlockState(blockPos).getBlock(), blockPos, world.getBlockState(blockPos).getBlock().getDamageValue(world, blockPos));
            if (fullBlock.getBlock() instanceof BlockLog) {
                FullBlock treeLeaf = TreeChecker.isTree(world, fullBlock.getBlockPos());
                if (treeLeaf != null) {
                    treeChopper = new TreeChopper(entityPlayerIn, fullBlock, treeLeaf);
                    int itemDamage = treeChopper.breakAllBlocks((itemStack.getMaxDamage() - itemStack.getItemDamage()) / 4);
                    if (itemDamage > 0) itemStack.damageItem(itemDamage * 4, entityPlayerIn);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onUsingTick(ItemStack itemStack, EntityPlayer entityPlayerIn, int count) {
        if (count >= 100) {
            if (treeChopper != null) {
                int itemDamage = treeChopper.breakAllBlocks((itemStack.getMaxDamage() - itemStack.getItemDamage()) / 4);
                if (itemDamage > 0) itemStack.damageItem(itemDamage * 4, entityPlayerIn);
            }
        } else if (count % 10 == 0) LogHelper.info(count);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 100;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemStack, BlockPos blockPos, EntityPlayer entityPlayer) {
        World world = entityPlayer.getEntityWorld();
        if (!world.isRemote && itemStack.getMaxDamage() - itemStack.getItemDamage() > 1) {
            FullBlock fullBlock = new FullBlock(world.getBlockState(blockPos).getBlock(), blockPos, world.getBlockState(blockPos).getBlock().getDamageValue(world, blockPos));
            if (fullBlock.getBlock() instanceof BlockLog) {
                if (treeChopper != null) LogHelper.info(treeChopper.isBlockContainedInTree(fullBlock));
                if (treeChopper == null || !treeChopper.isBlockContainedInTree(fullBlock)) {
                    FullBlock treeLeaf = TreeChecker.isTree(world, fullBlock.getBlockPos());
                    if (treeLeaf != null) {
                        treeChopper = new TreeChopper(entityPlayer, fullBlock, treeLeaf);
                        treeChopper.breakFurthestBlock();
                    } else return false;
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

