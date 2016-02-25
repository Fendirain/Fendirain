package fendirain.fendirain.common.item;

import fendirain.fendirain.utility.helper.FullBlock;
import fendirain.fendirain.utility.helper.LogHelper;
import fendirain.fendirain.utility.tools.TreeChecker;
import fendirain.fendirain.utility.tools.TreeChopper;
import net.minecraft.block.BlockLog;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemDebug extends ItemFendirain {
    public ItemDebug() {
        super();
        this.setMaxStackSize(1);
        this.setUnlocalizedName("itemDebug");
    }

    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (worldIn.getBlockState(pos).getBlock() instanceof BlockLog) {
            FullBlock fullBlock = TreeChecker.isTree(worldIn, pos);
            if (playerIn.isSneaking()) {
                if (fullBlock != null && !worldIn.isRemote) {
                    TreeChopper treeChopper = new TreeChopper(playerIn, new FullBlock(worldIn.getBlockState(pos).getBlock(), pos, worldIn.getBlockState(pos).getBlock().getDamageValue(worldIn, pos)), fullBlock, true);
                    treeChopper.breakAllBlocks(Integer.MAX_VALUE);
                }
            } else {
                String response = "Block at \"" + pos.toString() + "\" is " + ((fullBlock != null) ? "contained in a tree" : "not contained in a tree") + ".";
                if (!worldIn.isRemote) {
                    playerIn.addChatComponentMessage(new ChatComponentText(response));
                    LogHelper.info(response);
                }
            }
            return true;
        }
        return false;
    }
}
