package com.genuineflix.wt;

import java.util.regex.Pattern;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import com.genuineflix.wt.system.TooltipSystem;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = WorldTooltip.MODID, name = WorldTooltip.NAME, version = WorldTooltip.VERSION)
public class WorldTooltip {

	@Instance(WorldTooltip.MODID)
	public static WorldTooltip instance;
	public static final String MODID = "WorldTooltip";
	public static final String NAME = "World Tooltip";
	public static final String VERSION = "1.0.8";
	public static final String DESC = "This is a color in hex form (ie: 0xAB12cd or #AB12cd), one can always lookup your favorite colors online.";
	public static String color1, color2;
	private final Pattern pattern = Pattern.compile("^(0[xX]|#)[0-9a-fA-F]{1,8}$");

	@EventHandler
	public void pre(final FMLPreInitializationEvent event) {
		final Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		WorldTooltip.color1 = config.getString("background", "Colors", "0x100010", WorldTooltip.DESC, pattern);
		WorldTooltip.color2 = config.getString("outline", "Colors", "0x5000FF", WorldTooltip.DESC, pattern);
		config.save();
	}

	@EventHandler
	public void init(final FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new TooltipSystem());
	}
}
