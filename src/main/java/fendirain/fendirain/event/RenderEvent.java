package fendirain.fendirain.event;

import fendirain.fendirain.common.entity.mob.EntityFenderium.EntityFenderiumMob;
import fendirain.fendirain.common.item.ItemDebug;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.Set;

@SideOnly(Side.CLIENT)
public class RenderEvent implements IResourceManagerReloadListener {
    private final TextureAtlasSprite[] destroyBlockIcons = new TextureAtlasSprite[10];

    @SubscribeEvent
    public void renderEvent(RenderWorldLastEvent renderWorldLastEvent) {
        PlayerControllerMP playerControllerMP = Minecraft.getMinecraft().playerController;
        EntityPlayer entityPlayer = Minecraft.getMinecraft().thePlayer;
        World world = entityPlayer.worldObj;

        // Temp code for debugging
        if (entityPlayer.getCurrentEquippedItem() != null && (entityPlayer.getCurrentEquippedItem().getItem() instanceof ItemDebug)) {
            MovingObjectPosition movingObjectPosition = entityPlayer.rayTrace(playerControllerMP.getBlockReachDistance(), renderWorldLastEvent.partialTicks);
            if (movingObjectPosition != null) {
                Item item = entityPlayer.getCurrentEquippedItem().getItem();
                Set<BlockPos> blockPosSet;
                if (item instanceof ItemDebug) blockPosSet = ((ItemDebug) item).getBlocks();
                else return;
                if (!blockPosSet.isEmpty()) {
                    blockPosSet.forEach(blockPos -> {
                        MovingObjectPosition movingObjectPositionIn = new MovingObjectPosition(new Vec3(0, 0, 0), null, blockPos);
                        GlStateManager.enableBlend();
                        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                        GlStateManager.color(255F, 238F, 0.0F, 1F);
                        GL11.glLineWidth(2.0F);
                        GlStateManager.disableTexture2D();
                        GlStateManager.depthMask(false);
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

        // Not yet working - Will fix at a later time.
        for (EntityFenderiumMob entityFenderiumMob : world.getEntitiesWithinAABB(EntityFenderiumMob.class, entityPlayer.getEntityBoundingBox().expand(31, 31, 31))) {
            if (entityFenderiumMob.isCurrentlyChopping() && entityFenderiumMob.getCurrentlyBreakingPos() != null && entityFenderiumMob.getWholeTreeProgress() != -1) {
                double d0 = entityPlayer.lastTickPosX + (entityPlayer.posX - entityPlayer.lastTickPosX) * (double) renderWorldLastEvent.partialTicks;
                double d1 = entityPlayer.lastTickPosY + (entityPlayer.posY - entityPlayer.lastTickPosY) * (double) renderWorldLastEvent.partialTicks;
                double d2 = entityPlayer.lastTickPosZ + (entityPlayer.posZ - entityPlayer.lastTickPosZ) * (double) renderWorldLastEvent.partialTicks;

                BlockPos blockPos = entityFenderiumMob.getCurrentlyBreakingPos();
                int progress = entityFenderiumMob.getWholeTreeProgress();

                if (progress < 0 || progress > 9)
                    break;

                TextureManager renderEngine = Minecraft.getMinecraft().renderEngine;
                Tessellator tessellator = Tessellator.getInstance();
                WorldRenderer worldRenderer = tessellator.getWorldRenderer();

                renderEngine.bindTexture(TextureMap.locationBlocksTexture);
                //preRenderDamagedBlocks BEGIN
                GlStateManager.tryBlendFuncSeparate(774, 768, 1, 0);
                GlStateManager.enableBlend();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
                GlStateManager.doPolygonOffset(-3.0F, -3.0F);
                GlStateManager.enablePolygonOffset();
                GlStateManager.alphaFunc(516, 0.1F);
                GlStateManager.enableAlpha();
                GlStateManager.pushMatrix();
                //preRenderDamagedBlocks END
                worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
                worldRenderer.setTranslation(-d0, -d1, -d2);

                Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockDamage(world.getBlockState(blockPos), blockPos, this.destroyBlockIcons[progress], world);

                tessellator.draw();
                worldRenderer.setTranslation(0.0D, 0.0D, 0.0D);
                // postRenderDamagedBlocks BEGIN
                GlStateManager.disableAlpha();
                GlStateManager.doPolygonOffset(0.0F, 0.0F);
                GlStateManager.disablePolygonOffset();
                GlStateManager.enableAlpha();
                GlStateManager.depthMask(true);
                GlStateManager.popMatrix();
                // postRenderDamagedBlocks END
            }
        }
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();
        for (int i = 0; i < this.destroyBlockIcons.length; ++i) {
            this.destroyBlockIcons[i] = texturemap.getAtlasSprite("minecraft:blocks/destroy_stage_" + i);
        }
    }
}
