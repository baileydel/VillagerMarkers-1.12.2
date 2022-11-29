package com.wcl102.villagermarkers.client.render;

import com.wcl102.villagermarkers.VillagerMarkersConfig;
import com.wcl102.villagermarkers.client.resource.MarkerResource;
import com.wcl102.villagermarkers.client.resource.VillagerResource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderLivingEvent;
import org.lwjgl.opengl.GL11;

public class Markers {
    public static void renderMarker(RenderLivingEvent.Specials.Post<? extends EntityVillager> event, VillagerResource resource) {
        Entity entity = event.getEntity();
        RenderManager renderManager = event.getRenderer().getRenderManager();

        double x = event.getX();
        double y = event.getY() + entity.height + 0.1;
        double z = event.getZ();

        float viewerYaw = renderManager.playerViewY;
        float viewerPitch = renderManager.playerViewX;

        renderMarker(resource, x, y, z, viewerYaw, viewerPitch);
    }

    public static void renderMarker(VillagerResource resource, double x, double y, double z, float viewerYaw, float viewerPitch) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(viewerPitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-0.025F, -0.025F, 0.025F);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableLighting();

        float alpha = 1.0F;

        // Fade closer you get to villager
        if (VillagerMarkersConfig.fadePercent > 0) {
            double distance = MathHelper.sqrt(x * x + y * y + z * z);
            double startFade = (1.0 - (VillagerMarkersConfig.fadePercent / 100.0));

            alpha = (float) MathHelper.clamp((distance - startFade) / (VillagerMarkersConfig.maxDistance / 2 - startFade), 0.2, 1);
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);

        // Render through walls
        if (VillagerMarkersConfig.showThroughWalls) {
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);

            renderMarker(resource);

            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
        }

        renderMarker(resource);

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    private static void renderMarker(VillagerResource resource) {
        GlStateManager.pushMatrix();

        double scale = VillagerMarkersConfig.scale;
        boolean showArrow = VillagerMarkersConfig.showArrow;
        int yOff = -18 - VillagerMarkersConfig.verticalOffset;
        int iy = showArrow ? yOff - 9 : yOff;

        GlStateManager.scale(scale, scale, scale);

        renderIcon(resource.getMarker().texture, -8, iy);
        renderOverlay(resource.getMarker(), -8, iy);

        if (showArrow) {
            renderArrow(0, yOff);
        }
        GlStateManager.popMatrix();
    }

    private static void renderIcon(ResourceLocation icon, int x, int y) {
        render(icon, x, y, 16, 16, 0, 1, 0, 1);
    }

    private static void renderOverlay(MarkerResource resource, int x, int y) {
        if (resource.overlay == MarkerResource.OverlayType.NONE) {
            return;
        }

        int dx = 8;
        int dy = 8;
        int width = 8;
        int height = 8;
        int sx = 0;
        int sy = 0;

        float imageSize = 32.0F;
        ResourceLocation location = MarkerResource.NUMBER_OVERLAY ;

        if (resource.overlay == MarkerResource.OverlayType.LEVEL) {
            int processedDigits = resource.level;
            int xOffset = 8;

            while (processedDigits > 0) {
                int currentDigit = processedDigits % 10;
                dx = xOffset;
                sx = (currentDigit % 4) * 8;
                sy = (currentDigit / 4) * 8;

                processedDigits /= 10;
                xOffset -= 5;
            }
        }
        else {
            sx = (resource.level % 2) * 8;
            sy = (resource.level / 2) * 8;
            imageSize = 16.0F;
            location = MarkerResource.ICON_OVERLAY;
        }

        GlStateManager.translate(0, 0, -1);

        //Render overlay
        render(location, x + dx, y + dy, width, height, sx / imageSize, (sx + width) / imageSize, sy / imageSize, (sy + height) / imageSize);
    }

    public static void renderArrow(int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(MarkerResource.MARKER_ARROW);
        Gui.drawModalRectWithCustomSizedTexture(x - 8, y + 8, 0, 0, 16, 8, 16, 8);
        GlStateManager.popMatrix();
    }

    //TODO remove this and use default mc one
    public static void render(ResourceLocation icon, int x, int y, int w, int h, float u0, float u1, float v0, float v1) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(icon);

        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos((float)x,			(float)(y + h),	0).tex(u0, v1).endVertex();
        bufferbuilder.pos((float)(x + w),	(float)(y + h),	0).tex(u1, v1).endVertex();
        bufferbuilder.pos((float)(x + w),	(float)y,		0).tex(u1, v0).endVertex();
        bufferbuilder.pos((float)x,			(float)y,		0).tex(u0, v0).endVertex();
        Tessellator.getInstance().draw();
    }
}
