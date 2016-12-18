package ninja.genuine.tooltips;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLModDisabledEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property.Type;
import ninja.genuine.tooltips.client.RenderEvent;
import ninja.genuine.tooltips.system.Tooltip;

@Mod(modid = WorldTooltips.MODID, name = WorldTooltips.NAME, version = WorldTooltips.VERSION, canBeDeactivated = true, useMetadata = true, guiFactory = "ninja.genuine.tooltips.client.TooltipsGuiFactory")
public class WorldTooltips {

	@Instance(WorldTooltips.MODID)
	public static WorldTooltips instance;
	public static Configuration config;
	public static final String MODID = "world-tooltips";
	public static final String NAME = "World-Tooltips";
	public static final String URL = "http://genuine.ninja/world-tooltips/";
	public static final String VERSION = "1.2.3";
	public static final String DESC = "Choose a color in hexidecimal (ie: 0xAB12cd or #AB12cd) \nYou can look up your favorite colors online.";
	public static final String GUIID = "worldtooltipsgui";
	public static int colorBackground, overrideOutlineColor;
	public static float alpha;
	public static boolean hideModName, overrideOutline;
	private static boolean enabled = false;
	public RenderEvent events;

	public WorldTooltips() {
		instance = this;
	}

	@EventHandler
	public void pre(FMLPreInitializationEvent event) {
		config = new Configuration(event.getSuggestedConfigurationFile());
		enabled = config.get("Appearance", "enabled", true, "Enable rendering the tooltips.").getBoolean();
		syncConfig();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		events = new RenderEvent();
		Tooltip.init();
		if (enabled)
			enable();
		FMLCommonHandler.instance().bus().register(this);
	}

	@EventHandler
	public void post(FMLPostInitializationEvent event) {
		events.post();
	}

	public void enable() {
		MinecraftForge.EVENT_BUS.register(events);
		enabled = true;
	}

	@EventHandler
	public void disable(FMLModDisabledEvent event) {
		MinecraftForge.EVENT_BUS.unregister(events);
		enabled = false;
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.modID.equals(MODID)) {
			if (event.configID.equals(GUIID)) {
				boolean tmp = enabled;
				enabled = config.get("Appearance", "enabled", true, "Enable rendering the tooltips.").getBoolean();
				if (tmp != enabled) {
					if (enabled)
						enable();
					else
						disable(null);
				}
				syncConfig();
				events.syncColors();
			}
		}
	}

	private void syncConfig() {
		hideModName = config.getBoolean("hide_mod_name", "Appearance", false, "Hide mod names on tooltips.");
		try {
			colorBackground = Integer.decode(config.get("Appearance", "background", "0x100010", DESC, Type.COLOR).getString());
		} catch (NumberFormatException e) {
			colorBackground = 0x100010;
		}
		overrideOutline = config.getBoolean("override_outline", "Appearance", false, "If enabled outline color will be override_outline_color instead of default behavior.");
		try {
			overrideOutlineColor = Integer.decode(config.get("Appearance", "override_outline_color", "0x5000FF", DESC, Type.COLOR).getString());
		} catch (NumberFormatException e) {
			overrideOutlineColor = 0x5000FF;
		}
		alpha = config.getFloat("transparency", "Appearance", 0.85F, 0.0F, 1.0F, "Set the opacity for the tooltips; 0 being completely invisible and 1 being completely opaque.");
		config.save();
	}
}
