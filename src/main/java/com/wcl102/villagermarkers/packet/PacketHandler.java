package com.wcl102.villagermarkers.packet;

import com.wcl102.villagermarkers.VillagerMarkers;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(VillagerMarkers.MODID);
    private static int nextPacketId = 0;

    public static void init() {
        registerMessage(PacketVillagerData.class, PacketVillagerData.Message.class);
        registerMessage(PacketVillagerLevelUp.class, PacketVillagerLevelUp.Message.class);
    }

    //TODO use ????? to get rid of warnings
    private static void registerMessage(Class packet, Class message) {
        INSTANCE.registerMessage(packet, message, nextPacketId, Side.CLIENT);
        INSTANCE.registerMessage(packet, message, nextPacketId, Side.SERVER);
        nextPacketId++;
    }
}
