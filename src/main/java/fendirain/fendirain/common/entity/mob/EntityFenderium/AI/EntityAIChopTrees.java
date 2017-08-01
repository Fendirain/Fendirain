package fendirain.fendirain.common.entity.mob.EntityFenderium.AI;

import fendirain.fendirain.common.entity.mob.EntityFenderium.EntityFenderiumMob;
import fendirain.fendirain.common.item.ItemFenderiumAxe;
import fendirain.fendirain.network.PacketHandler;
import fendirain.fendirain.network.packets.EntityFenderiumChoppingPacket;
import fendirain.fendirain.reference.ConfigValues;
import fendirain.fendirain.utility.helper.BlockTools;
import fendirain.fendirain.utility.tools.TreeChecker;
import fendirain.fendirain.utility.tools.TreeChopper;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

public class EntityAIChopTrees extends EntityAIBase {
    private final int timePer;
    private final boolean doTimePerLog;
    private final Random rand;
    private final EntityFenderiumMob entity;
    private final PathNavigate pathFinder;
    private final float moveSpeed;
    private TreeChopper treeChopper;
    private boolean alreadyExecuting;
    private int timeToWaitUntilNextRun;
    private boolean reloaded = false;

    public EntityAIChopTrees(EntityFenderiumMob entity, Random rand, float moveSpeed, boolean doTimePerLog, int timePer) {
        this.entity = entity;
        this.rand = rand;
        this.moveSpeed = moveSpeed;
        this.doTimePerLog = doTimePerLog;
        this.timePer = timePer;
        this.pathFinder = entity.getNavigator();
        this.alreadyExecuting = false;
        this.timeToWaitUntilNextRun = 2400;
        this.treeChopper = null;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (reloaded) return true;
        if ((!this.entity.world.getGameRules().getBoolean("mobGriefing") && ConfigValues.isMobGriefingGameRuleFollowed) || alreadyExecuting || timeToWaitUntilNextRun > 0 || !(entity.getHeldItemMainhand().getItem() instanceof ItemFenderiumAxe)) {
            return false;
        }
        if ((timeToWaitUntilNextRun == 0 || rand.nextInt(1000) == 1)) {
            World world = entity.world;
            int range = entity.getMaxRange();
            BlockPos closest = null;
            double currentDist = -1.0;
            for (int y = (int) entity.posY - range; y <= (int) entity.posY + range; y++) {
                for (int x = (int) entity.posX - range; x <= (int) entity.posX + range; x++) {
                    for (int z = (int) entity.posZ - range; z <= (int) entity.posZ + range; z++) {
                        BlockPos blockPos = new BlockPos(x, y, z);
                        if (entity.isItemValidForBreaking(world, blockPos, world.getBlockState(blockPos).getBlock()) && entity.isAnySpaceForItemPickup(new ItemStack(world.getBlockState(blockPos).getBlock(), 1))) {
                            Vec3d blockVec = new Vec3d(x, y, z).subtract(entity.getPositionVector());
                            Vec3d lookVec = entity.getLookVec();
                            double degree = Math.acos(((blockVec.x * lookVec.x) + (blockVec.y * lookVec.y) + (blockVec.z * lookVec.z)) / (Math.sqrt((blockVec.x * blockVec.x) + (blockVec.y * blockVec.y) + (blockVec.z * blockVec.z)) * Math.sqrt((lookVec.x * lookVec.x) + (lookVec.y * lookVec.y) + (lookVec.z * lookVec.z)))) * 180 / Math.PI;
                            if (degree < 80 && TreeChecker.isTree(world, blockPos) != null) {
                                double dist = entity.getDistance(x, y, z);
                                Path path = pathFinder.getPathToXYZ(x, y, z);
                                if (path == null || path.getFinalPathPoint() == null) break;
                                BlockPos blockPos1 = new BlockPos(path.getFinalPathPoint().x, path.getFinalPathPoint().y, path.getFinalPathPoint().z);
                                boolean canReach = BlockTools.compareTo(blockPos, blockPos1) <= 2;
                                if (canReach && (closest == null || currentDist == -1 || dist < currentDist)) {
                                    currentDist = dist;
                                    closest = blockPos;
                                }
                            }
                        }
                    }
                }
            }
            if (closest != null) {
                treeChopper = new TreeChopper(entity, closest, TreeChecker.isTree(world, closest), true, null);
                return true;
            } else timeToWaitUntilNextRun = timeToWaitUntilNextRun + 50;
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (treeChopper == null) return false;
        if (entity.world.getBlockState(treeChopper.getMainBlockPos()).getBlock() != treeChopper.getMainBlock())
            treeChopper.setMainBlockPosToClosest();
        if (!treeChopper.isFinished() && !treeChopper.isFinishedSearching()) return true;
        if (treeChopper.isFinishedSearching() && rand.nextInt(12) == 0)
            treeChopper.updateCurrentTreeBlocks(); // Occasionally checks if all the blocks are valid so the entity won't get stuck if the whole tree is suddenly destroyed.
        return (!treeChopper.isFinishedSearching() && !treeChopper.isFinished()) || !treeChopper.isFinished();
    }

    @Override
    public void startExecuting() {
        if (!alreadyExecuting) alreadyExecuting = true;
        else if (reloaded) reloaded = false;
        pathFinder.tryMoveToXYZ(treeChopper.getMainBlockPos().getX(), treeChopper.getMainBlockPos().getY(), treeChopper.getMainBlockPos().getZ(), this.moveSpeed);
    }

    @Override
    public void resetTask() {
        if (treeChopper != null) {
            treeChopper.resetBlockProgress();
            treeChopper = null;
            alreadyExecuting = false;
            PacketHandler.sendToAllAround(new EntityFenderiumChoppingPacket(entity.getEntityId(), false, -1L, -1), entity, 32);
        }
        for (Object potion : entity.getActivePotionEffects()) {
            PotionEffect potionEffect = (PotionEffect) potion;
            if (potionEffect.getPotion() == MobEffects.HASTE) {
                entity.removePotionEffect(MobEffects.HASTE);
                break;
            }
        }
    }

    @Override
    public void updateTask() {
        if (treeChopper.isFinishedSearching() && pathFinder.noPath() && entity.getDistance(treeChopper.getMainBlockPos().getX(), treeChopper.getMainBlockPos().getY(), treeChopper.getMainBlockPos().getZ()) < 2) {
            PacketHandler.sendToAllAround(new EntityFenderiumChoppingPacket(entity.getEntityId(), true, treeChopper.getCurrentlyBreakingPos().toLong(), treeChopper.getWholeTreeProgress()), entity, 32);
            entity.getLookHelper().setLookPosition(treeChopper.getMainBlockPos().getX(), treeChopper.getMainBlockPos().getY(), treeChopper.getMainBlockPos().getZ(), 2, 1);
            ItemStack itemStack = treeChopper.continueBreaking(entity.getBreakSpeed());
            if (itemStack != null) {
                entity.putIntoInventory(itemStack.copy());
                if (doTimePerLog) {
                    if (timeToWaitUntilNextRun < 0) timeToWaitUntilNextRun = 0;
                    timeToWaitUntilNextRun += timePer * itemStack.getCount();
                } else if (timeToWaitUntilNextRun != timePer) timeToWaitUntilNextRun = timePer;
            }
            if (treeChopper.isFinished() || (itemStack != null && !entity.isAnySpaceForItemPickup(itemStack)))
                resetTask();
        } else if (treeChopper.isFinishedSearching() && pathFinder.noPath()) {
            PacketHandler.sendToAllAround(new EntityFenderiumChoppingPacket(entity.getEntityId(), false, -1L, -1), entity, 32);
            startExecuting();
        }
    }

    public boolean isAlreadyExecuting() {
        return alreadyExecuting;
    }


    @Override
    public boolean isInterruptible() {
        return true;
    }

    public int getTimeToWaitUntilNextRun() {
        return timeToWaitUntilNextRun;
    }

    public void setTimeToWaitUntilNextRun(int timeToWaitUntilNextRun) {
        this.timeToWaitUntilNextRun = timeToWaitUntilNextRun;
    }

    // TODO Redo writeToNBT and readFromNBT
    public NBTTagCompound writeToNBT() {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        /*nbtTagCompound.setBoolean("alreadyExecuting", alreadyExecuting);
        if (alreadyExecuting) {

        }*/
        nbtTagCompound.setInteger("timeToWaitUntilNextRun", timeToWaitUntilNextRun);
        return nbtTagCompound;
    }

    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        /*alreadyExecuting = nbtTagCompound.getBoolean("alreadyExecuting");
        if (alreadyExecuting) {
            currentBlockProgress = nbtTagCompound.getInteger("currentBlockProgress");
            double[] temp = new double[3];
            temp[0] = nbtTagCompound.getDouble("treeTargetBlockProgress0");
            temp[1] = nbtTagCompound.getDouble("treeTargetBlockProgress1");
            temp[2] = nbtTagCompound.getDouble("treeTargetBlockProgress2");
            treeTargetBlockProgress = temp;
            treeTargetFullBlock = new FullBlock(nbtTagCompound.getCompoundTag("targetTreeBlock"));
            treeLeaf = new FullBlock(nbtTagCompound.getCompoundTag("treeLeafBlock"));
            *//*NBTTagCompound treeBlocks = nbtTagCompound.getCompoundTag("currentTree");
            Set<FullBlock> currentTree = new LinkedHashSet<>(treeBlocks.getKeySet().size());
            treeBlocks.getKeySet().forEach(key -> currentTree.add(new FullBlock(treeBlocks.getCompoundTag(key))));
            this.currentTree = currentTree;*//*
            //reloaded = true;
        }*/
        timeToWaitUntilNextRun = nbtTagCompound.getInteger("timeToWaitUntilNextRun");
    }
}
