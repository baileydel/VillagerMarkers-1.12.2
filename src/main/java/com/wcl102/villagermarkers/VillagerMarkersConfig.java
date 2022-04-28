package com.wcl102.villagermarkers;

import net.minecraftforge.common.config.Config;

import java.util.Arrays;
import java.util.List;

@Config(modid = VillagerMarkers.MODID, name = "villager-markers")
public class VillagerMarkersConfig {

    public static String[] blacklist = new String[] {
        "nitwit"
    };

    //TODO
    public static boolean showThroughwalls = true;

    public static boolean showArrow = true;

    public static int overlayIndex = 4;

    public static double maxDistance = 64;

    public static double fadePercent = 25;

    public static int verticalOffset = 0;


    public static List<String> getBlackList() {
        return Arrays.asList(blacklist);
    }
}
