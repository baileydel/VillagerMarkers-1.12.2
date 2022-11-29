package com.wcl102.villagermarkers.network;

import com.wcl102.villagermarkers.VillagerMarkers;
import com.wcl102.villagermarkers.network.packets.PacketVillagerData;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class Network {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(VillagerMarkers.MODID);

    public static void init() {
        registerMessage(PacketVillagerData.class, PacketVillagerData.Message.class);
    }

    private static void registerMessage(Class packet, Class message) {
        INSTANCE.registerMessage(packet, message, 0, Side.CLIENT);
        INSTANCE.registerMessage(packet, message, 0, Side.SERVER);
    }
}
