package ninja.genuine.tooltips;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLModDisabledEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ninja.genuine.tooltips.client.RenderEvent;

@Mod(modid = WorldTooltips.MODID, name = WorldTooltips.NAME, version = WorldTooltips.VERSION, canBeDeactivated = true, clientSideOnly = true, updateJSON = WorldTooltips.URL
		+ "update.json", useMetadata = true, guiFactory = "ninja.genuine.tooltips.client.TooltipsGuiFactory")
public class WorldTooltips {

	@Instance(WorldTooltips.MODID)
	public static WorldTooltips instance;
	public static Configuration config;
	public static final String MODID = "worldtooltips";
	public static final String NAME = "World Tooltips";
	public static final String URL = "http://genuine.ninja/world-tooltips/";
	public static final String VERSION = "1.2.1";
	public static final String DESC = "This is a color in hex form (ie: 0xAB12cd or #AB12cd), one can always lookup your favorite colors online.";
	public static int colorPrimary, colorOutline;
	public static float alpha;
	public RenderEvent re;
	// private final Pattern pattern = Pattern.compile("^(0[xX]|#)[0-9a-fA-F]{1,8}$");

	public WorldTooltips() {
		instance = this;
	}

	@EventHandler
	public void pre(FMLPreInitializationEvent event) {
		config = new Configuration(event.getSuggestedConfigurationFile());
		syncConfig();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		re = new RenderEvent();
		MinecraftForge.EVENT_BUS.register(re);
	}

	@EventHandler
	public void post(FMLPostInitializationEvent event) {
		re.post();
	}

	@EventHandler
	public void disable(FMLModDisabledEvent event) {
		MinecraftForge.EVENT_BUS.unregister(re);
		re.disable();
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
		if (eventArgs.getModID().equals(MODID))
			syncConfig();
	}

	private void syncConfig() {
		try {
			colorPrimary = Integer.decode(config.get("Appearance", "primary", "0x100010", DESC, Type.COLOR).getString());
		} catch (NumberFormatException e) {
			colorPrimary = 0x100010;
		}
		try {
			colorOutline = Integer.decode(config.get("Appearance", "outline", "0x5000FF", DESC, Type.COLOR).getString());
		} catch (NumberFormatException e) {
			colorOutline = 0x5000FF;
		}
		alpha = config.getFloat("transparency", "Appearance", 0.85F, 0.0F, 1.0F, "Set the opacity for the tooltips; 0 being completely invisible and 1 being completely opaque.");
		if (config.hasChanged())
			config.save();
	}
}
