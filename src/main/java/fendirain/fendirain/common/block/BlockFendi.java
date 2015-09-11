package fendirain.fendirain.common.block;

import fendirain.fendirain.common.entity.tile.TileFendiBlock;
import fendirain.fendirain.creativetab.CreativeTabFendirain;
import fendirain.fendirain.init.ModItems;
import fendirain.fendirain.reference.Reference;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class BlockFendi extends BlockContainer {

    public BlockFendi() {
        super(Material.rock);
        this.setHardness(2.0F);
        this.setResistance(5.0F);
        this.setUnlocalizedName("blockFendi");
        this.setCreativeTab(CreativeTabFendirain.FENDIRAIN_TAB);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
        this.setBlockBounds(0.125F, 0.125F, 0.125F, 0.885F, 0.885F, 0.885F);
    }

    @Override
    public void addCollisionBoxesToList(World world, BlockPos blockPos, IBlockState iBlockState, AxisAlignedBB axisAlignedBB, List list, Entity entity) {
        ArrayList<AxisAlignedBB> axisAlignedBBArrayList = new ArrayList<AxisAlignedBB>();
        axisAlignedBBArrayList.add(getBoundingBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 0.125F, 0.125F, 0.125F, 0.200F, 0.885F, 0.885F));
        axisAlignedBBArrayList.add(getBoundingBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 0.125F, 0.125F, 0.125F, 0.885F, 0.885F, 0.200F));
        axisAlignedBBArrayList.add(getBoundingBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 0.125F, 0.125F, 0.800F, 0.885F, 0.885F, 0.885F));
        axisAlignedBBArrayList.add(getBoundingBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 0.800F, 0.125F, 0.125F, 0.885F, 0.885F, 0.885F));
        for (AxisAlignedBB aAxisAlignedBB : axisAlignedBBArrayList) {
            if (aAxisAlignedBB != null && axisAlignedBB.intersectsWith(aAxisAlignedBB)) //noinspection unchecked
                list.add(aAxisAlignedBB);
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
        return AxisAlignedBB.fromBounds((double) pos.getX() + this.minX, (double) pos.getY() + this.minY, (double) pos.getZ() + this.minZ, (double) pos.getX() + this.maxX, (double) pos.getY() + this.maxY, (double) pos.getZ() + this.maxZ);
    }

    private AxisAlignedBB getBoundingBox(int x, int y, int z, double x1, double y1, double z1, double x2, double y2, double z2) {
        return AxisAlignedBB.fromBounds(x + x1, y + y1, z + z1, x + x2, y + y2, z + z2);
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public String getUnlocalizedName() {
        return String.format("tile.%s%s", Reference.MOD_ID.toLowerCase() + ":", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

    private String getUnwrappedUnlocalizedName(String unlocalizedName) {
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return new TileFendiBlock();
    }

    @Override
    public int getMobilityFlag() {
        return 2;
    }

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
    public boolean canCreatureSpawn(IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos blockPos) {
        return world.getBlockState(blockPos).getBlock().isReplaceable(world, blockPos) && world.getBlockState(blockPos.down()).getBlock().isFullBlock();
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, BlockPos blockPos) {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack itemStack = player.getCurrentEquippedItem();
        TileFendiBlock tileFendiBlock = (TileFendiBlock) world.getTileEntity(pos);
        if (itemStack != null && itemStack.getItem() == ModItems.fendiPiece && tileFendiBlock.getCurrentAmount() != tileFendiBlock.getAmountNeededToComplete()) {
            player.getHeldItem().stackSize -= 1;
            tileFendiBlock.addToCurrentAmount(1);
            return true;
        }
        return false;
    }
}