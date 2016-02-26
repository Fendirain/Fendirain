package fendirain.fendirain.event;

import fendirain.fendirain.common.item.ItemDebug;
import fendirain.fendirain.common.item.ItemFenderiumAxe;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.Set;

public class RenderEvent {

    // Temp code for debugging
    @SubscribeEvent
    public void renderSelectionEvent(RenderWorldLastEvent renderWorldLastEvent) {
        PlayerControllerMP playerControllerMP = Minecraft.getMinecraft().playerController;
        EntityPlayer entityPlayer = Minecraft.getMinecraft().thePlayer;
        World world = entityPlayer.getEntityWorld();
        if (entityPlayer.getCurrentEquippedItem() != null && (entityPlayer.getCurrentEquippedItem().getItem() instanceof ItemFenderiumAxe || entityPlayer.getCurrentEquippedItem().getItem() instanceof ItemDebug)) {
            MovingObjectPosition movingObjectPosition = entityPlayer.rayTrace(playerControllerMP.getBlockReachDistance(), renderWorldLastEvent.partialTicks);
            if (movingObjectPosition != null) {
                Item item = entityPlayer.getCurrentEquippedItem().getItem();
                Set<BlockPos> blockPosSet;
                if (item instanceof ItemFenderiumAxe) blockPosSet = ((ItemFenderiumAxe) item).getBlocks();
                else blockPosSet = ((ItemDebug) item).getBlocks();
                if (!blockPosSet.isEmpty()) {
                    blockPosSet.forEach(blockPos -> {
                        MovingObjectPosition movingObjectPositionIn = new MovingObjectPosition(new Vec3(0, 0, 0), null, blockPos);
                        //renderWorldLastEvent.context.drawSelectionBox(entityPlayer, new MovingObjectPosition(new Vec3(0, 0, 0), null, blockPos), 0, renderWorldLastEvent.partialTicks);
                        GlStateManager.enableBlend();
                        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                        GlStateManager.color(255F, 238F, 0.0F, 1F);
                        GL11.glLineWidth(2.0F);
                        GlStateManager.disableTexture2D();
                        GlStateManager.depthMask(false);
                        float f = 0.002F;
                        BlockPos blockpos = movingObjectPositionIn.getBlockPos();
                        Block block = world.getBlockState(blockpos).getBlock();
                        if (block.getMaterial() != Material.air && world.getWorldBorder().contains(blockpos)) {
                            block.setBlockBoundsBasedOnState(world, blockpos);
                            double d0 = entityPlayer.lastTickPosX + (entityPlayer.posX - entityPlayer.lastTickPosX) * (double) renderWorldLastEvent.partialTicks;
                            double d1 = entityPlayer.lastTickPosY + (entityPlayer.posY - entityPlayer.lastTickPosY) * (double) renderWorldLastEvent.partialTicks;
                            double d2 = entityPlayer.lastTickPosZ + (entityPlayer.posZ - entityPlayer.lastTickPosZ) * (double) renderWorldLastEvent.partialTicks;
                            AxisAlignedBB boundingBox = block.getSelectedBoundingBox(world, blockpos).expand(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D).offset(-d0, -d1, -d2);
                            Tessellator tessellator = Tessellator.getInstance();
                            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                            worldrenderer.begin(3, DefaultVertexFormats.POSITION);
                            worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
                            worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
                            worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
                            worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
                            worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
                            tessellator.draw();
                            worldrenderer.begin(3, DefaultVertexFormats.POSITION);
                            worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
                            worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
                            worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
                            worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
                            worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
                            tessellator.draw();
                            worldrenderer.begin(1, DefaultVertexFormats.POSITION);
                            worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
                            worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
                            worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
                            worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
                            worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
                            worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
                            worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
                            worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
                            tessellator.draw();
                        }
                        GlStateManager.depthMask(true);
                        GlStateManager.enableTexture2D();
                        GlStateManager.disableBlend();
                    });
                }
            }
        }
    }
}
