package com.wcl102.villagermarkers;

import com.wcl102.villagermarkers.client.ClientVillagerManager;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.List;

@Config(modid = VillagerMarkers.MODID, name = "villager-markers")
@Mod.EventBusSubscriber(modid = VillagerMarkers.MODID)
public class VillagerMarkersConfig {

    @Config.Name("Blacklist")
    public static String[] blacklist = new String[] {
        "nitwit"
    };

    @Config.Name("Show Through Walls")
    public static boolean showThroughWalls = true;

    @Config.Name("Show Arrow")
    public static boolean showArrow = true;

    @Config.Name("Max Distance")
    @Config.SlidingOption
    @Config.RangeDouble(min = 16, max = 64)
    public static double maxDistance = 64;

    @Config.Comment({
            "Overlay Types",
            "0 - None",
            "1 - Icons",
            "2 - Level",
    })
    @Config.Name("Overlay Type")
    @Config.SlidingOption
    @Config.RangeInt(min = 0, max = 2)
    public static int overlayIndex = 2;

    @Config.Comment("0% - No Fading")
    @Config.Name("Fade %")
    @Config.SlidingOption
    @Config.RangeDouble(min = 0, max = 100)
    public static double fadePercent = 25;

    @Config.Name("Scale")
    @Config.SlidingOption
    @Config.RangeDouble(min = 0.1, max = 2.0)
    public static double scale = 1;

    @Config.Name("Vertical Offset")
    @Config.SlidingOption
    @Config.RangeInt(min = 0, max = 64)
    public static int verticalOffset = 0;

    private static List<String> getBlackList() {
        return Arrays.asList(blacklist);
    }

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

            ClientVillagerManager.villagers.clear();
            ClientVillagerManager.resourceCache.clear();

            ConfigManager.sync(VillagerMarkers.MODID, Config.Type.INSTANCE);
        }
    }
}
