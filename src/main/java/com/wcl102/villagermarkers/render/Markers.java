package com.wcl102.villagermarkers.render;

import com.wcl102.villagermarkers.VillagerMarkersConfig;
import com.wcl102.villagermarkers.packet.PacketHandler;
import com.wcl102.villagermarkers.packet.PacketVillagerData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class Markers {
    public static final HashMap<UUID, PacketVillagerData.Message> villagers = new HashMap<>();
    public static final HashMap<String, ResourceLocation> resources = new HashMap<>();

    public static final ResourceLocation MARKER_ARROW =  new ResourceLocation("textures/entity/villager/arrow.png");

    //TODO MOVE THIS
    static Minecraft mc = Minecraft.getMinecraft();

    public static void renderMarker(EntityVillager entity, float partialTicks) {
        PacketVillagerData.Message data = getVillagerData(entity);

        if (entity.getHealth() <= 0) {
            //villagers.remove(entity.getUniqueID());
            return;
        }

        if (VillagerMarkersConfig.getBlackList().contains(data.getCareerName())) {
            return;
        }

        float distance = entity.getDistance(Objects.requireNonNull(mc.getRenderViewEntity()));

        double maxDistance = VillagerMarkersConfig.maxDistance;

        if (distance > maxDistance) {
            //villagers.remove(entity.getUniqueID());
            return;
        }

        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks;

        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();

        GlStateManager.pushMatrix();

        GlStateManager.translate((float) (x - renderManager.viewerPosX), (float) (y - renderManager.viewerPosY + (entity.height + 0.5F)), (float) (z -renderManager.viewerPosZ));

        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

        GlStateManager.scale(-0.025F, -0.025F, 0.025F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        int y2 = -18 - VillagerMarkersConfig.verticalOffset;

        boolean showArrow = VillagerMarkersConfig.showArrow;

        if (showArrow) {
            Markers.renderArrow(0, y2);
        }

        Markers.renderIcon(data,-8, y2 - 10, 16, 16, 0, 1, 0, 1);

        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    public static void renderIcon(PacketVillagerData.Message data, int x, int y, int w, int h, float u0, float u1, float v0, float v1) {
        mc.getTextureManager().bindTexture(getResource(data));

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

    private static ResourceLocation getResource(PacketVillagerData.Message data) {
        ResourceLocation resource = resources.get(data.getCareerName());

        if (resource == null) {
            resource = new ResourceLocation("textures/entity/villager/markers/" + data.getCareerName() + ".png");

            resources.put(data.getCareerName(), resource);
        }
        return resource;
    }

    public static PacketVillagerData.Message getVillagerData(EntityVillager villager) {
        PacketVillagerData.Message data = villagers.get(villager.getUniqueID());

        if (data == null) {
            PacketHandler.INSTANCE.sendToServer(new PacketVillagerData.Message(villager.getUniqueID()));
            data = villagers.get(villager.getUniqueID());
            System.out.println("adding villager");
        }
        return data;
    }
}
