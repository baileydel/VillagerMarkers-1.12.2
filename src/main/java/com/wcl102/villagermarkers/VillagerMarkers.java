package com.wcl102.villagermarkers;

import com.wcl102.villagermarkers.packet.PacketHandler;
import com.wcl102.villagermarkers.render.Markers;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = VillagerMarkers.MODID, name = VillagerMarkers.NAME, version = VillagerMarkers.VERSION)
@Mod.EventBusSubscriber(modid = VillagerMarkers.MODID)
public class VillagerMarkers {
    public static final String MODID = "villagermarkers";
    public static final String NAME = "Villager Markers";
    public static final String VERSION = "1.0";

    @Mod.EventHandler
    public static void start(FMLInitializationEvent event) {
        PacketHandler.init();
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void RenderWorldLast(RenderWorldLastEvent event) {
        try {
            for (Entity e : Minecraft.getMinecraft().player.world.getLoadedEntityList()) {
                if (e instanceof EntityVillager) {
                    Markers.renderMarker((EntityVillager) e, event.getPartialTicks());
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
