package fendirain.fendirain.common.item;

import fendirain.fendirain.reference.ConfigValues;
import fendirain.fendirain.utility.helper.LogHelper;
import fendirain.fendirain.utility.tools.TreeChecker;
import fendirain.fendirain.utility.tools.TreeChopper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class ItemDebug extends ItemFendirain {
    Set<BlockPos> blockPosSet = new HashSet<>();

    public ItemDebug() {
        super("itemDebug");
        this.setMaxStackSize(1);
        this.setUnlocalizedName("itemDebug");
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos blockPos, EnumHand enumHand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            if (worldIn.getBlockState(blockPos).getBlock().isWood(worldIn, blockPos)) {
                BlockPos blockPos1 = TreeChecker.isTree(worldIn, blockPos);
                ItemStack itemStack = playerIn.getHeldItem(enumHand);
                if (playerIn.isSneaking()) {
                    if (blockPos1 != null) {
                        TreeChopper treeChopper = new TreeChopper(playerIn, blockPos, blockPos1, true, itemStack);
                        treeChopper.breakAllBlocks(Integer.MAX_VALUE);
                    }
                } else {
                    String response = "Block at \"" + blockPos.toString() + "\" is " + ((blockPos1 != null) ? "contained in a tree" : "not contained in a tree") + ".";
                    playerIn.sendMessage(new TextComponentString(response));
                    LogHelper.info(response);
                    if (blockPos1 != null)
                        this.blockPosSet = new TreeChopper(playerIn, blockPos, blockPos1, true, itemStack).getCurrentTree();
                }
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.PASS;
    }

    public Set<BlockPos> getBlocks() {
        return blockPosSet;
    }

    public void setBlocks(Set<BlockPos> blockPosSet) {
        this.blockPosSet = blockPosSet;
    }

    @Override
    protected boolean shouldRegister() {
        return ConfigValues.isDebugSettingsEnabled;
    }
}
