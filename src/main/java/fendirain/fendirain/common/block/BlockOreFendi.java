package fendirain.fendirain.common.block;

import fendirain.fendirain.init.ModItems;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;

import java.util.Random;

public class BlockOreFendi extends BlockFendirain {
    @SuppressWarnings("FieldCanBeLocal")
    private final int dropAmountMin = 0, dropChance = 16;

    public BlockOreFendi() {
        super("blockOreFendi");
        this.setHardness(3.0F);
        this.setResistance(5.0F);
        this.setSoundType(SoundType.STONE);
        this.setUnlocalizedName("blockOreFendi");
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return ModItems.itemFendiPiece;
    }

    @Override
    public int quantityDropped(Random rand) {
        int number = rand.nextInt(dropChance + 1);
        if (number == 2) {
            return 2;
        } else if (number == 0 || number == 8 || number == 16) {
            return dropAmountMin;
        } else return 1;
    }
}
