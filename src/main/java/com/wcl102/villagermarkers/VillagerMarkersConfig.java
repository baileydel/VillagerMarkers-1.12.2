package com.wcl102.villagermarkers;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.List;

//TODO make cool config screen
//TODO use lang keys
//TODO comments
@Config(modid = VillagerMarkers.MODID, name = "villager-markers")
@Mod.EventBusSubscriber(modid = VillagerMarkers.MODID)
public class VillagerMarkersConfig {

    public static String[] blacklist = new String[] {
        "nitwit"
    };

    public static boolean showThroughWalls = true;

    public static boolean showArrow = true;

    @Config.SlidingOption
    @Config.RangeDouble(min = 16, max = 128)
    public static double maxDistance = 64;

    //Put this as drop-down in config screen.
    public static int overlayIndex = 4;
    @Config.SlidingOption
    @Config.RangeDouble(min = 5, max = 100)
    public static double fadePercent = 25;

    @Config.SlidingOption
    @Config.RangeDouble(min = 0.1, max = 2.0)
    public static double scale = 1;

    @Config.SlidingOption
    @Config.RangeInt(min = -128, max = 128)
    public static int verticalOffset = 0;

    private static List<String> getBlackList() {
        return Arrays.asList(blacklist);
    }

    //TODO if it equals core name or translated name
    public static boolean isBlackListed(String career) {
        if (getBlackList().contains(career)) {
            return true;
        }

        for (int i = 0; i < getBlackList().size(); i++) {
            if (getBlackList().get(i).contains(career)) {
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onConfigChange(ConfigChangedEvent event) {
        if (event.getModID().equals(VillagerMarkers.MODID)) {
            ConfigManager.sync(VillagerMarkers.MODID, Config.Type.INSTANCE);
        }
    }
}
