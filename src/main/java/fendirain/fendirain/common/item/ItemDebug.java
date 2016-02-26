package fendirain.fendirain.common.item;

import fendirain.fendirain.utility.helper.LogHelper;
import fendirain.fendirain.utility.tools.TreeChecker;
import fendirain.fendirain.utility.tools.TreeChopper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.LinkedHashSet;
import java.util.Set;

public class ItemDebug extends ItemFendirain {
    TreeChopper treeChopper = null;

    public ItemDebug() {
        super();
        this.setMaxStackSize(1);
        this.setUnlocalizedName("itemDebug");
    }

    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos blockPos, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (worldIn.getBlockState(blockPos).getBlock().isWood(worldIn, blockPos)) {
            BlockPos blockPos1 = TreeChecker.isTree(worldIn, blockPos);
            if (playerIn.isSneaking()) {
                if (blockPos1 != null && !worldIn.isRemote) {
                    TreeChopper treeChopper = new TreeChopper(playerIn, blockPos, blockPos1, true);
                    treeChopper.breakAllBlocks(Integer.MAX_VALUE);
                }
            } else {
                String response = "Block at \"" + blockPos.toString() + "\" is " + ((blockPos1 != null) ? "contained in a tree" : "not contained in a tree") + ".";
                if (!worldIn.isRemote) {
                    playerIn.addChatComponentMessage(new ChatComponentText(response));
                    LogHelper.info(response);
                    if (blockPos1 != null) {
                        this.treeChopper = new TreeChopper(playerIn, blockPos, blockPos1, true);
                    }
                }
            }
            return true;
        }
        return false;
    }

    public Set<BlockPos> getBlocks() {
        Set<BlockPos> blockPosSet = new LinkedHashSet<>();
        if (treeChopper != null && !treeChopper.isFinished()) {
            blockPosSet.addAll(treeChopper.getCurrentTree());
        }
        return blockPosSet;
    }
}
