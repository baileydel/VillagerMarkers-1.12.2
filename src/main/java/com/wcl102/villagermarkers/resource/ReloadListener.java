package com.wcl102.villagermarkers.resource;

import com.wcl102.villagermarkers.render.Markers;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;

public class ReloadListener implements IResourceManagerReloadListener {
    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        System.out.println("Clearing Villager Cache");
        Markers.villagers.clear();
    }
}
