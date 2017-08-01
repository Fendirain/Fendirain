package fendirain.fendirain.event;

import fendirain.fendirain.common.item.ItemDebug;
import fendirain.fendirain.utility.helper.BlockTools;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
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
        EntityPlayer entityPlayer = Minecraft.getMinecraft().player;
        World world = entityPlayer.world;

        // Temp code for debugging
        if (entityPlayer.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemDebug) {
            Item item = entityPlayer.getHeldItem(EnumHand.MAIN_HAND).getItem();
            Set<BlockPos> blockPosSet = ((ItemDebug) item).getBlocks();
            if (!blockPosSet.isEmpty()) {
                blockPosSet.stream().filter(blockPos -> BlockTools.compareTo(entityPlayer.getPosition(), blockPos) <= 64).forEach(blockPos -> {
                    RayTraceResult rayTraceResult = new RayTraceResult(new Vec3d(0, 0, 0), null, blockPos);
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                    GlStateManager.color(255F, 238F, 0.0F, 1F);
                    GL11.glLineWidth(2.0F);
                    GlStateManager.disableTexture2D();
                    GlStateManager.depthMask(false);
                    BlockPos blockpos = rayTraceResult.getBlockPos();
                    Block block = world.getBlockState(blockpos).getBlock();
                    if (!block.isAir(world.getBlockState(blockPos), world, blockPos) && world.getWorldBorder().contains(blockpos)) {
                        //block.setBlockBoundsBasedOnState(world, blockpos);
                        double d0 = entityPlayer.lastTickPosX + (entityPlayer.posX - entityPlayer.lastTickPosX) * (double) renderWorldLastEvent.getPartialTicks();
                        double d1 = entityPlayer.lastTickPosY + (entityPlayer.posY - entityPlayer.lastTickPosY) * (double) renderWorldLastEvent.getPartialTicks();
                        double d2 = entityPlayer.lastTickPosZ + (entityPlayer.posZ - entityPlayer.lastTickPosZ) * (double) renderWorldLastEvent.getPartialTicks();
                        AxisAlignedBB boundingBox = block.getExtendedState(world.getBlockState(blockPos), world, blockPos).getSelectedBoundingBox(world, blockpos).expand(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D).offset(-d0, -d1, -d2);
                        Tessellator tessellator = Tessellator.getInstance();

                        BufferBuilder bufferBuilder = tessellator.getBuffer();
                        bufferBuilder.begin(3, DefaultVertexFormats.POSITION);
                        bufferBuilder.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
                        bufferBuilder.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
                        bufferBuilder.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
                        bufferBuilder.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
                        bufferBuilder.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
                        tessellator.draw();
                        bufferBuilder.begin(3, DefaultVertexFormats.POSITION);
                        bufferBuilder.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
                        bufferBuilder.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
                        bufferBuilder.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
                        bufferBuilder.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
                        bufferBuilder.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
                        tessellator.draw();
                        bufferBuilder.begin(1, DefaultVertexFormats.POSITION);
                        bufferBuilder.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
                        bufferBuilder.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
                        bufferBuilder.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
                        bufferBuilder.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
                        bufferBuilder.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
                        bufferBuilder.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
                        bufferBuilder.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
                        bufferBuilder.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
                        tessellator.draw();
                    }
                    GlStateManager.depthMask(true);
                    GlStateManager.enableTexture2D();
                    GlStateManager.disableBlend();
                });
            }
        } // End of temp code

        // Not yet working - TODO Will fix at a later time.
        /*for (EntityFenderiumMob entityFenderiumMob : world.getEntitiesWithinAABB(EntityFenderiumMob.class, entityPlayer.getEntityBoundingBox().expand(31, 31, 31))) {
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
        }*/
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();
        for (int i = 0; i < this.destroyBlockIcons.length; ++i) {
            this.destroyBlockIcons[i] = texturemap.getAtlasSprite("minecraft:blocks/destroy_stage_" + i);
        }
    }
}
