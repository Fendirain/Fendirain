package fendirain.fendirain.common.block;

import fendirain.fendirain.common.entity.tile.TileFendiBlock;
import fendirain.fendirain.common.item.ItemBlockFendirain;
import fendirain.fendirain.creativetab.CreativeTabFendirain;
import fendirain.fendirain.init.ModItems;
import fendirain.fendirain.reference.Reference;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BlockFendi extends BlockContainer {

    public BlockFendi() {
        super(Material.ROCK);
        this.setRegistryName(Reference.MOD_ID, "blockFendi".toLowerCase());
        ForgeRegistries.BLOCKS.register(this);
        this.setHardness(2.0F);
        this.setResistance(5.0F);
        this.setUnlocalizedName("blockFendi");
        this.setCreativeTab(CreativeTabFendirain.FENDIRAIN_TAB);
        this.registerItemForm();
    }

    public static void addCollisionBoxToList(BlockPos blockPos, AxisAlignedBB axisAlignedBB, List<AxisAlignedBB> collidingBoxes, @Nullable AxisAlignedBB blockBox) {
        ArrayList<AxisAlignedBB> axisAlignedBBArrayList = new ArrayList<>();
        axisAlignedBBArrayList.add(getBoundingBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 0.125F, 0.125F, 0.125F, 0.200F, 0.885F, 0.885F));
        axisAlignedBBArrayList.add(getBoundingBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 0.125F, 0.125F, 0.125F, 0.885F, 0.885F, 0.200F));
        axisAlignedBBArrayList.add(getBoundingBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 0.125F, 0.125F, 0.800F, 0.885F, 0.885F, 0.885F));
        axisAlignedBBArrayList.add(getBoundingBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 0.800F, 0.125F, 0.125F, 0.885F, 0.885F, 0.885F));
        //noinspection unchecked
        collidingBoxes.addAll(axisAlignedBBArrayList.stream().filter(aAxisAlignedBB -> aAxisAlignedBB != null && axisAlignedBB.intersects(aAxisAlignedBB)).collect(Collectors.toList()));
    }

    private static AxisAlignedBB getBoundingBox(int x, int y, int z, double x1, double y1, double z1, double x2, double y2, double z2) {
        return new AxisAlignedBB(x + x1, y + y1, z + z1, x + x2, y + y2, z + z2);
    }

    private void registerItemForm() {
        ItemBlockFendirain itemBlockFendirain = new ItemBlockFendirain(this);
        itemBlockFendirain.setRegistryName(this.getRegistryName());
        ForgeRegistries.ITEMS.register(itemBlockFendirain);
    }

    /*@Override
    public boolean isOpaqueCube(IBlockState iBlockState) {
        return false;
    }*/

    @Override
    public EnumBlockRenderType getRenderType(IBlockState iBlockState) {
        return EnumBlockRenderType.INVISIBLE;
    }

    public AxisAlignedBB getCollisionBoundingBox(IBlockState iBlockState, World world, BlockPos blockPos) {
        return new AxisAlignedBB((double) blockPos.getX() + 0.125F, (double) blockPos.getY() + 0.125F, (double) blockPos.getZ() + 0.125F, (double) blockPos.getX() + 0.885F, (double) blockPos.getY() + 0.885F, (double) blockPos.getZ() + 0.885F);
    }

   /* @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return false;
    }*/

    @Override
    public String getUnlocalizedName() {
        return String.format("tile.%s%s", Reference.MOD_ID + ":", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

    private String getUnwrappedUnlocalizedName(String unlocalizedName) {
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return new TileFendiBlock();
    }

    /*@Override
    public EnumPushReaction getMobilityFlag(IBlockState iBlockState) {
        return EnumPushReaction.BLOCK;
    }*/

    @Override
    public boolean canDropFromExplosion(Explosion explosion) {
        return false;
    }

    @Override
    public boolean canHarvestBlock(IBlockAccess iBlockAccess, BlockPos blockPos, EntityPlayer player) {
        return false;
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos blockPos, IBlockState iBlockState, EntityPlayer player) {
        return true;
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, net.minecraft.entity.EntityLiving.SpawnPlacementType type) {
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos blockPos) {
        return world.getBlockState(blockPos).getBlock().isReplaceable(world, blockPos) && world.getBlockState(blockPos.down()).getBlock().isNormalCube(world.getBlockState(blockPos), world, blockPos);
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack itemStack = player.getHeldItem(EnumHand.MAIN_HAND);
        TileFendiBlock tileFendiBlock = (TileFendiBlock) world.getTileEntity(pos);
        if (itemStack.getItem() == ModItems.itemFendiPiece && tileFendiBlock != null && tileFendiBlock.getCurrentAmount() != tileFendiBlock.getAmountNeededToComplete()) {
            itemStack.setCount(itemStack.getCount() - 1);
            tileFendiBlock.addToCurrentAmount(1);
            return true;
        }
        return false;
    }
}
