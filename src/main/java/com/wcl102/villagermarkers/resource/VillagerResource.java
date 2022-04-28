package com.wcl102.villagermarkers.resource;

import com.wcl102.villagermarkers.VillagerMarkersConfig;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

//TODO Move marker resource, any villager can use any marker resource..
public class VillagerResource {
    public static final Map<String, MarkerResource> resourceCache = new HashMap<>();

    private String profession;
    private final String careerName;
    private final int career;
    private int level;

    private MarkerResource marker;

    public VillagerResource(NBTTagCompound tag) {
        this.profession = tag.getString("Profession");
        this.careerName = tag.getString("CareerName");
        this.career = tag.getInteger("Career");
        this.level = tag.getInteger("CareerLevel");
    }

    public MarkerResource getMarker() {
        String resourceKey = String.format("%s-%d", this.careerName, this.level);

        // Returned the cached value, if there is one.
        if (resourceCache.containsKey(resourceKey)) {
            return resourceCache.get(resourceKey);
        }

        MarkerResource marker = null;
        MarkerResource.OverlayType overlayType = MarkerResource.OverlayType.fromValue(VillagerMarkersConfig.overlayIndex).orElse(MarkerResource.OverlayType.NONE);

        //TODO the rest of this


        if (marker == null) {
            ResourceLocation resource = new ResourceLocation(String.format("textures/entity/villager/markers/%s.png", this.careerName));
            marker = new MarkerResource(resource, overlayType, this.level);
        }

        resourceCache.put(resourceKey, marker);
        return marker;
    }

    public String getCareerName() {
        return this.careerName;
    }

    public int getCareer() {
        return this.career;
    }

    public int getCareerlevel() {
        return this.level;
    }
}
