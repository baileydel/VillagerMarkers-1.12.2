package com.wcl102.villagermarkers;

import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.util.Map;

@MCVersion(ForgeVersion.mcVersion)
public class VillagerMarkersLoadingPlugin implements IFMLLoadingPlugin {

	public VillagerMarkersLoadingPlugin() {
		MixinBootstrap.init();
		Mixins.addConfiguration("mixins." + VillagerMarkers.MODID + ".json");
	}

	@Override
	public String[] getASMTransformerClass() { return new String[0]; }

	@Override
	public String getModContainerClass() { return null; }

	@Nullable
	@Override
	public String getSetupClass() { return null; }

	@Override
	public void injectData(Map<String,Object> data) {}

	@Override
	public String getAccessTransformerClass() { return null; }
}
