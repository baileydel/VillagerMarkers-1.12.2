package com.wcl102.villagermarkers.client.resource;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@SideOnly(Side.CLIENT)
public class MarkerResource {
    public static final ResourceLocation DEFAULT_ICON = new ResourceLocation("textures/entity/villager/default.png");
    public static final ResourceLocation MARKER_ARROW = new ResourceLocation("textures/entity/villager/arrow.png");
    public static final ResourceLocation ICON_OVERLAY = new ResourceLocation("textures/entity/villager/overlay.png");
    public static final ResourceLocation NUMBER_OVERLAY = new ResourceLocation("textures/entity/villager/numbers.png");

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
        NONE(0),
        ICON(1),
        LEVEL(2);

        private final int value;

        OverlayType(int value) {
            this.value = value;
        }

        public static Optional<OverlayType> fromValue(int value) {
            return Arrays.stream(values()).filter(v -> v.value == value).findFirst();
        }
    }
}
