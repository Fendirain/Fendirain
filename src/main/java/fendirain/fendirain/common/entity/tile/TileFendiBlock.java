package fendirain.fendirain.common.entity.tile;

import net.minecraft.nbt.NBTTagCompound;

public class TileFendiBlock extends TileFendirain {
    @SuppressWarnings("FieldCanBeLocal")
    private final int amountNeededToComplete = 8;
    private int currentAmount = 0;

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
        //worldObj.markBlockForUpdate(pos);
    }

    public int getCurrentAmount() {
        return currentAmount;
    }

    public int getAmountNeededToComplete() {
        return amountNeededToComplete;
    }

    /*@Override
    public void updateEntity() {
        *//*if (currentAmount > 1) {
            // Nothing yet
        }*//*
    }*/
}
