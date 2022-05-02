package com.wcl102.villagermarkers.render;

import com.wcl102.villagermarkers.VillagerMarkersConfig;
import com.wcl102.villagermarkers.packet.PacketHandler;
import com.wcl102.villagermarkers.packet.PacketVillagerData;
import com.wcl102.villagermarkers.resource.MarkerResource;
import com.wcl102.villagermarkers.resource.VillagerResource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.UUID;

public class Markers {
    public static final HashMap<UUID, VillagerResource> villagers = new HashMap<>();

    public static final ResourceLocation MARKER_ARROW = new ResourceLocation("textures/entity/villager/arrow.png");
    public static final ResourceLocation ICON_OVERLAY = new ResourceLocation("textures/entity/villager/overlay.png");
    public static final ResourceLocation NUMBER_OVERLAY = new ResourceLocation("textures/entity/villager/numbers.png");
    //TODO if can't find resource to villager default to this
    public static final ResourceLocation DEFAULT_ICON = new ResourceLocation("textures/entity/villager/default.png");

    private static final Frustum frustum = new Frustum();

    static Minecraft mc = Minecraft.getMinecraft();

    public static void renderMarker(EntityVillager entity, float partialTicks) {
        Entity cameraEntity = mc.getRenderViewEntity();

        assert cameraEntity != null;

        if (entity.getHealth() <= 0) {
            villagers.remove(entity.getUniqueID());
            return;
        }

        if (!entity.canEntityBeSeen(cameraEntity) && !VillagerMarkersConfig.showThroughWalls) {
            return;
        }

        float distance = entity.getDistance(cameraEntity);
        double maxDistance = VillagerMarkersConfig.maxDistance;

        if (distance > maxDistance) {
            villagers.remove(entity.getUniqueID());
            return;
        }

        VillagerResource resource = getVillagerResource(entity);

        if (resource != null) {
            if (VillagerMarkersConfig.isBlackListed(resource.getCareerName())) {
                return;
            }

            RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
            double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks;
            double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks;
            double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks;

            frustum.setPosition(x, y, z);

            if (!frustum.isBoundingBoxInFrustum(entity.getEntityBoundingBox())) {
                return;
            }

            GlStateManager.pushMatrix();

            GlStateManager.translate(
                    (float) (x - renderManager.viewerPosX),
                    (float) (y - renderManager.viewerPosY + (entity.height + 0.5F)),
                    (float) (z - renderManager.viewerPosZ)
            );

            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
            GlStateManager.scale(-0.025F, -0.025F, 0.025F);

            double fadePercent = VillagerMarkersConfig.fadePercent;
            double currentAlpha = 1.0;

            if (fadePercent < 100.0) {
                double startFade = ((1.0 - (fadePercent / 100.0)) * maxDistance);
                currentAlpha = MathHelper.clamp(1.0 - ((distance - startFade) / (maxDistance - startFade)), 0.0, 1.0);
            }

            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.3F * (float)currentAlpha);

            int yPos = -18 - VillagerMarkersConfig.verticalOffset;

            boolean showArrow = VillagerMarkersConfig.showArrow;

            if (showArrow) {
                renderArrow(0, yPos);
            }

            renderMarker(resource.getMarker(), -8, showArrow ? yPos - 9 : yPos);

            GlStateManager.disableBlend();
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);

            renderMarker(resource.getMarker(), -8, showArrow ? yPos - 9 : yPos);

            if (showArrow) {
                renderArrow(0, yPos);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
    }

    public static void renderMarker(MarkerResource resource, int x, int y) {
        GlStateManager.pushMatrix();

        double scale = VillagerMarkersConfig.scale;
        GlStateManager.scale(scale, scale, scale);

        renderIcon(resource.texture, x, y);

        renderOverlay(resource, (dx, dy, width, height, sx, sy) -> {
            GlStateManager.translate(0, 0, -1);

            float imageSize = resource.overlay == MarkerResource.OverlayType.LEVEL ? 32.0F : 16.0F;

            renderIcon(resource.overlay == MarkerResource.OverlayType.LEVEL ? NUMBER_OVERLAY : ICON_OVERLAY, x + dx, y + dy, width, height, sx / imageSize, (sx + width) / imageSize, sy / imageSize, (sy + height) / imageSize);
        });

        GlStateManager.popMatrix();
    }

    @FunctionalInterface
    public interface OverlayRendererMethod {
        void accept(int dx, int dy, int width, int height, int sx, int sy);
    }

    public static void renderOverlay(MarkerResource resource, OverlayRendererMethod method) {
        if (resource.overlay == MarkerResource.OverlayType.LEVEL) {
            renderOverlayLevel(resource, method);
        }
        else if (resource.overlay != MarkerResource.OverlayType.NONE) {
            renderOverlayIcon(resource, method);
        }
    }

    private static void renderOverlayLevel(MarkerResource resource, OverlayRendererMethod method) {
        int processedDigits = resource.level;
        int xOffset = 8;

        // If the overlay is set to "profession level" and this marker has a level to show, add every digit needed.
        // Even though vanilla only supports a max level of 5, this should support any profession level.
        while (processedDigits > 0) {
            int currentDigit = processedDigits % 10;
            method.accept(xOffset, 8, 8, 8, (currentDigit % 4) * 8, (currentDigit / 4) * 8);
            processedDigits /= 10;
            xOffset -= 5;
        }
    }

    private static void renderOverlayIcon(MarkerResource resource, OverlayRendererMethod method) {
        method.accept(8, 8, 8, 8, (resource.overlay.value() % 2) * 8, (resource.overlay.value() / 2) * 8);
    }

    private static void renderIcon(ResourceLocation icon, int x, int y) {
        renderIcon(icon, x, y, 16, 16, 0, 1, 0, 1);
    }

    public static void renderIcon(ResourceLocation icon, int x, int y, int w, int h, float u0, float u1, float v0, float v1) {
        mc.getTextureManager().bindTexture(icon);

        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();

        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos((float)x,			(float)(y + h),	0).tex(u0, v1).endVertex();
        bufferbuilder.pos((float)(x + w),	(float)(y + h),	0).tex(u1, v1).endVertex();
        bufferbuilder.pos((float)(x + w),	(float)y,		0).tex(u1, v0).endVertex();
        bufferbuilder.pos((float)x,			(float)y,		0).tex(u0, v0).endVertex();
        Tessellator.getInstance().draw();
    }

    public static void renderArrow(int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(MARKER_ARROW);
        Gui.drawModalRectWithCustomSizedTexture(x - 8, y + 8, 0, 0, 16, 8, 16, 8);
        GlStateManager.popMatrix();
    }

    public static VillagerResource getVillagerResource(EntityVillager villager) {
        if (!villagers.containsKey(villager.getUniqueID())) {
            PacketHandler.INSTANCE.sendToServer(new PacketVillagerData.Message(villager.getUniqueID()));
        }
        return villagers.get(villager.getUniqueID());
    }
}
