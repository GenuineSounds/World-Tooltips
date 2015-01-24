package com.genuineminecraft.tooltips;

import java.util.regex.Pattern;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import com.genuineminecraft.tooltips.system.TooltipSystem;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Tooltips.MODID, name = Tooltips.NAME, version = Tooltips.VERSION)
public class Tooltips {

	@Instance(Tooltips.MODID)
	public static Tooltips instance;
	public static final String MODID = "WorldTooltips";
	public static final String NAME = "World Tooltips";
	public static final String VERSION = "1.7.10-1.0.5";
	public static final String DESC = "This is a color in hex form (ie: 0xAB12cd or #AB12cd), one can always lookup your favorite colors online.";
	public static String color1, color2;
	private Pattern pattern = Pattern.compile("^(0[xX]|#)[0-9a-fA-F]{1,8}$");

	@EventHandler
	public void pre(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		Tooltips.color1 = config.getString("background", "Colors", "0x100010", Tooltips.DESC, pattern);
		Tooltips.color2 = config.getString("outline", "Colors", "0x5000FF", Tooltips.DESC, pattern);
		config.save();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new TooltipSystem());
	}
}
