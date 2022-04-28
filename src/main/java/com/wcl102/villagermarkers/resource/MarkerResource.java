package com.wcl102.villagermarkers.resource;

import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class MarkerResource {
    public final ResourceLocation texture;
    public final OverlayType overlay;
    public final int level;

    public MarkerResource(ResourceLocation texture, OverlayType overlay, int level) {
        this.texture = texture;
        this.overlay = overlay;
        this.level = level;
    }

    @Override
    public int hashCode() {
        return Objects.hash(texture, overlay, level);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        else if (!(obj instanceof MarkerResource)) {
            return false;
        }
        else {
            MarkerResource other = (MarkerResource) obj;
            return Objects.equals(texture, other.texture) &&
                    Objects.equals(overlay, other.overlay) &&
                    Objects.equals(level, other.level);
        }
    }

    public enum OverlayType {
        NONE(-1),
        BACKPACK(0),
        EMERALD(1),
        COINS(2),
        BAG(3),
        LEVEL(4);

        private final int value;

        private OverlayType(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }

        public static Optional<OverlayType> fromValue(int value) {
            return Arrays.stream(values()).filter(v -> v.value == value).findFirst();
        }
    }
}
