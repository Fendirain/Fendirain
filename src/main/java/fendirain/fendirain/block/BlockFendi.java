package fendirain.fendirain.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fendirain.fendirain.creativetab.CreativeTabFendirain;
import fendirain.fendirain.entity.tile.TileFendiBlock;
import fendirain.fendirain.reference.Reference;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class BlockFendi extends BlockContainer {

    public BlockFendi(Material material) {
        super(material);
        this.setHardness(2.0F);
        this.setResistance(5.0F);
        this.setHarvestLevel("pickAxe", 3);
        this.setBlockName("fendiBlock");
        this.setBlockTextureName("fendiBlock");
        this.setCreativeTab(CreativeTabFendirain.FENDIRAIN_TAB);
    }

    public BlockFendi() {
        super(Material.rock);
        this.setHardness(2.0F);
        this.setResistance(5.0F);
        this.setBlockName("fendiBlock");
        this.setBlockTextureName("fendiBlock");
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
    public void setBlockBoundsBasedOnState(IBlockAccess iBlockAccess, int x, int y, int z) {
        this.setBlockBounds(0.125F, 0.125F, 0.125F, 0.885F, 0.885F, 0.885F);
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB axisAlignedBB, List list, Entity entity) {
        ArrayList<AxisAlignedBB> axisAlignedBBArrayList = new ArrayList<AxisAlignedBB>();
        axisAlignedBBArrayList.add(getBoundingBox(x, y, z, 0.125F, 0.125F, 0.125F, 0.200F, 0.885F, 0.885F));
        axisAlignedBBArrayList.add(getBoundingBox(x, y, z, 0.125F, 0.125F, 0.125F, 0.885F, 0.885F, 0.200F));
        axisAlignedBBArrayList.add(getBoundingBox(x, y, z, 0.125F, 0.125F, 0.800F, 0.885F, 0.885F, 0.885F));
        axisAlignedBBArrayList.add(getBoundingBox(x, y, z, 0.800F, 0.125F, 0.125F, 0.885F, 0.885F, 0.885F));
        for (AxisAlignedBB aAxisAlignedBB : axisAlignedBBArrayList) {
            if (aAxisAlignedBB != null && axisAlignedBB.intersectsWith(aAxisAlignedBB)) //noinspection unchecked
                list.add(aAxisAlignedBB);
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
        return AxisAlignedBB.getBoundingBox((double) i + this.minX, (double) j + this.minY, (double) k + this.minZ, (double) i + this.maxX, (double) j + this.maxY, (double) k + this.maxZ);
    }

    private AxisAlignedBB getBoundingBox(int x, int y, int z, double x1, double y1, double z1, double x2, double y2, double z2) {
        return AxisAlignedBB.getBoundingBox(x + x1, y + y1, z + z1, x + x2, y + y2, z + z2);
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess iBlockAccess, int i, int j, int k, int l) {
        return false;
    }

    @Override
    public String getUnlocalizedName() {
        return String.format("tile.%s%s", Reference.MOD_ID.toLowerCase() + ":", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(String.format("%s", getUnwrappedUnlocalizedName(this.getUnlocalizedName())));
    }

    public String getUnwrappedUnlocalizedName(String unlocalizedName) {
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
    public boolean canHarvestBlock(EntityPlayer player, int meta) {
        return false;
    }

    @Override
    public boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata) {
        return true;
    }

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return world.getBlock(x, y, z).isReplaceable(world, x, y, z) && world.getBlock(x, (y - 1), z).isBlockNormalCube();
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
        return false;
    }
}