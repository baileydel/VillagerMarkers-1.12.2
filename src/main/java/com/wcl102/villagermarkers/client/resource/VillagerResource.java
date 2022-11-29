package com.wcl102.villagermarkers.client.resource;

import com.wcl102.villagermarkers.VillagerMarkersConfig;
import com.wcl102.villagermarkers.client.ClientVillagerManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

public class VillagerResource {
    private final String careerName;
    private final int level;

    public VillagerResource(EntityVillager villager) {
        NBTTagCompound nbt = villager.writeToNBT(new NBTTagCompound());
        int career = nbt.getInteger("Career") - 1;

        this.careerName = villager.getProfessionForge().getCareer(career).getName();
        this.level = nbt.getInteger("CareerLevel");
    }

    public VillagerResource(NBTTagCompound tag) {
        this.careerName = tag.getString("Career");
        this.level = tag.getInteger("CareerLevel");
    }

    @SideOnly(Side.CLIENT)
    public MarkerResource getMarker() {
        String key = this.careerName + "-" + this.level;

        // Add Resource if it doesn't exist
        if (!ClientVillagerManager.hasMarker(key)) {
            // Get Profession Icon
            ResourceLocation resource = new ResourceLocation(String.format("textures/entity/villager/markers/%s.png", this.careerName));

            // Get the Overlay Type
            MarkerResource.OverlayType overlayType = MarkerResource.OverlayType
                    .fromValue(VillagerMarkersConfig.overlayIndex)
                    .orElse(MarkerResource.OverlayType.NONE);

            // See if Minecraft Resources has the texture for the profession
            try {
                Minecraft.getMinecraft().getResourceManager().getResource(resource);
            }
            catch (IOException e) {
                // Set resource to default if it doesn't exist
                resource = MarkerResource.DEFAULT_ICON;
                e.printStackTrace();
            }
            ClientVillagerManager.addMarker(key, new MarkerResource(resource, overlayType, this.level));
        }
        return ClientVillagerManager.getMarker(key);
    }

    public String getCareerName() {
        return this.careerName;
    }

    public int getLevel() {
        return level;
    }
}
