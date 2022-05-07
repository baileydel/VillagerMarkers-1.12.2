package com.wcl102.villagermarkers.resource;

import com.wcl102.villagermarkers.VillagerMarkersConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VillagerResource {
    public static final Map<String, MarkerResource> resourceCache = new HashMap<>();

    public static final ResourceLocation DEFAULT_ICON = new ResourceLocation("textures/entity/villager/default.png");

    private final String careerName;
    private final int level;

    private MarkerResource marker;

    public VillagerResource(NBTTagCompound tag) {
        this.careerName = tag.getString("CareerName");
        this.level = tag.getInteger("CareerLevel");
        this.marker = w();
    }

    public MarkerResource getMarker() {
        return marker;
    }

    private MarkerResource w() {
        String resourceKey = String.format("%s-%d", this.careerName, this.level);

        if (resourceCache.containsKey(resourceKey)) {
            return resourceCache.get(resourceKey);
        }

        MarkerResource.OverlayType overlayType = MarkerResource.OverlayType.fromValue(VillagerMarkersConfig.overlayIndex).orElse(MarkerResource.OverlayType.NONE);

        ResourceLocation resource = new ResourceLocation(String.format("textures/entity/villager/markers/%s.png", this.careerName));

        boolean found = true;

        try {
            Minecraft.getMinecraft().getResourceManager().getAllResources(resource);
        }
        catch (IOException e) {
            e.printStackTrace();
            found = false;
        }

        marker = new MarkerResource(found ? resource : DEFAULT_ICON, overlayType, this.level);

        resourceCache.put(resourceKey, marker);
        return marker;
    }

    public String getCareerName() {
        return this.careerName;
    }
}
