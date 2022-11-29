package com.wcl102.villagermarkers.client;

import com.wcl102.villagermarkers.client.resource.MarkerResource;
import com.wcl102.villagermarkers.client.resource.VillagerResource;
import com.wcl102.villagermarkers.network.Network;
import com.wcl102.villagermarkers.network.packets.PacketVillagerData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SideOnly(Side.CLIENT)
public class ClientVillagerManager {
    public static final Map<String, MarkerResource> resourceCache = new HashMap<>();
    public static final Map<UUID, VillagerResource> villagers = new HashMap<>();


    // Ask the server for VillagerResource
    public static VillagerResource getVillagerResource(EntityVillager villager) {
        if (!villagers.containsKey(villager.getUniqueID())) {
            Network.INSTANCE.sendToServer(new PacketVillagerData.Message(villager.getUniqueID()));
        }
        return villagers.get(villager.getUniqueID());
    }

    public static void add(UUID uuid, VillagerResource resource) {
        villagers.put(uuid, resource);
    }

    public static void remove(Entity entity) {
        villagers.remove(entity.getUniqueID());
    }

    // Markers
    public static MarkerResource getMarker(String key) {
        return resourceCache.get(key);
    }

    public static void addMarker(String hash, MarkerResource resource) {
        resourceCache.putIfAbsent(hash, resource);
    }

    public static boolean hasMarker(String hash) {
        return resourceCache.containsKey(hash);
    }
}
