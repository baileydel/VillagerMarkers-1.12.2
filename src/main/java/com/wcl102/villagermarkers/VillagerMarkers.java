package com.wcl102.villagermarkers;

import com.wcl102.villagermarkers.client.ClientVillagerManager;
import com.wcl102.villagermarkers.client.resource.VillagerResource;
import com.wcl102.villagermarkers.network.Network;
import com.wcl102.villagermarkers.client.render.Markers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = VillagerMarkers.MODID, name = VillagerMarkers.NAME, version = VillagerMarkers.VERSION)
public class VillagerMarkers {
    public static final String MODID = "villagermarkers";
    public static final String NAME = "Villager Markers";
    public static final String VERSION = "1.0.5";

    public VillagerMarkers() {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ServerVillagerManager());
    }

    @Mod.EventHandler
    public static void FMLInitialization(FMLInitializationEvent event) {
        Network.init();
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void render(RenderLivingEvent.Specials.Post<? extends EntityVillager> event) {
        Entity entity = event.getEntity();

        if (entity instanceof EntityVillager) {
            float distance = entity.getDistance(event.getRenderer().getRenderManager().renderViewEntity);
            double maxDistance = VillagerMarkersConfig.maxDistance;

            if (distance < maxDistance) {
                VillagerResource resource = ClientVillagerManager.getVillagerResource((EntityVillager) entity);

                if (resource != null) {
                    if (VillagerMarkersConfig.isBlackListed(resource.getCareerName())) {
                        return;
                    }
                    Markers.renderMarker(event, resource);
                }
            }
            else {
                ClientVillagerManager.remove(entity);
            }
        }
    }
}
