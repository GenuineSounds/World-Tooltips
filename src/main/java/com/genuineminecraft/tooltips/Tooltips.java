package com.genuineminecraft.tooltips;

import java.util.regex.Pattern;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import com.genuineminecraft.tooltips.events.GlobalEvents;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Tooltips.MODID, name = Tooltips.NAME, version = Tooltips.VERSION)
public class Tooltips {

	@Instance("Tooltips")
	public static Tooltips instance;
	public static final String MODID = "Tooltips";
	public static final String NAME = "Tooltips";
	public static final String VERSION = "1.3";
	public static final String DESC = "This is a color in hex form (ie: 0xab12CD or #ab12CD), one can always lookup your favorite colors online.";
	public static String color1, color2;
	private boolean enabled = true;
	private Pattern pattern = Pattern.compile("((0[xX])|(#))[0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F]");

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		this.instance = this;
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		color1 = config.getString("background", "Colors", "0x100010", DESC, pattern);
		color2 = config.getString("outline", "Colors", "0x5000FF", DESC, pattern);
		enabled = config.getBoolean("enable", "Options", true, "Set to false to disable this mod completely");
		config.save();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		if (enabled)
			MinecraftForge.EVENT_BUS.register(new GlobalEvents());
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {}
}
