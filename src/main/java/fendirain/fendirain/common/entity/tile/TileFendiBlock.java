package fendirain.fendirain.common.entity.tile;

import net.minecraft.nbt.NBTTagCompound;

public class TileFendiBlock extends TileFendirain {
    private int currentAmount = 0;
    private int amountNeededToComplete = 8;


    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);

        currentAmount = nbtTagCompound.getInteger("currentAmount");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);

        nbtTagCompound.setInteger("currentAmount", currentAmount);
    }

    public void addToCurrentAmount(int amountToAdd) {
        currentAmount += amountToAdd;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public int getCurrentAmount() {
        return currentAmount;
    }

    public int getAmountNeededToComplete() {
        return amountNeededToComplete;
    }

    @Override
    public void updateEntity() {
        /*if (currentAmount > 1) {
            // Nothing yet
        }*/
    }
}
