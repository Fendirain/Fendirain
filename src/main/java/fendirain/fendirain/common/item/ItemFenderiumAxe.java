package fendirain.fendirain.common.item;

import fendirain.fendirain.Fendirain;
import fendirain.fendirain.creativetab.CreativeTabFendirain;
import fendirain.fendirain.init.ModItems;
import fendirain.fendirain.network.PacketHandler;
import fendirain.fendirain.network.packets.DestroyItemPacket;
import fendirain.fendirain.network.packets.FenderiumAxeProgressPacket;
import fendirain.fendirain.reference.ConfigValues;
import fendirain.fendirain.reference.Reference;
import fendirain.fendirain.utility.tools.TreeChecker;
import fendirain.fendirain.utility.tools.TreeChopper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemFenderiumAxe extends ItemAxe {
    private final int axeDamagePerBlock = 4;
    private TreeChopper treeChopper = null;
    private int lastUsed = 0;
    private float axeProgress = 0;

    public ItemFenderiumAxe() {
        super(ModItems.fendi, 5.0F, -3.0F);
        this.setRegistryName(Reference.MOD_ID, "itemFenderiumAxe");
        ForgeRegistries.ITEMS.register(this);
        this.setUnlocalizedName("itemFenderiumAxe");
        this.setCreativeTab(CreativeTabFendirain.FENDIRAIN_TAB);
        this.addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter() {
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn) {
                if (entityIn == null) return 0.0F;
                else {
                    ItemStack itemStack = entityIn.getActiveItemStack();
                    if (itemStack.getItem() == ModItems.itemFenderiumAxe && (treeChopper != null || entityIn.world.isRemote))
                        return axeProgress;
                    else return 0.0F;
                }
            }
        });
        this.addPropertyOverride(new ResourceLocation("pulling"), new IItemPropertyGetter() {
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn) {
                return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
            }
        });
    }

    public void setAxeProgress(float axeProgress) {
        this.axeProgress = axeProgress;
    }

    @Override
    public float getStrVsBlock(ItemStack stack, IBlockState state) {
        Material material = state.getMaterial();
        return material != Material.WOOD && material != Material.PLANTS && material != Material.VINE ? super.getStrVsBlock(stack, state) : this.efficiencyOnProperMaterial;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer entityPlayerIn, World world, BlockPos blockPos, EnumHand enumHand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!ConfigValues.fenderiumAxe_disableRightClickFunction && EnumHand.MAIN_HAND.equals(enumHand)) {
            ItemStack itemStack = entityPlayerIn.getHeldItem(enumHand);
            if (itemStack.getMaxDamage() - itemStack.getItemDamage() > 4) {
                Block block = world.getBlockState(blockPos).getBlock();
                if (block.isWood(world, blockPos)) {
                    if (treeChopper != null && treeChopper.isBlockContainedInTree(blockPos)) {
                        treeChopper.setMainBlockPos(blockPos);
                        entityPlayerIn.setActiveHand(enumHand);
                        return EnumActionResult.SUCCESS;
                    } else {
                        BlockPos treeLeaf = TreeChecker.isTree(world, blockPos);
                        if (treeLeaf != null) {
                            if (!world.isRemote)
                                treeChopper = new TreeChopper(entityPlayerIn, blockPos, treeLeaf, true, itemStack);
                            entityPlayerIn.setActiveHand(enumHand);
                            return EnumActionResult.SUCCESS;
                        }
                    }
                }
            }
        }
        return EnumActionResult.PASS;
    }

    @Override
    public void onUsingTick(ItemStack itemStack, EntityLivingBase entityLivingBase, int count) {
        this.lastUsed = 0;
        if (treeChopper != null && !entityLivingBase.world.isRemote) {
            axeProgress = (float) ((getMaxItemUseDuration(itemStack) - count) / 2) / (treeChopper.getNumberOfLogs() <= ((itemStack.getMaxDamage() - itemStack.getItemDamage()) / axeDamagePerBlock) ? (float) treeChopper.getNumberOfLogs() : ((float) (itemStack.getMaxDamage() - itemStack.getItemDamage()) / axeDamagePerBlock));
            PacketHandler.sendTo(new FenderiumAxeProgressPacket(axeProgress), (EntityPlayerMP) entityLivingBase);
        }
    }


    @Override
    public void onPlayerStoppedUsing(ItemStack itemStack, World worldIn, EntityLivingBase entityLivingBase, int timeLeft) {
        if (!worldIn.isRemote) {
            int maxToBreak = ((this.getMaxItemUseDuration(itemStack) - timeLeft) / 2 < (itemStack.getMaxDamage() - itemStack.getItemDamage()) / axeDamagePerBlock) ? (this.getMaxItemUseDuration(itemStack) - timeLeft) / 2 : (itemStack.getMaxDamage() - itemStack.getItemDamage()) / axeDamagePerBlock;
            treeChopper.breakAllBlocks(maxToBreak);
        }
        if (itemStack.getMaxDamage() <= itemStack.getItemDamage()) {
            PacketHandler.sendTo(new DestroyItemPacket(entityLivingBase.getHeldItemMainhand()), (EntityPlayerMP) entityLivingBase);
            entityLivingBase.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
        }
        if (treeChopper != null && treeChopper.isFinished()) treeChopper = null;
        axeProgress = 0;
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (treeChopper != null) { // This will clear treeChopper if the user doesn't use that axe for 2 minutes.
            lastUsed++;
            if (lastUsed > 2400) {
                treeChopper = null;
                Fendirain.logHelper.info(entityIn.getDisplayName() + " had their FenderiumAxe reset the currently loaded tree (Was 2+ Minutes since last use).");
            }
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
        if (!entityPlayerIn.capabilities.isCreativeMode && !world.isRemote && itemStack.getMaxDamage() - itemStack.getItemDamage() > 0) {
            Block block = world.getBlockState(blockPos).getBlock();
            if (block.isWood(world, blockPos)) {
                if (treeChopper == null || !treeChopper.isBlockContainedInTree(blockPos)) {
                    BlockPos treeLeaf = TreeChecker.isTree(world, blockPos);
                    treeChopper = new TreeChopper(entityPlayerIn, blockPos, treeLeaf, treeLeaf != null, itemStack);
                    treeChopper.breakFurthestBlock();
                } else {
                    if (treeChopper.getMainBlockPos().toLong() != blockPos.toLong())
                        treeChopper.setMainBlockPos(blockPos);
                    treeChopper.breakFurthestBlock();
                }
                if (treeChopper.isFinished()) treeChopper = null;
                lastUsed = 0;
                if (itemStack.getMaxDamage() <= itemStack.getItemDamage()) {
                    PacketHandler.sendTo(new DestroyItemPacket(entityPlayerIn.getHeldItemMainhand()), (EntityPlayerMP) entityPlayerIn);
                    entityPlayerIn.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
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

